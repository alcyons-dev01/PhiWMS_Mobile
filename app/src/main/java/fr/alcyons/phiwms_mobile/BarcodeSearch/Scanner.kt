package fr.alcyons.phiwms_mobile.BarcodeSearch

import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Visibility
import fr.alcyons.phiwms_mobile.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import fr.alcyons.phiwms_mobile.ServiceActivity
import java.util.Locale
import java.util.Locale.getDefault

abstract class Scanner : ServiceActivity() {

    protected lateinit var barcodeView: DecoratedBarcodeView
    protected lateinit var tvCode: TextView
    protected lateinit var btnFlash: ImageButton
    protected lateinit var btnExit: ImageButton
    protected lateinit var btnAlcyons: ImageButton
    protected lateinit var btnActiveSon: ImageButton
    protected lateinit var btnInactiveSon: ImageButton
    protected var isSoundOn = true
    private var flashOn = false
    protected abstract val layoutResId: Int
    private var part1: String? = null
    private var part1Time: Long = 0L
    private val windowMs = 1000L  // délai 1 seconde max entre 2 scans
    var type = ""
    val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)

        barcodeView = findViewById(R.id.barcodeView)
        btnFlash = findViewById(R.id.btnFlash)
        btnExit = findViewById(R.id.btnClose)
        btnAlcyons = findViewById(R.id.btnAlcyons)
        btnActiveSon = findViewById(R.id.btnActiveSon)
        btnInactiveSon = findViewById(R.id.btnInactiveSon)

        if(!isSoundEnabled())
        {
            isSoundOn = false
            btnActiveSon.visibility = View.GONE
            btnInactiveSon.visibility = View.VISIBLE
        }

        btnActiveSon.setOnClickListener {
            isSoundOn = false
            btnInactiveSon.visibility = View.VISIBLE
            btnActiveSon.visibility = View.GONE
        }

        btnInactiveSon.setOnClickListener {
            if(isSoundEnabled())
            {
                isSoundOn = true
                btnInactiveSon.visibility = View.GONE
                btnActiveSon.visibility = View.VISIBLE
            }
        }

        btnFlash.setOnClickListener {
            flashOn = !flashOn
            if (flashOn) barcodeView.setTorchOn() else barcodeView.setTorchOff()
        }

        btnExit.setOnClickListener {
            quitterActivite()
        }

        if(utilisateurConnecte.identifiant.uppercase(getDefault()).contentEquals("ALCYONS"))
        {
            btnAlcyons.setOnClickListener { v ->
                val popup = PopupMenu(this, v)
                popup.menuInflater.inflate(R.menu.alcyons_scan_option, popup.menu)

                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_gs1 -> {
                            onCodeScanned("01076401510872961721123110ABCDEF")
                            true
                        }
                        R.id.action_gs1_part1 -> {
                            onCodeScanned("0107640151087296")
                            true
                        }
                        R.id.action_gs1_part2 -> {
                            onCodeScanned("1721123110ABCDEF")
                            true
                        }
                        R.id.action_hibc -> {
                            onCodeScanned("+M684KTONP51R1/$$3231231ABCDEFE")
                            true
                        }
                        R.id.action_hibc_part1 -> {
                            onCodeScanned("+M684KTONP51R1L")
                            true
                        }
                        R.id.action_hibc_part2 -> {
                            onCodeScanned("+$$3231231ABCDEFLF")
                            true
                        }
                        R.id.action_gtin -> {
                            onCodeScanned("PHITAGTIN:0107640151087296")
                            true
                        }
                        R.id.action_ref -> {
                            onCodeScanned("PHITAGREF:120")
                            true
                        }
                        R.id.action_pvref -> {
                            onCodeScanned("PHITAGPVREF:120")
                            true
                        }
                        R.id.action_place -> {
                            onCodeScanned("PHITAGPLACE+xxx:1203")
                            true
                        }
                        R.id.action_codeinconnu -> {
                            onCodeScanned("codeinconnu")
                            true
                        }
                        else -> false
                    }
                }

                popup.show()
            }
        }
        else
        {
            btnAlcyons.visibility = View.GONE
        }


        val formats = listOf(
            BarcodeFormat.QR_CODE,
            BarcodeFormat.DATA_MATRIX,
            BarcodeFormat.EAN_13,
            BarcodeFormat.EAN_8,
            BarcodeFormat.UPC_A,
            BarcodeFormat.UPC_E,
            BarcodeFormat.CODE_128,
            BarcodeFormat.CODE_39,
            BarcodeFormat.ITF
        )

        barcodeView.setStatusText("")
        barcodeView.translationY = -500f;
        barcodeView.barcodeView.decoderFactory =
            DefaultDecoderFactory(formats, null, null, Intents.Scan.MIXED_SCAN)

        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                if(!isSoundEnabled)
                {
                    isSoundOn = false
                    btnInactiveSon.visibility = View.VISIBLE
                    btnActiveSon.visibility = View.GONE
                }

                val texte = result.text ?: return

                if(texte.uppercase().startsWith("PHITAG") || texte.uppercase().startsWith("PHI"))
                    onCodeScanned(normalize(texte))
                else
                {
                    val assembled = handleSplitScan(texte) ?: return
                    onCodeScanned(assembled)
                }
            }
        })
    }

    private fun normalize(raw: String): String {
        return raw
            .replace("]C1", "")
            .replace("]e0", "")
            .replace("]d2", "")
            .replace("]Q3", "")
            .replace("}C1", "")
            .replace("}d2010", "")
            .replace("]d2010", "")
            .replace("\u001D", "")
            .trim()
    }

    private fun handleSplitScan(raw: String): String? {
        val now = System.currentTimeMillis()
        val code = normalize(raw)

        //code monoligne
        if (isCompleteCode(code)) {
            part1 = null
            part1Time = 0L
            return code
        }

        // si une première partie existe mais trop vieille -> on repart à zéro
        if (part1 != null && (now - part1Time) > windowMs) {
            part1 = null
        }

        // si pas de première partie -> on enregistre
        if (part1 == null || part1.contentEquals(code)) {
            if(!code.startsWith("01") && !code.startsWith("02") && !code.startsWith("+") && code.startsWith("+$")) return null
            if (code.startsWith("01") || code.startsWith("02"))
            {
                type = "GTIN"
            }
            else if(code.startsWith("+"))
            {
                type = "HIBC"
            }
            else
            {
                type = ""
            }
            part1 = code
            part1Time = now
            return null
        }

        // sinon -> on assemble
        var assembled = part1 + code

        if(type.contentEquals("HIBC"))
        {
            val p1 = part1 ?: return null
            assembled = p1.dropLast(1)+"/"+code.substring(1, code.length-2)
        }

        // reset état
        part1 = null
        part1Time = 0L

        return assembled
    }

    private fun looksLikeHibc(s: String): Boolean = s.startsWith("+") && s[1].isLetter() && s.contains("/")
    private fun isCompleteCode(code: String): Boolean {
        val c = code.trim()

        if (c.startsWith("01") && c.length > 16) return true

        if (looksLikeHibc(c)) return true

        return false
    }

    protected open fun onCodeScanned(code: String) {
        // par défaut : rien, les classes filles peuvent surcharger
    }

    protected open fun quitterActivite() {
        // par défaut : rien, les classes filles peuvent surcharger
        val data = Intent().apply {
            //putExtra("result", tvCode.text?.toString().orEmpty())
        }
        setResult(RESULT_OK, data)
        finish()
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        toneGenerator.release()
    }
}