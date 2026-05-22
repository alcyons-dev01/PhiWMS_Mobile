package fr.alcyons.phiwms_mobile.Services;

import android.os.Bundle;

import com.android.volley.toolbox.JsonObjectRequest;

import fr.alcyons.phiwms_mobile.Base.BaseReceptionActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.Callbacks.ReceptionApiCallbacks;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Request.ReceptionPadApiRequest;
import fr.alcyons.phiwms_mobile.ViewModel.ReceptionPuiViewModel;

import java.util.List;

/**
 * Réception PAD — ne contient que ce qui est spécifique à ce service.
 * Tout le code commun est dans BaseReceptionActivity.
 *
 * Spécificités PAD :
 * - Dépôt PUIPAD
 * - Ajoute serviceActuel.getId() dans le bundle d'ouverture du détail
 * - Ajoute getCommandeTestAlcyons() à la liste avant un rafraîchissement post-scan
 */
public class ServiceReceptionPadActivity extends BaseReceptionActivity {

    @Override
    protected Depot chargerDepot() {
        return DepotOpenHelper.getDepotPUIPAD(db);
    }

    @Override
    protected String getNomService() {
        return "Réception PAD";
    }

    @Override
    protected JsonObjectRequest creerRequeteApi(String url,
                                                ReceptionPuiViewModel viewModel,
                                                ReceptionApiCallbacks.OnSuccessCallback onSuccess,
                                                ReceptionApiCallbacks.OnErreurCallback onErreur) {
        return ReceptionPadApiRequest.creer(this, db, url, utilisateurConnecte, viewModel, onSuccess, onErreur);
    }

    @Override
    protected void enrichirBundleDetail(Bundle bundle, Commande commande) {
        bundle.putInt("serviceSelectionneID", serviceActuel.getId());
    }

    @Override
    protected void onAvantRafraichissement() {
        Commande commandeTest = CommandeOpenHelper.getCommandeTestAlcyons(db);
        if (commandeTest != null) {
            List<Commande> liste = viewModel.getCommandesVisibles().getValue();
            if (liste != null && !liste.contains(commandeTest)) {
                liste.add(commandeTest);
                viewModel.setCommandes(liste);
            }
        }
    }
}