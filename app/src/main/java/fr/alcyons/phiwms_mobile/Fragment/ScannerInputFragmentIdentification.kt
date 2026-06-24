package fr.alcyons.phiwms_mobile.Fragment

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import fr.alcyons.phiwms_mobile.Interfaces.ScanDebounce
import fr.alcyons.phiwms_mobile.Interfaces.ScannerControllable
import fr.alcyons.phiwms_mobile.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScannerInputFragmentIdentification : Fragment(), ScanDebounce, ScannerControllable
{

    private lateinit var scannerInput_ET: EditText
    private lateinit var descriptionCode_TV: TextView
    private lateinit var btnActiveSon: ImageButton
    private lateinit var btnInactiveSon: ImageButton
    private lateinit var btnClose: ImageButton
    private lateinit var viewBtnClose: View
    private lateinit var layoutFondValidation: LinearLayout

    private var isSoundOn = true
    override var btnCloseVisible: Boolean = true

    private var type = ""
    private var part1: String? = null
    private var part1Time: Long = 0L
    private val windowMs = 1000L
    private val scannerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // ScanDebounce interface implementation
    override var mLastScanTime: Long = 0
    override var mScanDebounceMS: Long = 1000L

    val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    var onCodeScanned: ((String) -> Unit)? = null
    var onCloseRequested: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.scanner_input_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scannerInput_ET      = view.findViewById(R.id.scannerInput_ET)
        descriptionCode_TV   = view.findViewById(R.id.descriptionCode_TV)
        btnActiveSon         = view.findViewById(R.id.btnActiveSon)
        btnInactiveSon       = view.findViewById(R.id.btnInactiveSon)
        btnClose             = view.findViewById(R.id.btnClose)
        viewBtnClose         = view.findViewById(R.id.viewBtnClose)
        layoutFondValidation = view.findViewById(R.id.layoutFondValidation)

        btnClose.visibility = if (btnCloseVisible) View.VISIBLE else View.GONE
        viewBtnClose.visibility = if (btnCloseVisible) View.VISIBLE else View.GONE


        // Focus automatique pour recevoir les scans dès l'ouverture
        scannerInput_ET.requestFocus()
        // Empêche le clavier de s'afficher
        scannerInput_ET.showSoftInputOnFocus = false

        // Cache le clavier s'il est déjà visible
        scannerInput_ET.post {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(scannerInput_ET.windowToken, 0)
        }

        // Détection fin de scan : ENTER (Honeywell) ou TAB (Zebra)
        scannerInput_ET.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_TAB)) {
                val texte = scannerInput_ET.text.toString()
                if (texte.isNotEmpty()) {
                    traiterCode(texte)
                    scannerInput_ET.setText("")
                }
                true
            } else {
                false
            }
        }

        btnClose.setOnClickListener {
            onCloseRequested?.invoke()
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
    }

    private fun traiterCode(texte: String)
    {
        val code = normalize(texte)


        scannerScope.launch {
            // Affiche le code scanné + fond vert
            withContext(Dispatchers.Main) {
                afficherCodeColore(code)
                layoutFondValidation.visibility = View.VISIBLE
                if (isSoundOn) toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
            }

            // Fond vert pendant 200ms
            delay(200)
            withContext(Dispatchers.Main) {
                layoutFondValidation.visibility = View.GONE
            }

            delay(100)
            withContext(Dispatchers.Main) {
                descriptionCode_TV.text = ""
                fireCode(code)
            }
        }
    }

    private fun afficherCodeColore(code: String) {
        val spannable = SpannableString(code)
        val couleurIdentifiant = ContextCompat.getColor(requireContext(), R.color.bleu_clair_alcyons)
        val couleurValeur      = ContextCompat.getColor(requireContext(), R.color.blanc)
        val couleurDate        = ContextCompat.getColor(requireContext(), R.color.vert)
        val couleurLot         = ContextCompat.getColor(requireContext(), R.color.jaune)
        val couleurSerie       = ContextCompat.getColor(requireContext(), R.color.orange)

        val gs = "\u001D"
        val identifiantsConnus = listOf("17", "10", "21", "30", "01", "02")

        // GTIN : toujours "01" + 14 chiffres au début
        if (code.length >= 16) {
            spannable.setSpan(ForegroundColorSpan(couleurIdentifiant), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(couleurValeur), 2, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        var position = 16

        while (position < code.length) {
            val reste = code.substring(position)

            when {
                // Séparateur GS → on saute
                reste.startsWith(gs) -> position += 1

                // Date péremption "17" + 6 chiffres fixes
                reste.startsWith("17") && reste.length >= 8 -> {
                    spannable.setSpan(ForegroundColorSpan(couleurIdentifiant), position, position + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(ForegroundColorSpan(couleurDate), position + 2, position + 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    position += 8
                }

                // Numéro de lot "10" + longueur variable
                reste.startsWith("10") -> {
                    val debutValeur = position + 2
                    val finValeur = trouverFinChampDynamique(code, debutValeur, gs, identifiantsConnus)
                    spannable.setSpan(ForegroundColorSpan(couleurIdentifiant), position, debutValeur, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(ForegroundColorSpan(couleurLot), debutValeur, finValeur, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    position = if (finValeur < code.length && code[finValeur].toString() == gs) finValeur + 1 else finValeur
                }

                // Numéro de série "21" + longueur variable
                reste.startsWith("21") -> {
                    val debutValeur = position + 2
                    val finValeur = trouverFinChampDynamique(code, debutValeur, gs, identifiantsConnus)
                    spannable.setSpan(ForegroundColorSpan(couleurIdentifiant), position, debutValeur, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(ForegroundColorSpan(couleurSerie), debutValeur, finValeur, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    position = if (finValeur < code.length && code[finValeur].toString() == gs) finValeur + 1 else finValeur
                }

                // Identifiant inconnu → on avance
                else -> position += 1
            }
        }

        descriptionCode_TV.text = spannable
    }

    private fun trouverFinChampDynamique(
        code: String,
        debut: Int,
        gs: String,
        identifiantsConnus: List<String>
    ): Int {
        // Cherche d'abord un séparateur GS
        val indexGS = code.indexOf(gs, debut)

        // Cherche le prochain identifiant connu après la position de début
        var prochainIdentifiant = if (indexGS != -1) indexGS else code.length

        for (id in identifiantsConnus) {
            var index = code.indexOf(id, debut)
            while (index != -1 && index < prochainIdentifiant) {
                // Vérifie que ce n'est pas une valeur qui contient accidentellement "17", "10", etc.
                // en s'assurant que la position précédente n'est pas un chiffre
                val charAvant = if (index > 0) code[index - 1] else ' '
                if (!charAvant.isDigit() && !charAvant.isLetter()) {
                    prochainIdentifiant = index
                    break
                }
                index = code.indexOf(id, index + 1)
            }
        }

        return prochainIdentifiant
    }

    // Trouve la fin d'un champ à longueur variable (séparateur GS ou fin de chaine)
    private fun trouverFinChamp(code: String, debut: Int, gs: String): Int {
        val indexGS = code.indexOf(gs, debut)
        return if (indexGS != -1) indexGS else code.length
    }


    private fun fireCode(code: String) {
        onCodeScanned?.invoke(code)
    }

    private fun normalize(raw: String): String = raw
        .replace("]C1", "").replace("]e0", "").replace("]d2", "")
        .replace("]Q3", "").replace("}C1", "").replace("}d2010", "")
        .replace("]d2010", "")

    private fun handleSplitScan(raw: String): String? {
        val now = System.currentTimeMillis()
        val code = normalize(raw)
        if (isCompleteCode(code)) {
            part1 = null
            part1Time = 0L
            return code
        }
        if (part1 != null && (now - part1Time) > windowMs) part1 = null
        if (part1 == null || part1.contentEquals(code)) {
            if (!code.startsWith("01") && !code.startsWith("02") &&
                !code.startsWith("+") && code.startsWith("+$")) return null
            type = when {
                code.startsWith("01") || code.startsWith("02") -> "GTIN"
                code.startsWith("+") -> "HIBC"
                else -> ""
            }
            part1 = code
            part1Time = now
            return null
        }
        var assembled = part1 + code
        if (type == "HIBC") {
            val p1 = part1 ?: return null
            assembled = p1.dropLast(1) + "/" + code.substring(1, code.length - 2)
        }
        part1 = null
        part1Time = 0L
        return assembled
    }

    private fun looksLikeHibc(s: String) =
        s.startsWith("+") && s[1].isLetter() && s.contains("/")

    private fun isCompleteCode(code: String): Boolean {
        val c = code.trim()
        if ((c.startsWith("01") || c.startsWith("02")) && c.length > 16) return true
        if (looksLikeHibc(c)) return true
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        toneGenerator.release()
    }

    override fun masquerBtnClose() {
        view?.findViewById<ImageButton>(R.id.btnClose)?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        scannerInput_ET.showSoftInputOnFocus = false
        scannerInput_ET.post {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(scannerInput_ET.windowToken, 0)
        }
    }
}