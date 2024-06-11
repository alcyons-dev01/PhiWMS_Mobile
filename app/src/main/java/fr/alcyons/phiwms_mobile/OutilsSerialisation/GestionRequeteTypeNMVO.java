package fr.alcyons.phiwms_mobile.OutilsSerialisation;

public class GestionRequeteTypeNMVO {

    public static String getType(String code) {
        String requete_type = "Requête inconnue";

        switch (code) {
            case "G101":
                requete_type = "Récupérer.";
                break;
            case "G110":
                requete_type = "Vérifier";
                break;
            case "G115":
                requete_type = "Vérifier";
                break;
            case "G120":
                requete_type = "Dispenser";
                break;
            case "G121":
                requete_type = "Annuler";
                break;
            case "G122":
                requete_type = "Dispenser";
                break;
            case "G125":
                requete_type = "Dispenser";
                break;
            case "G127":
                requete_type = "Dispenser";
                break;
            case "G130":
                requete_type = "Détruire";
                break;
            case "G135":
                requete_type = "Détruire";
                break;
            case "G150":
                requete_type = "Dispenser";
                break;
            case "G151":
                requete_type = "Annuler";
                break;
            case "G155":
                requete_type = "Dispenser";
                break;
            case "G157":
                requete_type = "Dispenser";
                break;
            case "G188":
                requete_type = "Récupérer";
                break;
            case "G195":
                requete_type = "Envoyer";
                break;
            case "G196":
                requete_type = "Récupérer";
                break;
            case "G445":
                requete_type = "Modifier";
                break;
            case "G482":
                requete_type = "Télécharger";
                break;
            case "G483":
                requete_type = "Accepter";
                break;
            case "G615":
                requete_type = "Télécharger";
                break;
        }

        return requete_type;
    }
}
