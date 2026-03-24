package fr.alcyons.phiwms_mobile.BarcodeSearch

import android.media.ToneGenerator
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Commande
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation
import fr.alcyons.phiwms_mobile.R
import java.util.Random

class ScannerRafaleReception : ScannerRafale() {
    protected lateinit var btnLancerTraitement_IB: ImageButton
    protected lateinit var nbScan_TV: TextView
    protected lateinit var listViewCodeScan: ListView

    override val layoutResId: Int = R.layout.scanner_rafale
    private lateinit var adapter_activity: ArrayAdapter<String>
    protected var listeCodeScanne = ArrayList<String>()

    var phReliquatList : List<PH_Reliquat> = ArrayList()
    var receptionid : Int = 0
    lateinit var commandeCourante : Commande
    var produit: Produit? = null
    var ligneBase : PH_Reliquat? = null
    lateinit var serialisation : Serialisation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = findViewById<FrameLayout>(R.id.overlayContainer)
        layoutInflater.inflate(R.layout.scanner_rafale_overlay, container, true)
        btnLancerTraitement_IB = findViewById(R.id.btnLancerTraitement_IB)
        nbScan_TV = findViewById(R.id.nbScan_TV)
        listViewCodeScan = findViewById(R.id.listViewCodeScan)
        //SERIALISATION
        serialisation = Serialisation(this@ScannerRafaleReception, db, utilisateurConnecte)

        btnLancerTraitement_IB.setOnClickListener { v ->
            showProgressDialog()
        }

        adapter_activity = ArrayAdapter(this, R.layout.item_code_scan, listeCodeScanne)
        listViewCodeScan.adapter = adapter_activity

        receptionid = intent.getIntExtra("ReceptionID",0)

        if(receptionid != 0)
        {
            commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionid)
            phReliquatList = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, commandeCourante.numero)
        }

        codeUnique = true
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
                val codeIdentification = resultDecoupage.get("code")
                val numeroLotIdentification = resultDecoupage.get("lot")
                val peremptionIdentification = resultDecoupage.get("peremption")
                val numeroSerieIdentification = resultDecoupage.get("serie")
                val conditionnement = resultDecoupage.get("conditionnement")
                val emplacement = resultDecoupage.get("emplacement")
                val codeinconnu = resultDecoupage.get("codeinconnu")

                val produitIdentifier : List<Produit> = ProduitOpenHelper.getProduitsByIdentification(db, codeIdentification)

                if(produitIdentifier.isEmpty() || produitIdentifier.size > 1)
                {

                }
                else
                {
                    //un seul produit identifie
                    produit = produitIdentifier[0]

                    //on vérifie la présence du produit dans la préparation
                    var produitpresent = false
                    for(courante : PH_Reliquat in phReliquatList)
                    {
                        if(courante.produitID == produit?.iD_produit)
                        {
                            produitpresent = true
                            ligneBase = courante
                            break
                        }
                    }

                    //on vérifie que le produit n'a pas déjà été préparé entièrement
                    if(produitpresent)
                    {
                        val serialisation : Boolean = produit?.isSuivi_Serialisation == false
                        val serialisationreception : Boolean = produit?.isSerialiser_Reception_Delivrance == true

                        if(serialisation && serialisationreception && numeroSerieIdentification.contentEquals(""))
                        {

                        }
                        else
                        {
                            val reliquatPreparer = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeCourante.numero, produit?.iD_produit ?: 0)
                            val qteDemander: Int = ligneBase?.qteReliquat_X ?: 0
                            var qteReceptionne = 0
                            for (ligneTemp in reliquatPreparer) {
                                qteReceptionne += ligneTemp.qteLivraison
                            }
                            val qteRestante: Int = qteDemander - qteReceptionne

                            val designationProduit: String? = ligneBase?.designationCourte
                            val conditionnement =
                                ((ligneBase?.conditionnementAchat ?: 1)).toString()

                            if (qteRestante == 0) {

                            }
                            else
                            {
                                var seriedejascanne = false
                                for (courant in reliquatPreparer) {
                                    if (courant.lot == numeroLotIdentification) {
                                        if (serialisation && serialisationreception) {
                                            if (courant.serie.contentEquals(numeroSerieIdentification)) {
                                                seriedejascanne = true
                                            }
                                        }
                                    }
                                }

                                if (seriedejascanne) {
                                    produit = null

                                } else {
                                    if (!numeroSerieIdentification.contentEquals("")) {
                                        Serialisation.Serialisation_Creer(utilisateurConnecte.id, "G110", codeIdentification, "GTIN", numeroLotIdentification, peremptionIdentification, numeroSerieIdentification, "CDE", commandeCourante.numero).toInt()

                                    } else {

                                    }

                                    //gestion de la validation du scan
                                    enregistrerPhReliquat(ligneBase, numeroLotIdentification, peremptionIdentification, numeroSerieIdentification, conditionnement)
                                }

                            }
                        }
                    }
                    else
                    {

                    }
                }

                val progression = index + 1

                runOnUiThread {
                    progressBar.progress = progression
                    tvProgress.text = "$progression / ${listeCodeScanne.size}"
                }
            }

            runOnUiThread {
                dialog.dismiss()
                finish()
            }

        }.start()
    }
    private fun enregistrerPhReliquat(phReliquatBase: PH_Reliquat?, numeroLot: String?, datePeremptionParam: String?, numeroSerie: String?, conditionnement: String?) {
        val produitCourant = ProduitOpenHelper.getProduitByID(db, produit?.iD_produit ?: 0)
        val listeReliquatReceptionner = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeCourante.numero, produitCourant.iD_produit)
        var datePeremption = ""
        if(datePeremptionParam != null)
            datePeremption = datePeremptionParam

        if(!datePeremption.contentEquals(""))
        {
            val datePeremptionTab: Array<String?> = datePeremption.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (datePeremptionTab.size == 3) datePeremption = datePeremptionTab[2] + "-" + datePeremptionTab[1] + "-" + datePeremptionTab[0]
        }

        val emplacementName: String? = produit?.emplacement_PUI_Defaut

        var creation = true
        for (reliquat in listeReliquatReceptionner) {
            if (reliquat.lot.contentEquals(numeroLot) && reliquat.serie.contentEquals(numeroSerie)) {
                creation = false
                reliquat.qteLivraison += reliquat.conditionnementAchat
                PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquat)
            }
        }

        if (creation) {
            val randomreliquat = Random()
            var reliquatId = randomreliquat.nextInt()
            if (reliquatId > 0) reliquatId *= -1

            val phReliquatCourant = phReliquatBase
            phReliquatCourant?.reliquat_UID = reliquatId

            var quantite = phReliquatCourant?.conditionnementAchat
            if(!conditionnement.contentEquals("") && !conditionnement.contentEquals("0"))
                quantite = conditionnement?.toInt()

            phReliquatCourant?.lot = numeroLot
            phReliquatCourant?.serie = numeroSerie
            phReliquatCourant?.peremptionDate = datePeremption

            if (commandeCourante.ref_Depot_Dest.contains("-PAD")) {
                phReliquatCourant?.zone = "RECEPTION"
                phReliquatCourant?.emplacement = "RECEPTION-" + commandeCourante.numero + "-" + commandeCourante.patient_identite
            } else {
                phReliquatCourant?.zone = produit?.zone_PUI_Defaut
                phReliquatCourant?.emplacement= emplacementName
            }
            if (quantite != null) {
                phReliquatCourant?.qteLivraison = quantite
            }
            phReliquatCourant?.bL_Numero = ""
            phReliquatCourant?.scanValue = ""

            PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, phReliquatCourant)
        }
    }
}