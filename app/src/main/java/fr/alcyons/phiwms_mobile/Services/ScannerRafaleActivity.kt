package fr.alcyons.phiwms_mobile.Services

import android.annotation.SuppressLint
import android.media.ToneGenerator
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerRafale
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.R

class ScannerRafaleActivity : ScannerRafale() {
    protected lateinit var btnLancerTraitement_IB: ImageButton
    protected lateinit var nbScan_TV: TextView
    protected lateinit var listViewCodeScan: ListView

    override val layoutResId: Int = R.layout.scanner_rafale
    private lateinit var adapter_activity: ArrayAdapter<String>
    protected var listeCodeScanne = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = findViewById<FrameLayout>(R.id.overlayContainer)
        layoutInflater.inflate(R.layout.scanner_rafale_overlay, container, true)
        btnLancerTraitement_IB = findViewById(R.id.btnLancerTraitement_IB)
        nbScan_TV = findViewById(R.id.nbScan_TV)
        listViewCodeScan = findViewById(R.id.listViewCodeScan)

        btnLancerTraitement_IB.setOnClickListener { v ->
            showProgressDialog()
        }

        adapter_activity = ArrayAdapter(this, R.layout.item_code_scan, listeCodeScanne)
        listViewCodeScan.adapter = adapter_activity


    }

    override fun onCodeScanned(code: String) {
        if(!code.contentEquals(""))
        {
            if((codeUnique && !listeCodeScanne.contains(code)) || !codeUnique)
            {
                listeCodeScanne.add(code)
                adapter_activity.notifyDataSetChanged() // ← Met à jour l'affichage
                Thread { toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150) }.start()
                nbScan_TV.text = listeCodeScanne.size.toString()+" codes scannés"
            }

            btnLancerTraitement_IB.visibility = ImageButton.VISIBLE
        }
    }

    private fun showProgressDialog() {
        val dialogView = layoutInflater.inflate(R.layout.progressbar_modale, null)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)
        val tvProgress = dialogView.findViewById<TextView>(R.id.tvProgress)

        // Définir le max selon la taille de la liste
        progressBar.max = listeCodeScanne.size

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()

        // Traitement dans un thread séparé pour ne pas bloquer l'UI
        Thread {
            val resultats = HashMap<String, Int>()  //

            listeCodeScanne.forEachIndexed { index, item ->

                val resultDecoupage: HashMap<String, String> = GestionCodeScanne.Companion.decoupageCode(item)
                val identifiant = resultDecoupage.get("code")
                val lot = resultDecoupage.get("lot")
                val peremption = resultDecoupage.get("peremption")
                val serie = resultDecoupage.get("serie")
                val emplacement = resultDecoupage.get("emplacement")
                val codeinconnu = resultDecoupage.get("codeinconnu")

                //on récupère le produit
                val produitcourant = ProduitOpenHelper.getUnProduitsByIdentification(db, identifiant)
                // Compter les occurrences de chaque lot
                if (produitcourant != null) {
                    resultats[produitcourant.designation_interne] = (resultats[produitcourant.designation_interne] ?: 0) + 1
                }
                else
                {
                    resultats["Produit inconnu"] = (resultats["Produit inconnu"] ?: 0) + 1
                }

                val progression = index + 1

                runOnUiThread {
                    progressBar.progress = progression
                    tvProgress.text = "$progression / ${listeCodeScanne.size}"
                }
            }

            runOnUiThread {
                dialog.dismiss()
                showResultDialog(resultats)
            }

        }.start()
    }

    private fun showResultDialog(resultats: HashMap<String, Int>) {
        val dialogView = layoutInflater.inflate(R.layout.listeview_modale, null)
        val lvResults = dialogView.findViewById<ListView>(R.id.lvResults)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)

        // Transforme le HashMap en liste de chaînes "LOT123 : 4x"
        val lignes = resultats.entries
            .sortedByDescending { it.value }
            .map { (lot, count) -> "$lot : $count fois" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lignes)
        lvResults.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnClose.setOnClickListener {
            dialog.dismiss()
            listeCodeScanne.clear()
            adapter_activity.notifyDataSetChanged() // ← Met à jour l'affichage
            nbScan_TV.text = listeCodeScanne.size.toString()+" codes scannés"
            btnLancerTraitement_IB.visibility = ImageButton.INVISIBLE
        }

        dialog.show()
    }
}