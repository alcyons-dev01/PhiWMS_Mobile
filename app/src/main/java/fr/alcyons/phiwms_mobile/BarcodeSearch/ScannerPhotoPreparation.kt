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
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.StockUtilisesOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Classes.StockUtilises
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation
import fr.alcyons.phiwms_mobile.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Random

class ScannerPhotoPreparation : Scanner() {
    var preparationLigneList : List<PH_Preparation_Ligne> = ArrayList()
    var preparationid : Int = 0
    lateinit var preparationCourante : PH_Preparation
    var lotdisponnibleList : ArrayList<String> = ArrayList()
    override val layoutResId: Int = R.layout.scanner
    var produit: Produit? = null
    private var isProcessing : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = findViewById<FrameLayout>(R.id.overlayContainer)
        layoutInflater.inflate(R.layout.layout_scanphoto_preparation, container, true)


        preparationid = intent.getIntExtra("preparationId",0)
        lotdisponnibleList = intent.getStringArrayListExtra("liste_lot")!!

        if(preparationid != 0)
        {
            preparationCourante = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparationid)
            preparationLigneList = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(db, preparationCourante)
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
                    var ligneBase : PH_Preparation_Ligne? = null
                    for(courante : PH_Preparation_Ligne in preparationLigneList)
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
                        val preparationLignesPreparer = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationAndProduitNeg(db, preparationCourante, ligneBase?.produitID ?: 0)
                        val qteDemander: Int = ligneBase?.qte_APreparer ?: 0
                        var qtePreparer = 0
                        for (ligneTemp in preparationLignesPreparer) {
                            qtePreparer += ligneTemp.qte_preparer
                        }
                        val qteRestante: Int = qteDemander - qtePreparer

                        val designationProduit: String? = ligneBase?.produitDesignation
                        val conditionnement = ligneBase?.produitCondDistrib?.toInt().toString()

                        if (qteRestante == 0) {
                            withContext(Dispatchers.Main) {
                                (findViewById<View?>(R.id.layoutProduitComplet) as LinearLayout).visibility = View.VISIBLE
                                (findViewById<View?>(R.id.designationComplete) as TextView).text = designationProduit
                                (findViewById<View?>(R.id.quantiteComplete) as TextView).text = "$qtePreparer / $qteDemander"

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
                            //on vérifie la présence du lot dans la préparation
                            var lotpresent = false
                            for(lotcourant : String in lotdisponnibleList)
                            {
                                if(lotcourant.contentEquals(numeroLotIdentification))
                                {
                                    lotpresent = true
                                    break
                                }
                            }

                            if(lotpresent)
                            {
                                //on récupère la fiche de stock concernée
                                var stockcourant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByLotPeremptionEtDepot(db, numeroLotIdentification, peremptionIdentification, DepotOpenHelper.getDepotPUI(db))

                                if(stockcourant != null && stockcourant.produit_Code == produit?.iD_produit)
                                {
                                    //si le produit est serialisé, on vérifie qu'il n'a pas encore été scanné
                                    val serialisation : Boolean = produit?.isSuivi_Serialisation == false
                                    val serialisationpreparation : Boolean = produit?.isSerialiser_Reception_Delivrance == false
                                    if(serialisation && !serialisationpreparation)
                                    {
                                        Serialisation.Serialisation_Creer(utilisateurConnecte.id, "G110", codeIdentification, "GTIN", numeroLotIdentification, peremptionIdentification, numeroSerieIdentification, "DELIVRANCE", preparationCourante.uid.toString()).toInt()
                                    }

                                    //on peut modifier la quantité
                                    val qteProgress = qtePreparer + conditionnement.toInt()
                                    withContext(Dispatchers.Main) {
                                        (findViewById<View?>(R.id.progressBarQuantite) as ProgressBar).max = qteDemander
                                        (findViewById<View?>(R.id.progressBarQuantite) as ProgressBar).progress = qteProgress
                                        (findViewById<View?>(R.id.designationValidation) as TextView).text = designationProduit
                                        (findViewById<View?>(R.id.quantiteValidation) as TextView).text = "$qteProgress / $qteDemander"
                                        (findViewById<View?>(R.id.lotValidation) as TextView).text = numeroLotIdentification
                                        (findViewById<View?>(R.id.peremptionValidation) as TextView).text = peremptionIdentification
                                        (findViewById<View?>(R.id.emplacementValidation) as TextView).text = stockcourant.emplacement
                                    }
                                    if (!numeroSerieIdentification.contentEquals("")) {
                                        (findViewById<View?>(R.id.layoutSerieValidation) as LinearLayout).visibility = View.VISIBLE
                                        (findViewById<View?>(R.id.serieValidation) as TextView).text = numeroSerieIdentification
                                        if (!stockcourant.serie.contentEquals(numeroSerieIdentification)) {
                                            val stockTemp: Stock_Lot_Emplacement_Light = stockcourant
                                            stockcourant = Stock_Lot_Emplacement_Light(conditionnement.toInt().toDouble(), stockTemp.lot, stockTemp.peremptionDate, stockTemp.emplacement, stockTemp.depot_Reference, stockTemp.zone, stockTemp.produit_Code, 0, numeroSerieIdentification)
                                            Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stockcourant)
                                        }
                                    }

                                    //on valide
                                    //gestion de la validation du scan
                                    val finalligneBase = ligneBase
                                    val quantiteSaisie = conditionnement.toInt()
                                    stockcourant.qte_Preparer = stockcourant.qte_Preparer + quantiteSaisie
                                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockcourant)
                                    enregistrementPreparationLigne(finalligneBase, stockcourant)
                                    produit = null

                                    withContext(Dispatchers.Main) {
                                        (findViewById<View?>(R.id.layoutFondValidation) as LinearLayout).visibility = View.VISIBLE

                                        delay(500)

                                        (findViewById<View?>(R.id.layoutFondValidation) as LinearLayout).visibility = View.GONE
                                        (findViewById<View?>(R.id.lotValidation) as TextView).text = ""
                                        (findViewById<View?>(R.id.peremptionValidation) as TextView).text = ""
                                        (findViewById<View?>(R.id.emplacementValidation) as TextView).text = ""
                                        (findViewById<View?>(R.id.serieValidation) as TextView).text = ""
                                        isProcessing = false
                                    }
                                }
                                else
                                {
                                    withContext(Dispatchers.Main) {
                                        //erreur récupération du stock
                                        (findViewById<View?>(R.id.layoutLotAbsent) as LinearLayout).visibility = View.VISIBLE

                                        delay(2000)
                                        (findViewById<View?>(R.id.layoutLotAbsent) as LinearLayout).visibility = View.INVISIBLE

                                        isProcessing = false

                                        produit = null
                                    }
                                }
                            }
                            else
                            {
                                withContext(Dispatchers.Main) {
                                    //gestion lot non présent
                                    (findViewById<View?>(R.id.layoutLotAbsent) as LinearLayout).visibility = View.VISIBLE

                                    delay(2000)
                                    (findViewById<View?>(R.id.layoutLotAbsent) as LinearLayout).visibility = View.INVISIBLE
                                    isProcessing = false
                                    produit = null
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun enregistrementPreparationLigne(phPreparationLigneCorrespondant : PH_Preparation_Ligne?, stockLotEmplacementLight : Stock_Lot_Emplacement_Light)
    {
        if(stockLotEmplacementLight.qte_Preparer > 0)
        {
            val now : LocalDateTime = LocalDateTime.now()
            val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDateTime : String = now.format(formatter)
            val preparationtemp : PH_Preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db,
                phPreparationLigneCorrespondant?.preparationID ?: 0
            )
            val stockUtilises = StockUtilises(preparationid.toString(), stockLotEmplacementLight.produit_Code, stockLotEmplacementLight._UID, stockLotEmplacementLight.lot, stockLotEmplacementLight.peremptionDate, preparationtemp.depotOrigineID, stockLotEmplacementLight.zone, stockLotEmplacementLight.emplacement, stockLotEmplacementLight.qte_Preparer, utilisateurConnecte.id, formattedDateTime, utilisateurConnecte.etablissementId)
            StockUtilisesOpenHelper.insererUnStockUtilisesEnBDD(db, stockUtilises)
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, StockUtilisesOpenHelper.Constantes.TABLE_STOCK_UTILISE, stockUtilises.getphiwms_mobileUUID(), stockUtilises.getphiwms_mobileUUID(), DBOpenHelper.ActionsEAS.AJOUT)
            ElementASynchroniserOpenHelper.toutSynchroniser(this@ScannerPhotoPreparation, db, utilisateurConnecte, false)
        }

        /* on supprime les lignes déjà enregistrées qui ne sont pas les lignes de bases */
        var globalAPreparer : Int = phPreparationLigneCorrespondant?.qte_Demander ?: 0
        var phpreparationLigneCourant : PH_Preparation_Ligne?

        phpreparationLigneCourant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByProduitLotSerieNegPreparation(db,
            phPreparationLigneCorrespondant?.produitID ?: 0,
            phPreparationLigneCorrespondant?.preparationID ?: 0, stockLotEmplacementLight.lot, stockLotEmplacementLight.serie, stockLotEmplacementLight.emplacement
        )

        if(phpreparationLigneCourant != null)
        {
            globalAPreparer -= stockLotEmplacementLight.qte_Preparer
            phpreparationLigneCourant.qte_RAL = globalAPreparer
            phpreparationLigneCourant.qte_preparer = stockLotEmplacementLight.qte_Preparer
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, phpreparationLigneCourant)
        }
        else
        {
            phpreparationLigneCourant = PH_Preparation_Ligne(phPreparationLigneCorrespondant)
            val random = Random()
            var newId : Int = random.nextInt()
            if(newId > 0)
            {
                newId *= -1
            }

            phpreparationLigneCourant._UID = newId
            phpreparationLigneCourant.qte_Demander = globalAPreparer
            globalAPreparer -= stockLotEmplacementLight.qte_Preparer
            phpreparationLigneCourant.qte_RAL = globalAPreparer
            phpreparationLigneCourant.qte_preparer = stockLotEmplacementLight.qte_Preparer
            phpreparationLigneCourant.lotNumero = stockLotEmplacementLight.lot.trim()
            phpreparationLigneCourant.peremptionDate = stockLotEmplacementLight.peremptionDate
            phpreparationLigneCourant.zoneDepot = stockLotEmplacementLight.zone.trim()
            phpreparationLigneCourant.emplacementParDefaut = stockLotEmplacementLight.emplacement.trim()
            phpreparationLigneCourant.serieNumero = stockLotEmplacementLight.serie.trim()
            phpreparationLigneCourant._UID_4D = phPreparationLigneCorrespondant?._UID_4D

            PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, phpreparationLigneCourant)
        }
    }

}