package fr.alcyons.phiwms_mobile.BarcodeSearch

import android.content.Intent
import android.media.ToneGenerator
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ListView
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.R
import java.io.Serializable

class ScannerPhotoEmplacement : Scanner() {
    override val layoutResId: Int = R.layout.scanner
    private var isProcessing : Boolean = false
    var listeProduitScanne: MutableList<Produit> = ArrayList()
    var listDesignation : MutableList<String> = ArrayList()
    private lateinit var adapter : ArrayAdapter<String>
    var ListViewDesignation: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = findViewById<FrameLayout>(R.id.overlayContainer)
        layoutInflater.inflate(R.layout.layout_scanphoto_planplacement, container, true)
        adapter = ArrayAdapter(this, R.layout.row_string_text_blanc, listDesignation)
        ListViewDesignation = findViewById<View?>(R.id.ListViewDesignation) as ListView
        ListViewDesignation!!.setAdapter(adapter)
        listeProduitScanne = (intent.extras?.getSerializable("ListProduitScannes") as? ArrayList<Produit>)?.toMutableList() ?: mutableListOf()

        (findViewById<View?>(R.id.boutonPlacerReferences) as Button).setOnClickListener { quitterActivite() }
    }

    override fun quitterActivite()
    {
        val scannerSearchOnlyIntent = Intent()
        val scannerSearchOnlyBundle: Bundle = this@ScannerPhotoEmplacement.getBundle()
        val codeEchangeActivity = 0

        scannerSearchOnlyBundle.putSerializable("ListProduitScannes",
            listeProduitScanne as Serializable?
        )
        scannerSearchOnlyBundle.putBoolean("placement", false)

        scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle)

        this@ScannerPhotoEmplacement.setResult(codeEchangeActivity, scannerSearchOnlyIntent)
        this@ScannerPhotoEmplacement.finish()
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

            val produitScanne : Produit? = ProduitOpenHelper.getUnProduitsByIdentification(db, codeIdentification)
            if(produitScanne != null)
            {
                var trouve : Boolean = false
                for(produitTemp : Produit in listeProduitScanne)
                {
                    if(produitTemp.iD_produit == produitScanne?.iD_produit)
                    {
                        trouve = true
                        break
                    }
                }

                if(!trouve && (produitScanne.emplacement_PUI_Defaut.contentEquals("EMPLACEMENT") || produitScanne.emplacement_PUI_Defaut.contentEquals("")))
                {
                    listeProduitScanne.add(produitScanne)
                    listDesignation.add(0, produitScanne.designation_interne)
                    adapter.notifyDataSetChanged()
                }

                (findViewById<View?>(R.id.boutonPlacerReferences) as Button).setText("Placer les " + listeProduitScanne.size + " références")
                if (listeProduitScanne.isNotEmpty()) (findViewById<View?>(R.id.boutonPlacerReferences) as Button).visibility = View.VISIBLE
                else (findViewById<View?>(R.id.boutonPlacerReferences) as Button).visibility = View.INVISIBLE
            }

            isProcessing = false
        }
    }
}