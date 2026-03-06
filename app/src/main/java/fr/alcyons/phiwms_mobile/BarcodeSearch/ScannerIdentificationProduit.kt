package fr.alcyons.phiwms_mobile.BarcodeSearch

import android.content.Intent
import android.media.ToneGenerator
import android.os.Bundle
import android.widget.FrameLayout
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.R

class ScannerIdentificationProduit : Scanner() {
    override val layoutResId: Int = R.layout.scanner
    private var isProcessing : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = findViewById<FrameLayout>(R.id.overlayContainer)
        layoutInflater.inflate(R.layout.layout_scanphoto_identification, container, true)
    }

    override fun onCodeScanned(code: String) {
        if(!code.contentEquals("") && !isProcessing)
        {
            if(isSoundOn)
            {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 150)
            }
            isProcessing = true
            var gtin = false
            var chaineRetourner: String
            val resultDecoupage: HashMap<String, String> = GestionCodeScanne.decoupageCode(code)
            val codeIdentification = resultDecoupage["code"]
            val numeroLotIdentification = resultDecoupage["lot"]
            val peremptionIdentification = resultDecoupage["peremption"]
            val numeroSerieIdentification = resultDecoupage["serie"]
            val emplacementIdentification = resultDecoupage["emplacement"]
            val codeinconnuIdentification = resultDecoupage["codeinconnu"]
            val type = resultDecoupage["type"]


            if (!codeIdentification.contentEquals("")) {
                chaineRetourner = code
            } else {
                val texteNettoye = code.replace("\u0000".toRegex(), "")
                chaineRetourner = texteNettoye
            }

            if(type.contentEquals("GS1"))
                gtin = true

            val scannerSearchOnlyIntent = Intent()
            val scannerSearchOnlyBundle: Bundle = this@ScannerIdentificationProduit.getBundle()
            val codeEchangeActivity = 0

            scannerSearchOnlyBundle.putString("code", codeIdentification)
            scannerSearchOnlyBundle.putString("numLot", numeroLotIdentification)
            scannerSearchOnlyBundle.putString("numSerie", numeroSerieIdentification)
            scannerSearchOnlyBundle.putString("datePeremption", peremptionIdentification)
            scannerSearchOnlyBundle.putString("datePeremptionSqlFormat", peremptionIdentification)
            scannerSearchOnlyBundle.putBoolean("gtin", gtin)
            scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle)

            this@ScannerIdentificationProduit.setResult(codeEchangeActivity, scannerSearchOnlyIntent)
            this@ScannerIdentificationProduit.finish()
        }
    }
}