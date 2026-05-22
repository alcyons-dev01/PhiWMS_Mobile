package fr.alcyons.phiwms_mobile.Services;

import com.android.volley.toolbox.JsonObjectRequest;

import fr.alcyons.phiwms_mobile.Base.BaseReceptionActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.Callbacks.ReceptionApiCallbacks;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Request.ReceptionPuiApiRequest;
import fr.alcyons.phiwms_mobile.ViewModel.ReceptionPuiViewModel;

/**
 * Réception PUI — ne contient que ce qui est spécifique à ce service.
 * Tout le code commun est dans BaseReceptionActivity.
 */
public class ServiceReceptionPuiActivity extends BaseReceptionActivity {

    @Override
    protected Depot chargerDepot() {
        return DepotOpenHelper.getDepotPUI(db);
    }

    @Override
    protected String getNomService() {
        return "Réception PUI";
    }

    @Override
    protected JsonObjectRequest creerRequeteApi(String url,
                                                ReceptionPuiViewModel viewModel,
                                                ReceptionApiCallbacks.OnSuccessCallback onSuccess,
                                                ReceptionApiCallbacks.OnErreurCallback onErreur) {
        return ReceptionPuiApiRequest.creer(this, db, url, utilisateurConnecte, viewModel, onSuccess, onErreur);
    }
}