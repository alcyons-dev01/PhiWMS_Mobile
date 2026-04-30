package fr.alcyons.phiwms_mobile.Fragment

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import fr.alcyons.phiwms_mobile.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScannerFragment : Fragment() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var btnFlash: ImageButton
    private lateinit var btnActiveSon: ImageButton
    private lateinit var btnInactiveSon: ImageButton
    private lateinit var btnClose: ImageButton
    private lateinit var layoutFondValidation : LinearLayout

    private var flashOn = false
    private var isSoundOn = true
    private var type = ""
    private var part1: String? = null
    private var part1Time: Long = 0L
    private val windowMs = 1000L
    private val scannerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var toneGenerator: ToneGenerator? = null

    // Callback vers l'activité parente
    var onCodeScanned: ((String) -> Unit)? = null
    var onCloseRequested: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.scanner_fragment, container, false)
    //          ↑ Utilisez le XML du scanner fourni, renommé en fragment_scanner.xml

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

        barcodeView   = view.findViewById(R.id.barcodeView)
        btnFlash      = view.findViewById(R.id.btnFlash)
        btnActiveSon  = view.findViewById(R.id.btnActiveSon)
        btnInactiveSon= view.findViewById(R.id.btnInactiveSon)
        btnClose      = view.findViewById(R.id.btnClose)
        layoutFondValidation = view.findViewById(R.id.layoutFondValidation)

        btnClose.setOnClickListener { onCloseRequested?.invoke() }

        btnFlash.setOnClickListener {
            flashOn = !flashOn
            if (flashOn) barcodeView.setTorchOn() else barcodeView.setTorchOff()
        }

        btnActiveSon.setOnClickListener {
            isSoundOn = false
            btnInactiveSon.visibility = View.VISIBLE
            btnActiveSon.visibility = View.GONE
        }

        btnInactiveSon.setOnClickListener {
            isSoundOn = true
            btnInactiveSon.visibility = View.GONE
            btnActiveSon.visibility = View.VISIBLE
        }

        val formats = listOf(
            BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX,
            BarcodeFormat.EAN_13,  BarcodeFormat.EAN_8,
            BarcodeFormat.UPC_A,   BarcodeFormat.UPC_E,
            BarcodeFormat.CODE_128,BarcodeFormat.CODE_39,
            BarcodeFormat.ITF
        )

        barcodeView.setStatusText("")
        barcodeView.barcodeView.decoderFactory =
            DefaultDecoderFactory(formats, null, null, Intents.Scan.MIXED_SCAN)

        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                scannerScope.launch {
                    withContext(Dispatchers.Main) {
                        layoutFondValidation.visibility = View.VISIBLE
                    }
                    delay(300)
                    withContext(Dispatchers.Main) {
                        layoutFondValidation.visibility = View.GONE
                    }
                }
                val texte = result.text ?: return
                val code = if (texte.uppercase().startsWith("PHITAG"))
                    normalize(texte)
                else
                    handleSplitScan(texte) ?: return

                if (isSoundOn) toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                fireCode(code)
            }
        })
    }

    private fun fireCode(code: String) {
        onCodeScanned?.invoke(code)
    }

    // ---- logique normalize / split identique à Scanner.kt ----

    private fun normalize(raw: String): String = raw
        .replace("]C1", "").replace("]e0", "").replace("]d2", "")
        .replace("]Q3", "").replace("}C1", "").replace("}d2010", "")
        .replace("]d2010", "").replace("\u001D", "").trim()

    private fun handleSplitScan(raw: String): String? {
        val now = System.currentTimeMillis()
        val code = normalize(raw)
        if (isCompleteCode(code)) { part1 = null; part1Time = 0L; return code }
        if (part1 != null && (now - part1Time) > windowMs) part1 = null
        if (part1 == null || part1.contentEquals(code)) {
            if (!code.startsWith("01") && !code.startsWith("02") &&
                !code.startsWith("+") && code.startsWith("+$")) return null
            type = when { code.startsWith("01") || code.startsWith("02") -> "GTIN"
                code.startsWith("+") -> "HIBC" else -> "" }
            part1 = code; part1Time = now; return null
        }
        var assembled = part1 + code
        if (type == "HIBC") {
            val p1 = part1 ?: return null
            assembled = p1.dropLast(1) + "/" + code.substring(1, code.length - 2)
        }
        part1 = null; part1Time = 0L
        return assembled
    }

    private fun looksLikeHibc(s: String) = s.startsWith("+") && s[1].isLetter() && s.contains("/")
    private fun isCompleteCode(code: String): Boolean {
        val c = code.trim()
        if ((c.startsWith("01") || c.startsWith("02")) && c.length > 16) return true
        if (looksLikeHibc(c)) return true
        return false
    }

    override fun onResume()  { super.onResume();  barcodeView.resume() }
    override fun onPause()   { super.onPause();   barcodeView.pause()  }
    override fun onDestroy() { super.onDestroy();     super.onDestroy()
        toneGenerator?.release()
        toneGenerator = null }
}