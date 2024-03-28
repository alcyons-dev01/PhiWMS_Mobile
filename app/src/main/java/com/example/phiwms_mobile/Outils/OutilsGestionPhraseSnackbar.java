package com.example.phiwms_mobile.Outils;

/**
 * Created by olivier on 27/06/2019.
 */

public class OutilsGestionPhraseSnackbar {

    public static String obtenirPhraseSnackbar(String service)
    {
        String phrase = "";
        switch (service)
        {
            case "Destruction":
                phrase = "Aucune destruction détectée";
                break;
            case "Préparation PUF":
                phrase = "Aucune préparation PUF détectée";
                break;
            case "Préparation PAD":
                phrase = "Aucune préparation PAD détectée";
                break;
            case "Quarantaine":
                phrase = "Aucune quarantaine détectée";
                break;
            case "Réception PAD":
                phrase = "Aucune réception PAD détectée";
                break;
            case "Réception PUI":
                phrase = "Aucune réception PUI détectée";
                break;
            case "Retour fournisseur":
                phrase = "Aucun retour fournisseur détecté";
                break;
            case "Retour PUI":
                phrase = "Aucun retour PUI détecté";
                break;
            case "Verrou Pharmacie":
                phrase = "Aucun verrou pharmacie détecté";
                break;
            case "QuarantaineNonAcces":
                phrase = "Vous n'avez pas accès à ce service";
                break;
            case "EnCoursDeDeveloppement":
                phrase = "Service en cours de développement";
                break;
            case "AucuneZone":
                phrase = "Aucune zone disponible";
                break;
            case "Protocole":
                phrase = "Aucun protocole trouvé";
                break;
            case "Dotation":
                phrase = "Aucune dotation trouvée";
                break;
            case "Protocole PAD":
                phrase = "Aucun protocole PAD trouvé";
                break;
            case "Dotation Service":
                phrase = "Aucune dotation service trouvée";
                break;
            case "Dotation PAD":
                phrase = "Aucune dotation PAD trouvée";
                break;
            case "Demande Reassort":
                phrase = "Aucune demande réassort trouvée";
                break;
            case "Notification":
                phrase = "Vous n'avez pas de notification";
                break;
            case "Livraison":
                phrase = "Aucune livraison à traiter";
                break;
            case "NavigationService":
                phrase = "Service scanné inconnu";
                break;
            default:
                phrase = "Aucune ressource détectée";
                break;
        }

        return phrase;
    }
}
