package fr.alcyons.phimr4.Outils;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by quentinlanusse on 19/06/2017.
 */

public class OutilsGestionClasses {

    public static boolean recupererBooleen(JSONObject objetJson, String nomValeur) {
        // Permet d'éviter de tenter d'insérer la valeur null dans un booléen
        boolean booleen;
        try {
            booleen = objetJson.getBoolean(nomValeur);
        } catch (JSONException exception) {
            try {
                int x = objetJson.getInt(nomValeur);
                if (x == 1) {
                    booleen = true;
                } else {
                    booleen = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                booleen = Boolean.parseBoolean(null);
            }
        }
        return booleen;
    }

    public static boolean recupererBooleen(Cursor cursor, int numColonne) {
        int valeur = cursor.getInt(numColonne);

        return valeur == 1;
    }

    public static String recupererString(String string) {
        return String.valueOf(string).equals("null") ? "" : string;
    }
}
