package fr.alcyons.phiwms_mobile.BarcodeSearch

import android.annotation.SuppressLint
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Commande
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation
import fr.alcyons.phiwms_mobile.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Random

class ScannerPhotoReception  : Scanner() {
    var phReliquatList : List<PH_Reliquat> = ArrayList()
    var receptionid : Int = 0
    lateinit var commandeCourante : Commande
    override val layoutResId: Int = R.layout.scanner
    var produit: Produit? = null
    var ligneBase : PH_Reliquat? = null
    private var isProcessing : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = findViewById<FrameLayout>(R.id.overlayContainer)
        layoutInflater.inflate(R.layout.layout_scanphoto_reception, container, true)

        receptionid = intent.getIntExtra("ReceptionID",0)

        if(receptionid != 0)
        {
            commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionid)
            phReliquatList = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, commandeCourante.numero)
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCodeScanned(code: String) {
        if(!code.contentEquals("") && !isProcessing)
        {
            if(isSoundOn)
            {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 150)
            }
            isProcessing = true
            lifecycleScope.launch(Dispatchers.IO) {
                val resultDecoupage: HashMap<String, String> = GestionCodeScanne.decoupageCode(code)
                val codeIdentification = resultDecoupage["code"]
                val numeroLotIdentification = resultDecoupage["lot"]
                val peremptionIdentification = resultDecoupage["peremption"]
                val numeroSerieIdentification = resultDecoupage["serie"]
                val emplacementIdentification = resultDecoupage["emplacement"]
                val codeinconnuIdentification = resultDecoupage["codeinconnu"]

                val produitIdentifier : List<Produit> = ProduitOpenHelper.getProduitsByIdentification(db, codeIdentification)

                if(produitIdentifier.isEmpty() || produitIdentifier.size > 1)
                {
                    withContext(Dispatchers.Main) {
                        //plusieurs produits identifiés
                        (findViewById<View?>(R.id.layoutProduitInconnu) as LinearLayout).visibility = View.VISIBLE
                        delay(3000)
                        (findViewById<View?>(R.id.layoutProduitInconnu) as LinearLayout).visibility = View.INVISIBLE
                        isProcessing = false
                    }
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
                            withContext(Dispatchers.Main) {
                                //plusieurs produits identifiés
                                (findViewById<View?>(R.id.layoutSerieNonScannee) as LinearLayout).visibility = View.VISIBLE
                                delay(3000)
                                (findViewById<View?>(R.id.layoutSerieNonScannee) as LinearLayout).visibility = View.INVISIBLE
                                isProcessing = false
                            }
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
                                withContext(Dispatchers.Main) {
                                    (findViewById<View?>(R.id.layoutProduitComplet) as LinearLayout).visibility = View.VISIBLE
                                    (findViewById<View?>(R.id.designationComplete) as TextView).text = designationProduit
                                    (findViewById<View?>(R.id.quantiteComplete) as TextView).text = "$qteReceptionne / $qteDemander"

                                    delay(2000)

                                    (findViewById<View?>(R.id.layoutProduitComplet) as LinearLayout).visibility = View.INVISIBLE
                                    (findViewById<View?>(R.id.quantiteComplete) as TextView).text = ""
                                    (findViewById<View?>(R.id.designationComplete) as TextView).text = ""

                                    isProcessing = false
                                    produit = null
                                }
                            }
                            else
                            {
                                val qteProgress = qteReceptionne + conditionnement.toInt()
                                withContext(Dispatchers.Main) {
                                    (findViewById<View?>(R.id.progressBarQuantite) as ProgressBar).max = qteDemander
                                    (findViewById<View?>(R.id.progressBarQuantite) as ProgressBar).progress = qteProgress
                                    (findViewById<View?>(R.id.designationValidation) as TextView).text = designationProduit
                                    (findViewById<View?>(R.id.quantiteValidation) as TextView).text = "$qteProgress / $qteDemander"
                                    (findViewById<View?>(R.id.lotValidation) as TextView).text = numeroLotIdentification
                                    (findViewById<View?>(R.id.peremptionValidation) as TextView).text = peremptionIdentification
                                    (findViewById<View?>(R.id.emplacementValidation) as TextView).text = produit?.emplacement_PUI_Defaut
                                }
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
                                    withContext(Dispatchers.Main) {
                                        (findViewById<View?>(R.id.layoutSerieScannee) as LinearLayout).visibility = View.VISIBLE
                                        delay(2000)
                                        (findViewById<View?>(R.id.layoutSerieScannee) as LinearLayout).visibility = View.INVISIBLE
                                    }
                                } else {
                                    if (!numeroSerieIdentification.contentEquals("")) {
                                        Serialisation.Serialisation_Creer(utilisateurConnecte.id, "G110", codeIdentification, "GTIN", numeroLotIdentification, peremptionIdentification, numeroSerieIdentification, "CDE", commandeCourante.numero).toInt()
                                        withContext(Dispatchers.Main) {
                                            (findViewById<View?>(R.id.serieValidation) as TextView).text = numeroSerieIdentification
                                            (findViewById<View?>(R.id.layoutSerieValidation) as LinearLayout).visibility = View.VISIBLE
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            (findViewById<View?>(R.id.layoutSerieValidation) as LinearLayout).visibility = View.GONE
                                            (findViewById<View?>(R.id.serieValidation) as TextView).text = ""
                                        }
                                    }

                                    //gestion de la validation du scan
                                    enregistrerPhReliquat(ligneBase)
                                    withContext(Dispatchers.Main) {
                                        (findViewById<View?>(R.id.layoutFondValidation) as LinearLayout).visibility = View.VISIBLE

                                        delay(250)

                                        (findViewById<View?>(R.id.layoutFondValidation) as LinearLayout).visibility = View.GONE
                                        (findViewById<View?>(R.id.lotValidation) as TextView).text = ""
                                        (findViewById<View?>(R.id.peremptionValidation) as TextView).text = ""
                                        (findViewById<View?>(R.id.emplacementValidation) as TextView).text = ""
                                        (findViewById<View?>(R.id.serieValidation) as TextView).text = ""
                                        isProcessing = false
                                        produit = null
                                    }
                                }

                            }
                        }
                    }
                    else
                    {
                        withContext(Dispatchers.Main) {
                            //produit non présent
                            (findViewById<View?>(R.id.layoutProduitAbsent) as LinearLayout).visibility = View.VISIBLE
                            delay(2000)
                            isProcessing = false
                            (findViewById<View?>(R.id.layoutProduitAbsent) as LinearLayout).visibility = View.INVISIBLE
                            produit = null
                        }
                    }
                }
            }
        }
    }

    private fun enregistrerPhReliquat(phReliquatBase: PH_Reliquat?) {
        val produitCourant = ProduitOpenHelper.getProduitByID(db, produit?.iD_produit ?: 0)
        val listeReliquatReceptionner = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeCourante.numero, produitCourant.iD_produit)
        val numeroLot = (findViewById<View?>(R.id.lotValidation) as TextView).text.toString()
        var datePeremption = (findViewById<View?>(R.id.peremptionValidation) as TextView).text.toString()
        val datePeremptionTab: Array<String?> = datePeremption.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (datePeremptionTab.size == 3) datePeremption = datePeremptionTab[2] + "-" + datePeremptionTab[1] + "-" + datePeremptionTab[0]

        val emplacementName: String? = produit?.emplacement_PUI_Defaut
        val numeroSerie = (findViewById<View?>(R.id.serieValidation) as TextView).text.toString()

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
            val quantite = phReliquatCourant?.conditionnementAchat

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
                phReliquatCourant.qteLivraison = quantite
            }
            phReliquatCourant?.bL_Numero = ""
            phReliquatCourant?.scanValue = ""

            PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, phReliquatCourant)
        }
    }

}