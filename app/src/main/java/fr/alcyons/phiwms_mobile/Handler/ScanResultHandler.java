package fr.alcyons.phiwms_mobile.Handler;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Helper.NavigationHelper;
import fr.alcyons.phiwms_mobile.Reception.DetailReception_V2;

/**
 * Encapsule la logique de navigation après un scan de document.
 * Avant refactoring : ce bloc if/else était copié 3-4 fois dans le
 * ActivityResultLauncher de ServiceReceptionPuiActivity.
 */
public final class ScanResultHandler {

    public interface AdapterCallback {
        void gestionAdapter();
        List<Commande> getCommandeList();
        Bundle getBaseBundle();
    }

    private final Context context;
    private final SQLiteDatabase db;
    private final int utilisateurId;
    private final AdapterCallback callback;

    public ScanResultHandler(Context context,
                             SQLiteDatabase db,
                             int utilisateurId,
                             AdapterCallback callback) {
        this.context = context;
        this.db = db;
        this.utilisateurId = utilisateurId;
        this.callback = callback;
    }

    /**
     * Appelé lorsqu'un numéro de document est scanné (ou null si scan annulé).
     *
     * @param numeroDocument le code scanné, ou null
     * @param nomServiceVide nom du service affiché si la liste est vide (pour NavigationActivity)
     * @param vide           tableau [0] mis à true si la liste est vide (side-effect volontaire)
     */
    public void traiter(String numeroDocument, String nomServiceVide, boolean[] vide) {
        if (numeroDocument != null) {
            Commande commandeTrouvee = CommandeOpenHelper.getCommandeByNumero(db, numeroDocument);
            if (commandeTrouvee != null) {
                ouvrirDetailReception(commandeTrouvee);
                return;
            }
            // Code scanné inconnu : on reste sur la liste
        }
        // Scan annulé ou document inconnu → refresh de la liste
        rafraichirOuRediriger(nomServiceVide, vide);
    }

    private void rafraichirOuRediriger(String nomServiceVide, boolean[] vide) {
        callback.gestionAdapter();
        if (callback.getCommandeList().isEmpty()) {
            vide[0] = true;
            NavigationHelper.allerVersNavigation(context, utilisateurId);
        }
    }

    private void ouvrirDetailReception(Commande commande) {
        Intent intent = new Intent(context, DetailReception_V2.class);
        Bundle bundle = callback.getBaseBundle();
        bundle.putInt("commandeID_Selectionne", commande.getID_commande());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}