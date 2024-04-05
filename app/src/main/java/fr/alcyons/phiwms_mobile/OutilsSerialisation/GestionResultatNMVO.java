package fr.alcyons.phiwms_mobile.OutilsSerialisation;

/**
 * Created by olivier on 27/02/2019.
 */

public class GestionResultatNMVO {

    public static String getResultat(String resultat) {
        String resultatFR = "";

        switch (resultat) {
            case "SUPPLIED":
                resultatFR = "Référence dispensée";
                break;
            case "DESTROYED":
                resultatFR = "Référence détruite";
                break;
            case "LOCKED":
                resultatFR = "Référence verrouillée";
                break;
            case "EXPORTED":
                resultatFR = "Référence exportée";
                break;
            case "SAMPLE":
                resultatFR = "Référence dispensée";
                break;
            case "STOLEN":
                resultatFR = "Référence volée";
                break;
            case "CHECKED_OUT":
                resultatFR = "Référence repacketée";
                break;
            case "FREESAMPLE":
                resultatFR = "Référence dispensée";
                break;
            case "RECALLED":
                resultatFR = "Référence rappelée";
                break;
            case "EXPIRED":
                resultatFR = "Référence expirée";
                break;
            case "WITHDRAWN":
                resultatFR = "Référence retirée";
                break;
        }

        return resultatFR;
    }

}
