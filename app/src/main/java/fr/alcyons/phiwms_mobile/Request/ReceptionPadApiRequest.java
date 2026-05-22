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

public class ReceptionPadApiRequest extends JsonObjectRequest {

    private static final String TAG = "ReceptionPadApiRequest";

    private final String token;

    private ReceptionPadApiRequest(String url,
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
                            "Veuillez contacter la société Alcyons ! \n Référence : HTTP Service Réception PAD");
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

    public static ReceptionPadApiRequest creer(Context context,
                                               SQLiteDatabase db,
                                               String url,
                                               Utilisateur utilisateur,
                                               ReceptionPuiViewModel viewModel,
                                               ReceptionApiCallbacks.OnSuccessCallback onSuccess,
                                               ReceptionApiCallbacks.OnErreurCallback onErreur) {
        return new ReceptionPadApiRequest(
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
            onErreur.onErreur("Votre session est invalide, veuillez vous reconnecter.");
        } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
            onErreur.onErreur("Votre session a expiré, veuillez vous reconnecter.");
        } else {
            onErreur.onErreur("Aucune réception PAD à traiter");
        }
    }

    private static void persisterEtNotifier(SQLiteDatabase db,
                                            JSONObject response,
                                            ReceptionPuiViewModel viewModel,
                                            ReceptionApiCallbacks.OnSuccessCallback onSuccess) throws Exception {
        JSONArray commandesJson = response.getJSONArray("PH_Commandes");

        viderTablesConcernees(db);

        List<Commande> commandes = new ArrayList<>();

        for (int i = 0; i < commandesJson.length(); i++) {
            JSONObject obj = commandesJson.getJSONObject(i);
            Commande commande = new Commande(obj);
            JSONArray reliquatsJson = obj.getJSONArray("ph_reliquat");

            boolean aDesReliquats = insererReliquats(db, reliquatsJson);
            if (aDesReliquats) {
                long rowId = CommandeOpenHelper.insererUneCommandeEnBDD(db, commande);
                if (rowId != -1 && commande.getRef_Depot_Dest().contains("-PAD")) {
                    commandes.add(commande);
                }
            }
        }

        viewModel.setCommandes(commandes);
        new Handler(Looper.getMainLooper()).post(onSuccess::onTerminee);
    }

    private static void viderTablesConcernees(SQLiteDatabase db) {
        for (Commande commande : CommandeOpenHelper.getAllCommandes(db)) {
            if (!commande.getNumero().contentEquals("RECALCYONS01")) {
                for (PH_Reliquat reliquat : PH_ReliquatOpenHelper
                        .getPH_ReliquatBaseByCommandeNumero(db, commande.getNumero())) {
                    PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, reliquat);
                }
                CommandeOpenHelper.supprimerUneCommande(db, commande);
            }
        }
    }

    private static boolean insererReliquats(SQLiteDatabase db, JSONArray reliquatsJson) throws Exception {
        boolean aDesReliquats = false;
        for (int j = 0; j < reliquatsJson.length(); j++) {
            PH_Reliquat reliquat = new PH_Reliquat(reliquatsJson.getJSONObject(j));
            long id = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, reliquat);
            if (id != -1) aDesReliquats = true;
        }
        return aDesReliquats;
    }
}