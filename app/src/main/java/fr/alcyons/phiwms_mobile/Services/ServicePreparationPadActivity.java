package fr.alcyons.phiwms_mobile.Services;

import java.util.Comparator;
import java.util.List;

import fr.alcyons.phiwms_mobile.Base.BasePreparationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;

/**
 * Préparation PAD — spécificités :
 * - Endpoint uriRequetePreparationPAD
 * - viderTablesConcernees() préserve ALCYONS_LISTE et supprime les stocks liés
 * - Tri par date de livraison prévue uniquement
 * - Injecte getPreparationEssaiAlcyons() après chargement API
 */
public class ServicePreparationPadActivity extends BasePreparationActivity {

    @Override
    protected String getUrlRequete() {
        return ParametresServeurOpenHelper.getPartieCommuneUrls(db)
                + DBOpenHelper.Urls.uriRequetePreparationPAD;
    }

    @Override
    protected String getNomService() {
        return "Préparation PAD";
    }

    @Override
    protected String getGenre() {
        return "PAD";
    }

    @Override
    protected String getLabelTousDepots() {
        return "Tous";
    }

    @Override
    protected List<PH_Preparation> chargerDepuisBdd() {
        return PH_PreparationOpenHelper.getAllPHPreparationPreparationPAD(db);
    }

    @Override
    protected void trierListe(List<PH_Preparation> liste) {
        liste.sort(Comparator.comparing(PH_Preparation::getLivraisonPrevueDate));
    }

    /**
     * Après chargement API, injecte la préparation de test Alcyons si elle existe.
     */
    @Override
    protected void onApresChargementApi() {
        PH_Preparation preparationTest = PH_PreparationOpenHelper.getPreparationEssaiAlcyons(db);
        if (preparationTest != null) {
            ph_preparation_List.add(preparationTest);
        }
    }

    /**
     * Supprime les préparations PAD en préservant ALCYONS_LISTE,
     * et nettoie également les stocks liés à chaque ligne.
     */
    @Override
    protected void viderTablesConcernees() {
        for (PH_Preparation preparation : PH_PreparationOpenHelper.getAllPHPreparationPreparationPAD(db)) {
            if (!preparation.getListe().contentEquals("ALCYONS_LISTE")) {
                for (PH_Preparation_Ligne ligne : PH_Preparation_LigneOpenHelper
                        .getAllPHPreparationLignesBaseParPHPreparation(db, preparation)) {
                    PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ligne);

                    Produit produit = ProduitOpenHelper.getProduitByID(db, ligne.getProduitID());
                    Depot depot = DepotOpenHelper.getDepotParReference(
                            db, preparation.getDepotOrigineReference());

                    if (depot != null && produit != null) {
                        for (Stock_Lot_Emplacement_Light stock :
                                Stock_Lot_EmplacementLightOpenHelper
                                        .getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)) {
                            Stock_Lot_EmplacementLightOpenHelper.supprimerUnStockLotEmplacement(db, stock);
                        }
                    }
                }
                PH_PreparationOpenHelper.supprimerUnPhPreparation(db, preparation);
            }
        }
    }
}