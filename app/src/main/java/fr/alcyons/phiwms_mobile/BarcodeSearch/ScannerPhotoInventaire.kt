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
import fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Inventaire
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

class ScannerPhotoInventaire : Scanner() {
    var inventaireLigneList : List<Inventaire_Ligne_Temp> = ArrayList()
    var inventaireid : Int = 0
    lateinit var inventairecourant : Inventaire
    override val layoutResId: Int = R.layout.scanner
    var produit: Produit? = null
    private var isProcessing : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = findViewById<FrameLayout>(R.id.overlayContainer)
        layoutInflater.inflate(R.layout.layout_scanphoto_preparation, container, true)


        inventaireid = intent.getIntExtra("inventaireID",0)

        if(inventaireid != 0)
        {
            inventairecourant = InventaireOpenHelper.getInventaireById(db, inventaireid)
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCodeScanned(code: String) {
        if(!code.contentEquals("") && !isProcessing)
        {
            inventaireLigneList = Inventaire_Ligne_TempOpenHelper.getAllInventaireLigneTempByInventaire(db,inventaireid)

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
                val tabDateSQL = peremptionIdentification?.split("/")
                val datePeremptionSQL = tabDateSQL?.get(tabDateSQL.size-1)+"-"+tabDateSQL?.get(1)+"-"+tabDateSQL?.get(0)
                val numeroSerieIdentification = resultDecoupage["serie"]
                val emplacementIdentification = resultDecoupage["emplacement"]
                val codeinconnuIdentification = resultDecoupage["codeinconnu"]
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateDuJour = sdf.format(Date())
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
                    var ligneBase : Inventaire_Ligne_Temp? = null
                    var ligneCorrespondante : Inventaire_Ligne_Temp? = null
                    for(courante : Inventaire_Ligne_Temp in inventaireLigneList)
                    {
                        if(courante.produitID == produit?.iD_produit)
                        {
                            produitpresent = true
                            ligneBase = courante
                            if(courante.lot.contentEquals(numeroLotIdentification) && courante.peremptionDate.contentEquals(datePeremptionSQL)) {
                                ligneCorrespondante = courante
                                break
                            }
                        }
                    }

                    if(produitpresent)
                    {
                        val designationProduit: String? = produit?.designation_interne
                        val conditionnement = produit?.cond_distrib?.toInt().toString()
                        var quantiteinventaire = ligneBase?.stockPhysique?.toInt()

                        if(ligneCorrespondante?.inventaireDate.contentEquals("0000-00-00") || ligneCorrespondante?.inventaireDate.contentEquals("") || ligneCorrespondante?.inventaireDate.contentEquals("null"))
                            quantiteinventaire = 0

                        quantiteinventaire = quantiteinventaire?.plus(conditionnement.toInt())

                        //on peut modifier la quantité
                        withContext(Dispatchers.Main) {
                            (findViewById<View?>(R.id.progressBarQuantite) as ProgressBar).max = 10000
                            if (quantiteinventaire != null) {
                                (findViewById<View?>(R.id.progressBarQuantite) as ProgressBar).progress = quantiteinventaire
                            }
                            (findViewById<View?>(R.id.designationValidation) as TextView).text = designationProduit
                            (findViewById<View?>(R.id.quantiteValidation) as TextView).text = "$quantiteinventaire"
                            (findViewById<View?>(R.id.lotValidation) as TextView).text = numeroLotIdentification
                            (findViewById<View?>(R.id.peremptionValidation) as TextView).text = peremptionIdentification
                        }

                        if(ligneCorrespondante != null)
                        {
                            ligneCorrespondante?.stockPhysique = quantiteinventaire?.toDouble() ?: 0.0
                            ligneCorrespondante?.inventaireDate = dateDuJour

                            Inventaire_Ligne_TempOpenHelper.mettreAJourInventaireLigneTemp(db, ligneCorrespondante)

                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                                db,
                                Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP,
                                ligneCorrespondante?.phiMR4UUID ?: 0,
                                ligneCorrespondante?._UID ?: 0,
                                DBOpenHelper.ActionsEAS.MAJ
                            )
                            ElementASynchroniserOpenHelper.toutSynchroniser(
                                this@ScannerPhotoInventaire,
                                db,
                                utilisateurConnecte,
                                false
                            )

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
                            //création de l'inventaire ligne temp
                            val nouvelInventaireLigneTemp =
                                Inventaire_Ligne_Temp(ligneBase)

                            val randominventairelignetemp = Random()
                            var inventairelignetempid = randominventairelignetemp.nextInt()
                            if (inventairelignetempid > 0) inventairelignetempid *= -1

                            nouvelInventaireLigneTemp._UID = inventairelignetempid
                            nouvelInventaireLigneTemp.inventaireDate = dateDuJour
                            nouvelInventaireLigneTemp.stockPhysique = conditionnement.toDouble()
                            nouvelInventaireLigneTemp.lot = numeroLotIdentification
                            nouvelInventaireLigneTemp.isSynchroniser = false
                            nouvelInventaireLigneTemp.peremptionDate = datePeremptionSQL

                            nouvelInventaireLigneTemp.emplacement = ligneBase?.emplacement

                            Inventaire_Ligne_TempOpenHelper.insererUnInventaire_Ligne_TempEnBDD(
                                db,
                                nouvelInventaireLigneTemp
                            )
                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                                db,
                                Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP,
                                nouvelInventaireLigneTemp.getPhiMR4UUID(),
                                nouvelInventaireLigneTemp.get_UID(),
                                DBOpenHelper.ActionsEAS.AJOUT
                            )
                            ElementASynchroniserOpenHelper.toutSynchroniser(
                                this@ScannerPhotoInventaire,
                                db,
                                utilisateurConnecte,
                                false
                            )

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
}