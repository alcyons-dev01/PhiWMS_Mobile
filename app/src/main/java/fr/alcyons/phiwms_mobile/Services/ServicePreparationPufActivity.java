package fr.alcyons.phiwms_mobile.Services;

import java.util.List;

import fr.alcyons.phiwms_mobile.Base.BasePreparationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;

/**
 * Préparation PUF — spécificités :
 * - Endpoint uriRequetePreparationPUF
 * - viderTablesConcernees() supprime toutes les préparations PUF et leurs lignes
 * - Tri : urgents d'abord, puis par date de livraison prévue
 */
public class ServicePreparationPufActivity extends BasePreparationActivity {

    @Override
    protected String getUrlRequete() {
        return ParametresServeurOpenHelper.getPartieCommuneUrls(db)
                + DBOpenHelper.Urls.uriRequetePreparationPUF;
    }

    @Override
    protected String getNomService() {
        return "Préparation PUF";
    }

    @Override
    protected String getGenre() {
        return "PUF";
    }

    @Override
    protected String getLabelTousDepots() {
        return "Tous les dépôts";
    }

    @Override
    protected List<PH_Preparation> chargerDepuisBdd() {
        return PH_PreparationOpenHelper.getAllPHPreparationPreparationPUF(db);
    }

    @Override
    protected void trierListe(List<PH_Preparation> liste) {
        liste.sort((a, b) -> {
            int c = Boolean.compare(b.isURGENT(), a.isURGENT());
            if (c != 0) return c;
            return a.getLivraisonPrevueDate().compareTo(b.getLivraisonPrevueDate());
        });
    }

    /**
     * Supprime toutes les préparations PUF et leurs lignes associées.
     */
    @Override
    protected void viderTablesConcernees() {
        for (PH_Preparation preparation : PH_PreparationOpenHelper.getAllPHPreparationPreparationPUF(db)) {
            for (PH_Preparation_Ligne ligne : PH_Preparation_LigneOpenHelper
                    .getAllPHPreparationLignesParPHPreparation(db, preparation)) {
                PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ligne);
            }
            PH_PreparationOpenHelper.supprimerUnPhPreparation(db, preparation);
        }
    }
}