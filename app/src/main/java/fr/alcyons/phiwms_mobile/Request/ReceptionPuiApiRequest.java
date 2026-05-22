package fr.alcyons.phiwms_mobile.Request;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.Callbacks.ReceptionApiCallbacks;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ViewModel.ReceptionPuiViewModel;

import static fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper.viderTableCommandes;

public class ReceptionPuiApiRequest extends JsonObjectRequest {

    private static final String TAG = "ReceptionPuiApiRequest";

    private final String token;

    private ReceptionPuiApiRequest(String url,
                                   String token,
                                   Context context,
                                   SQLiteDatabase db,
                                   ReceptionPuiViewModel viewModel,
                                   ReceptionApiCallbacks.OnSuccessCallback onSuccess,
                                   ReceptionApiCallbacks.OnErreurCallback onErreur) {
        super(Request.Method.GET, url, null,
                response -> traiterReponse(context, db, response, viewModel, onSuccess, onErreur),
                error -> {
                    Log.e(TAG, "Erreur réseau", error);
                    onErreur.onErreur(
                            "Veuillez contacter la société Alcyons ! \n Référence : Requete Service Reception PUI");
                });
        this.token = token;
    }

    @NonNull
    @Override
    public Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", token);
        return headers;
    }

    public static ReceptionPuiApiRequest creer(Context context,
                                               SQLiteDatabase db,
                                               String url,
                                               Utilisateur utilisateur,
                                               ReceptionPuiViewModel viewModel,
                                               ReceptionApiCallbacks.OnSuccessCallback onSuccess,
                                               ReceptionApiCallbacks.OnErreurCallback onErreur) {
        return new ReceptionPuiApiRequest(
                url, utilisateur.getToken(),
                context, db, viewModel, onSuccess, onErreur);
    }

    private static void traiterReponse(Context context,
                                       SQLiteDatabase db,
                                       JSONObject response,
                                       ReceptionPuiViewModel viewModel,
                                       ReceptionApiCallbacks.OnSuccessCallback onSuccess,
                                       ReceptionApiCallbacks.OnErreurCallback onErreur) {
        try {
            int resultCount = response.getInt("resultCount");
            if (resultCount == 0) {
                traiterErreurApi(context, response, onErreur);
                return;
            }
            persisterEtNotifier(db, response, viewModel, onSuccess);
        } catch (Throwable t) {
            Log.e(TAG, "Erreur parsing JSON", t);
        }
    }

    private static void traiterErreurApi(Context context,
                                         JSONObject response,
                                         ReceptionApiCallbacks.OnErreurCallback onErreur) throws Exception {
        String erreur = response.getString("erreur");
        if (erreur.equals(context.getString(R.string.tokenInvalide))) {
            onErreur.onErreur("Votre session de connexion est invalide, veuillez vous reconnecter");
        } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
            onErreur.onErreur("Votre session de connexion est expirée, veuillez vous reconnecter");
        } else if (erreur.equals("Aucun PH_Commande trouvé")) {
            onErreur.onErreur("Aucune réception PUI à traiter");
        } else {
            onErreur.onErreur(
                    "Veuillez contacter la société Alcyons ! \n Référence : Requete Service Reception PUI");
        }
    }

    private static void persisterEtNotifier(SQLiteDatabase db,
                                            JSONObject response,
                                            ReceptionPuiViewModel viewModel,
                                            ReceptionApiCallbacks.OnSuccessCallback onSuccess) throws Exception {
        JSONArray commandesJson = response.getJSONArray("PH_Commandes");

        viderTableCommandes(db);
        supprimerReliquatsExistants(db);

        List<Commande> commandes = new ArrayList<>();

        for (int i = 0; i < commandesJson.length(); i++) {
            JSONObject obj = commandesJson.getJSONObject(i);
            Commande commande = new Commande(obj);
            JSONArray reliquatsJson = obj.getJSONArray("ph_reliquat");

            boolean aDesReliquats = insererReliquats(db, reliquatsJson);
            if (aDesReliquats) {
                long rowId = CommandeOpenHelper.insererUneCommandeEnBDD(db, commande);
                if (rowId != -1) {
                    commandes.add(commande);
                }
            }
        }

        viewModel.setCommandes(commandes);
        new Handler(Looper.getMainLooper()).post(onSuccess::onTerminee);
    }

    private static void supprimerReliquatsExistants(SQLiteDatabase db) {
        List<PH_Reliquat> aSupprimer = PH_ReliquatOpenHelper.getPH_ReliquatBase(db);
        for (PH_Reliquat r : aSupprimer) {
            PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, r);
        }
    }

    private static boolean insererReliquats(SQLiteDatabase db, JSONArray reliquatsJson) throws Exception {
        boolean aDesReliquats = false;
        for (int j = 0; j < reliquatsJson.length(); j++) {
            PH_Reliquat reliquat = new PH_Reliquat(reliquatsJson.getJSONObject(j));
            PH_Reliquat existant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, reliquat.getReliquat_UID());
            if (existant != null) {
                PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, existant);
            }
            long id = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, reliquat);
            if (id != -1) aDesReliquats = true;
        }
        return aDesReliquats;
    }
}