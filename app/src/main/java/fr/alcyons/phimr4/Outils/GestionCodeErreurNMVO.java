package fr.alcyons.phimr4.Outils;

/**
 * Created by olivier on 26/02/2019.
 */

public class GestionCodeErreurNMVO {

    public static String getMessage(String code) {
        String message_erreur = "Erreur inconnue";
        switch (code) {
            case "NMVS_ERROR":
                message_erreur = "Une erreur technique générale s'est produite.";
                break;

            case "NMVS_FE_AU_01":
                message_erreur = "L'utilisateur et le mot de passe saisis ne correspondent pas.";
                break;

            case "NMVS_FE_AU_02":
                message_erreur = "Connexion impossible, l'utilisateur est verrouillé.";
                break;

            case "NMVS_FE_AU_03":
                message_erreur = "Une violation générale a été détectée.";
                break;

            case "NMVS_FE_AU_04":
                message_erreur = "Le client spécifié n'existe pas.";
                break;

            case "NMVS_FE_AU_05":
                message_erreur = "Nombre de tentatives de connexion maximal atteint. Le compte est verrouillé.";
                break;

            case "NMVS_FE_AU_06":
                message_erreur = "L'utilisateur et le mot de passe saisis ne correspondent pas.";
                break;

            case "NMVS_FE_AU_07":
                message_erreur = "Le client ne correspond pas à celui spécifié dans le fichier.";
                break;

            case "NMVS_FE_AU_08":
                message_erreur = "Ce client n'est pas autorisé à accéder aux numéros de produits entrés.";
                break;

            case "NMVS_FE_AU_09":
                message_erreur = "L'accès à ce numéro de série est impossible. L'utilisateur n'a pas d'habilitation pour le produit associé.";
                break;

            case "NMVS_FE_AU_10":
                message_erreur = "Le mot de passe n'est pas correct.";
                break;

            case "NMVS_FE_AU_11":
                message_erreur = "Le mot de passe saisi est expiré ou est un mot de passe initial.  Merci de renouveler votre mot de passe.";
                break;

            case "NMVS_FE_AU_12":
                message_erreur = "L'utilisateur n'a pas les droits sur le produit associé.";
                break;

            case "NMVS_FE_AU_13":
                message_erreur = "Données invalides pour l'usage du Hub européen.";
                break;

            case "NMVS_FE_AU_14":
                message_erreur = "ID EU Hub valide mais l'utilisation de l'interface n'est pas permise pendant cette période.";
                break;

            case "NMVS_FE_AU_15":
                message_erreur = "L'utilisateur n'a pas l'autorisation d'exécuter le processus pour ce code produit.";
                break;

            case "NMVS_FE_AU_17":
                message_erreur = "Les données de connexion ne sont pas valides.  Merci de contacter France MVO.";
                break;

            case "NMVS_FE_AU_18":
                message_erreur = "L'adresse email a déjà été utilisée par un autre administrateur.";
                break;

            case "NMVS_FE_AU_23":
                message_erreur = "Les termes et conditions n'ont pas été acceptés dans les délais impartis.Veuillez accepter les nouvelles conditions d'utilisation pour pouvoir accéder à nouveau au système.";
                break;

            case "NMVS_FE_AU_24":
                message_erreur = "Le changement de mot de passe n'est pas autorisé. Un nouveau mot de passe a déjà été attribué.";
                break;

            case "NMVS_FE_BR_01":
                message_erreur = "L'opération n'est pas permise pour ce processus métier.";
                break;

            case "NMVS_FE_CFG_01":
                message_erreur = "The entered MAH-ID already exists in the NMVS for another client.";
                break;

            case "NMVS_FE_CFG_02":
                message_erreur = "Un identifiant de connexion existe déjà avec le même préfixe.";
                break;

            case "NMVS_FE_CFG_03":
                message_erreur = "Un seul participant de ce type peut exister à la fois.";
                break;

            case "NMVS_FE_FAQ_01":
                message_erreur = "La catégorie de FAQ existe déjà.";
                break;

            case "NMVS_FE_FAQ_02":
                message_erreur = "La catégorie ne peut pas être retirée. Il reste des éléments dans cette catégorie de FAQ.";
                break;

            case "NMVS_FE_FAQ_03":
                message_erreur = "La catégorie de FAQ n'existe pas dans le système.";
                break;

            case "NMVS_FE_FAQ_05":
                message_erreur = "La catégorie de FAQ standard ne peut pas être supprimée.";
                break;

            case "NMVS_FE_FAQ_06":
                message_erreur = "La question de FAQ existe déjà dans le système.";
                break;

            case "NMVS_FE_FAQ_07":
                message_erreur = "La question de FAQ n'existe pas dans le système.";
                break;

            case "NMVS_FE_FI_01":
                message_erreur = "Le nom de fichier spécifié ne correspond pas à la règle.";
                break;

            case "NMVS_FE_FI_02":
                message_erreur = "La taille du fichier dépasse la limite autorisée.";
                break;

            case "NMVS_FE_FI_03":
                message_erreur = "Le nom du fichier existe déjà.";
                break;

            case "NMVS_FE_FI_04":
                message_erreur = "Le contenu du fichier existe déjà.";
                break;

            case "NMVS_FE_GR_01":
                message_erreur = "Le code produit est invalide.";
                break;

            case "NMVS_FE_GR_02":
                message_erreur = "La clé de contrôle de l'identifiant PZN inclus dans le code produit en format PPN est incorrecte.";
                break;

            case "NMVS_FE_GR_03":
                message_erreur = "Le préfixe inséré pour le code produit ne correspond pas au code du pays.";
                break;

            case "NMVS_FE_GR_04":
                message_erreur = "Le code produit est invalide.";
                break;

            case "NMVS_FE_LOT_01":
                message_erreur = "Le lot associé au numéro de série entré a fait l'objet d'un rappel de lots.";
                break;

            case "NMVS_FE_LOT_02":
                message_erreur = "Le lot sélectionné existe déjà avec une date d'expiration différente.";
                break;

            case "NMVS_FE_LOT_03":
                message_erreur = "Le lot sélectionné n'existe pas.";
                break;

            case "NMVS_FE_LOT_04":
                message_erreur = "Le lot sélectionné existe déjà avec une date d'expiration différente.";
                break;

            case "NMVS_FE_LOT_05":
                message_erreur = "Le lot sélectionné n'est pas 'verrouillé'.";
                break;

            case "NMVS_FE_LOT_06":
                message_erreur = "La date d'expiration saisie ne correspond pas à celle qui est stockée.";
                break;

            case "NMVS_FE_LOT_07":
                message_erreur = "La date d'expiration contient une valeur incorrecte.";
                break;

            case "NMVS_FE_LOT_08":
                message_erreur = "Le lot ou produit correspondant au numéro de série saisi a été retiré.";
                break;

            case "NMVS_FE_LOT_09":
                message_erreur = "Aucune boite de ce lot ne peut être verrouillée.";
                break;

            case "NMVS_FE_LOT_10":
                message_erreur = "Aucune boite de ce lot ne peut être déverrouillée.";
                break;

            case "NMVS_FE_PKI_01":
                message_erreur = "Le certificat est expiré.";
                break;

            case "NMVS_FE_PKI_05":
                message_erreur = "L'utilisateur n'a pas de certificat.";
                break;

            case "NMVS_FE_PKI_06":
                message_erreur = "Il n'y a pas de certificat valide pour cet utilisateur.";
                break;

            case "NMVS_FE_PKI_07":
                message_erreur = "Il n'y a pas de nouveau certificat pour cet utilisateur.";
                break;

            case "NMVS_FE_PKI_08":
                message_erreur = "L'utilisateur n'a pas de certificat valide sur le serveur.";
                break;

            case "NMVS_FE_PKI_09":
                message_erreur = "L'installation de ce certificat a été déjà validée.";
                break;

            case "NMVS_FE_PMS_01":
                message_erreur = "L'utilisateur existe déjà.";
                break;

            case "NMVS_FE_PMS_02":
                message_erreur = "Le nouveau mot de passe doit être différent du précédent.";
                break;

            case "NMVS_FE_PMS_03":
                message_erreur = "Le mot de passe entré ne peut pas être utilisé car il correspond à l'un des 20 derniers mots de passe utilisés.";
                break;

            case "NMVS_FE_PMS_04":
                message_erreur = "Le mot de passe entré ne respecte pas les règles de sécurité.";
                break;

            case "NMVS_FE_PMS_05":
                message_erreur = "Le nom d'utilisateur (SFTP) existe déjà pour l'interface intégrée.";
                break;

            case "NMVS_FE_PMS_06":
                message_erreur = "Le nom d'utilisateur (SFTP) existe déjà pour l'interface intégrée.";
                break;

            case "NMVS_FE_PMS_07":
                message_erreur = "L'ID EU Hub est déjà assigné pour l'environnement d'intégration.";
                break;

            case "NMVS_FE_PMS_08":
                message_erreur = "L'ID EU Hub est déjà assigné pour l'environnement de production.";
                break;

            case "NMVS_FE_PMS_09":
                message_erreur = "L'ID EU Hub est déjà assigné pour l'environnement de production.";
                break;

            case "NMVS_FE_PMS_10":
                message_erreur = "Les données du contrat sont incohérentes.  Une ou plusieurs dates de début des contrats sont plus tard que la date de fin correspondante.";
                break;

            case "NMVS_FE_PMS_11":
                message_erreur = "Il n'existe pas de client habilité.";
                break;

            case "NMVS_FE_PMS_12":
                message_erreur = "Le contrat a lui-même des contrats délégués. Un lien entre clients n'est pas permis.";
                break;

            case "NMVS_FE_PMS_13":
                message_erreur = "Le client est inactif.";
                break;

            case "NMVS_FE_PMS_14":
                message_erreur = "Le rôle n'existe pas dans le système.";
                break;

            case "NMVS_FE_REQ_01":
                message_erreur = "Ecrasement de la valeur interdit. La valeur est déjà attribuée.";
                break;

            case "NMVS_FE_REQ_02":
                message_erreur = "Les données d'entrée remplies ne correspondent pas au type de requête spécifié.";
                break;

            case "NMVS_FE_SE_CLI_01":
                message_erreur = "Le client ne peut être édité que dans l'environnement de production.";
                break;

            case "NMVS_FE_SE_CLI_02":
                message_erreur = "La synchronisation d'un client n'est possible que dans l'environnement d'intégration.";
                break;

            case "NMVS_FE_SE_CLI_03":
                message_erreur = "La synchronisation des clients vers l'environnement d'intégration a échoué.";
                break;

            case "NMVS_FE_SE_CLI_05":
                message_erreur = "La mise à jour du rapport de qualification en surveillant l'exécution du processus n'est possible que dans l'environnement d'Intégration.";
                break;

            case "NMVS_FE_SN_02":
                message_erreur = "Combination of serial number and Productcode already exists in the MAH system.";
                break;

            case "NMVS_FE_TX_01":
                message_erreur = "Le numéro de transaction ne correspond pas au numéro utilisé pour la dispensation.";
                break;

            case "NMVS_FE_TX_02":
                message_erreur = "L'identifiant de la transaction de masse saisi n'est pas disponible.";
                break;

            case "NMVS_FE_TX_03":
                message_erreur = "Le numéro de transaction ne correspond pas au numéro utilisé pour la destruction.";
                break;

            case "NMVS_FE_TX_04":
                message_erreur = "Le numéro de transaction ne correspond pas au numéro utilisé pour le chargement.";
                break;

            case "NMVS_FE_TX_05":
                message_erreur = "Le numéro de la transaction (la référence de la transaction originale) n'est pas valide pour l'annulation.";
                break;

            case "NMVS_FI_BAT_01":
                message_erreur = "Un processus est déjà en cours pour cette action.";
                break;

            case "NMVS_FI_CFG_01":
                message_erreur = "Le schéma de base de données sélectionné n'existe pas pour le client.";
                break;

            case "NMVS_FI_CFG_02":
                message_erreur = "Aucun schéma de base de données ne pouvait être identifié pour le client sélectionné.";
                break;

            case "NMVS_FI_CFG_03":
                message_erreur = "Le client pour ce produit est inconnu.";
                break;

            case "NMVS_FI_SE_CLI_01":
                message_erreur = "Frequency of permissible transmission exceeded.	Fréquence de transmission autorisée dépassée.";
                break;

            case "NMVS_NC_PC_01":
                message_erreur = "Code produit inconnu.";
                break;

            case "NMVS_NC_PC_02":
                message_erreur = "Numéro de série inconnu.";
                break;

            case "NMVS_NC_PC_03":
                message_erreur = "The entered product number is already assigned to another client.";
                break;

            case "NMVS_NC_PC_04":
                message_erreur = "Le numéro de produit entré est déjà déverrouillé pour un usage au niveau du Hub européen.";
                break;

            case "NMVS_NC_PC_05":
                message_erreur = "The entered product master data do not match with the stored product master data.";
                break;

            case "NMVS_NC_PC_06":
                message_erreur = "Le code du produit n'est pas disponible dans le système (pas d'alerte).";
                break;

            case "NMVS_NC_PC_07":
                message_erreur = "Des enregistrements alternatifs ou multiples pour le code du produit ont été trouvés.";
                break;

            case "NMVS_NC_PC_09":
                message_erreur = "Le changement du code du produit n'est pas autorisé.";
                break;

            case "NMVS_NC_PC_10":
                message_erreur = "Le GTIN n'est pas autorisé comme code de produit pour un produit national (produit non multimarché).";
                break;

            case "NMVS_NC_PC_11":
                message_erreur = "NHRN invalid.";
                break;

            case "NMVS_NC_PCK_01":
                message_erreur = "Le statut de la boîte n'est pas 'dispensable'.";
                break;

            case "NMVS_NC_PCK_02":
                message_erreur = "Le statut de la boite est 'Expirée'.";
                break;

            case "NMVS_NC_PCK_03":
                message_erreur = "Le statut de la boîte n'est pas 'dispensable'.";
                break;

            case "NMVS_NC_PCK_04":
                message_erreur = "Le statut de la boite n'est pas 'dispensée'.";
                break;

            case "NMVS_NC_PCK_05":
                message_erreur = "Le délai entre la dispensation de la boite et l'annulation de la transaction dépasse la limite maximale.";
                break;

            case "NMVS_NC_PCK_06":
                message_erreur = "Le statut actuel de la boite ne correspond pas à la transaction d'annulation (le statut actuel et le statut d'annulation doivent être équivalants).";
                break;

            case "NMVS_NC_PCK_07":
                message_erreur = "Le statut de la boite n'est pas 'Exportée'.";
                break;

            case "NMVS_NC_PCK_08":
                message_erreur = "La boite est déjà verrouillée pour dispensation.";
                break;

            case "NMVS_NC_PCK_09":
                message_erreur = "Les lots des numéros de série saisis sont expirés.";
                break;

            case "NMVS_NC_PCK_10":
                message_erreur = "Pas de numéro de série trouvé dans le chargement sélectionné.";
                break;

            case "NMVS_NC_PCK_11":
                message_erreur = "L'état de la boîte est 'dispensée', 'exportée' ou 'détruite'.";
                break;

            case "NMVS_NC_PCK_12":
                message_erreur = "Le numéro du produit sélectionné est déverrouillé pour utilisation via le Hub Européen.";
                break;

            case "NMVS_NC_PCK_13":
                message_erreur = "Le délai entre la destruction du numéro de série et l'annulation de la transaction dépasse la limite maximale.";
                break;

            case "NMVS_NC_PCK_14":
                message_erreur = "Le statut de la boite n'est pas verrouillé.";
                break;

            case "NMVS_NC_PCK_15":
                message_erreur = "Le numéro de produit sélectionné est verrouillé en raison du processus de prise en charge notifié.";
                break;

            case "NMVS_NC_PCK_16":
                message_erreur = "Le numéro du produit sélectionné n'est pas verrouillé.";
                break;

            case "NMVS_NC_PCK_17":
                message_erreur = "Aucun numéro de série de la requête n'a pu être validé.";
                break;

            case "NMVS_NC_PCK_18":
                message_erreur = "La boite a une propriété exclusive.";
                break;

            case "NMVS_NC_PCK_19":
                message_erreur = "La propriété est déjà définie pour la boite.";
                break;

            case "NMVS_NC_PCK_20":
                message_erreur = "Le délai entre le changement de cet attribut et son annulation est dépassé.";
                break;

            case "NMVS_NC_PCK_21":
                message_erreur = "L'annulation ne peut être réalisée que par l'utilisateur qui a changé l'attribut originalement.";
                break;

            case "NMVS_NC_PCK_22":
                message_erreur = "La boite est déjà inactive.";
                break;

            case "NMVS_NC_PCK_23":
                message_erreur = "Les paramètres par défaut de la propriété par double scan sont enregistrés.";
                break;

            case "NMVS_NC_PCK_24":
                message_erreur = "Au moins un numéro de série de la requête n'a pas pu être traité.";
                break;

            case "NMVS_NC_PCK_25":
                message_erreur = "Aucun historique n'est disponible pour la boite saisie.";
                break;

            case "NMVS_NC_PMS_01":
                message_erreur = "Le client n'existe pas dans France MVS.";
                break;

            case "NMVS_NC_PMS_02":
                message_erreur = "Ce client est déjà verrouillé.";
                break;

            case "NMVS_NC_PMS_03":
                message_erreur = "Ce client n'est pas verrouillé.";
                break;

            case "NMVS_NC_PMS_04":
                message_erreur = "Cet utilisateur n'existe pas pour ce client.";
                break;

            case "NMVS_NC_PMS_05":
                message_erreur = "Utilisateur déjà verrouillé.";
                break;

            case "NMVS_NC_PMS_06":
                message_erreur = "Utilisateur non verrouillé.";
                break;

            case "NMVS_NC_PMS_07":
                message_erreur = "Un utilisateur ne peut pas se verrouiller ou se supprimer lui-même.";
                break;

            case "NMVS_NC_PMS_08":
                message_erreur = "Ce client ne peut pas utiliser le Hub européen. La souscription à un service additionnel est nécessaire.";
                break;

            case "NMVS_NC_PMS_09":
                message_erreur = "Les changements ne peuvent pas être faits.  Au moins deux accès administrateur doivent exister.";
                break;

            case "NMVS_NC_PMS_10":
                message_erreur = "Le rôle existe déjà dans le système.";
                break;

            case "NMVS_NC_PMS_11":
                message_erreur = "Droits invalides pour le client.";
                break;

            case "NMVS_NC_PMS_12":
                message_erreur = "Le rôle doit être édité - uniquement le descriptif a été enregistré.";
                break;

            case "NMVS_NC_PMS_13":
                message_erreur = "Le token de sécurité n'existe pas.";
                break;

            case "NMVS_NC_PMS_14":
                message_erreur = "Le token de sécurité est expiré.";
                break;

            case "NMVS_NC_PMS_15":
                message_erreur = "Le token de sécurité a été déjà utilisé.";
                break;

            case "NMVS_NC_PMS_16":
                message_erreur = "Le nombre maximal de tentatives de réinitialisation du mot de passe est atteint.";
                break;

            case "NMVS_NC_PMS_17":
                message_erreur = "La tâche ne pouvait pas être affectée parce que le rôle nécessaire n'existe pas.";
                break;

            case "NMVS_NC_PMS_19":
                message_erreur = "Au moins un champ obligatoire est manquant.";
                break;

            case "NMVS_NC_PMS_20":
                message_erreur = "Nombre limite du nombre d'utilisateurs à créer est dépassé (Max : message_erreur = 5.000).";
                break;

            case "NMVS_NC_PMS_21":
                message_erreur = "Sub-User is not on the blacklist.";
                break;

            case "NMVS_NC_PMS_22":
                message_erreur = "Sub-User is already on the blacklist.";
                break;

            case "NMVS_NC_PMS_23":
                message_erreur = "La version actuelle des Conditions Générales a déjà été confirmée.";
                break;

            case "NMVS_NC_PMS_24":
                message_erreur = "Il n'y a pas de Conditions Générales disponibles. Si cela devait entraîner des problèmes, veuillez contacter votre NMVO responsable.";
                break;

            case "NMVS_TE_JS_01":
                message_erreur = "La structure JSON des données d'entrée n'est pas valide.";
                break;

            case "NMVS_TE_JS_02":
                message_erreur = "Les données d'entrée ne correspondent pas à la définition du schéma JSON.";
                break;

            case "NMVS_TE_REQ_02":
                message_erreur = "Aucun point de terminaison web service trouvé.";
                break;

            case "NMVS_TE_TX_01":
                message_erreur = "ID de transaction externe déjà utilisé.";
                break;

            case "NMVS_TE_TX_02":
                message_erreur = "L'ID de transaction a déjà été utilisé par l'industriel pour une importation.";
                break;

            case "NMVS_TE_XM_01":
                message_erreur = "La structure XML des données d'entrée est invalide.";
                break;

            case "NMVS_TE_XM_02":
                message_erreur = "Les données entrées ne correspondent pas à la définition du schéma XML.";
                break;

            case "NMVS_TI_AU_01":
                message_erreur = "L'authentification a échoué pour des raisons techniques.";
                break;

            case "NMVS_TI_CFG_01":
                message_erreur = "La création du client a échoué pour des raisons techniques.";
                break;

            case "NMVS_TI_CFG_02":
                message_erreur = "La création de l'utilisateur a échoué pour des raisons techniques.";
                break;

            case "NMVS_TI_RT_01":
                message_erreur = "Pas de processus associé à la requête.";
                break;

            case "NMVS_TI_RT_02":
                message_erreur = "Erreur de communication avec les composants centraux.";
                break;

            case "NMVS_TI_TO_01":
                message_erreur = "Le temps d'exécution du processus dépasse le temps maximal configuré.";
                break;

            case "NMVS_TI_XM_01":
                message_erreur = "Les données de sortie ne correspondent pas à la définition du schéma XML.";
                break;

            case "NMVS_WF_NC_01":
                message_erreur = "Ce processus ne peut pas être exécuté actuellement.";
                break;

            case "NMVS_WF_NC_02":
                message_erreur = "Ce rôle ne peut pas être affecté actuellement.";
                break;
        }

        return message_erreur;
    }

}
