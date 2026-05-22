package fr.alcyons.phiwms_mobile.BarcodeSearch

import android.content.Intent
import android.media.ToneGenerator
import android.os.Bundle
import android.widget.FrameLayout
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites
import fr.alcyons.phiwms_mobile.R
import java.util.Locale

class ScannerPhotoIdentificationDocument : Scanner() {

    override val layoutResId: Int = R.layout.scanner
    private var isProcessing: Boolean = false

    // ═══════════════════════════════════════════
    // Préfixes reconnus, dans l'ordre de priorité
    // ═══════════════════════════════════════════
    companion object {
        private val PREFIXES = listOf("PHIBCF", "DDS", "PHI")
        private const val FALLBACK_DELIMITER = "*"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<FrameLayout>(R.id.overlayContainer).also { container ->
            layoutInflater.inflate(R.layout.layout_scanphoto_identification, container, true)
        }
    }

    override fun onCodeScanned(code: String) {
        if (code.isBlank() || isProcessing) return

        isProcessing = true

        if (isSoundOn) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 150)
        }

        val codeDocument = extraireCodeDocument(code)

        val resultIntent = Intent().apply {
            putExtra("numeroDocument", codeDocument)
        }
        setResult(CodesEchangesActivites.RESULT_OK, resultIntent)
        finish()
    }

    // ═══════════════════════════════════════════
    // Extraction du code document depuis le scan
    // ═══════════════════════════════════════════
    private fun extraireCodeDocument(code: String): String {
        val codeUpper = code.uppercase(Locale.getDefault())

        // 1. Tentative avec les préfixes connus (PHIBCF, DDS, PHI)
        for (prefix in PREFIXES) {
            if (codeUpper.startsWith(prefix)) {
                val parts = codeUpper.split(prefix).filter { it.isNotEmpty() }
                if (parts.isNotEmpty()) return parts.first()
            }
        }

        // 2. Fallback : séparateur "*"
        val partsEtoile = code.split(FALLBACK_DELIMITER).filter { it.isNotEmpty() }
        if (partsEtoile.size >= 2) return partsEtoile[1]

        // 3. Aucun préfixe ni séparateur reconnu : on retourne le code brut
        return code
    }
}