package fr.alcyons.phiwms_mobile.OutilsSerialisation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Parametres_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Parametres_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.AGL;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
public class Serialisation {

    protected static SQLiteDatabase db;
    protected static Context context;
    protected static Utilisateur utilisateur;

    public Serialisation(Context context, SQLiteDatabase db, Utilisateur utilisateur) {
        this.context = context;
        this.db = db;
        this.utilisateur = utilisateur;
    }

    // Création d'une nouvelle requête
    public static long Serialisation_Creer(int UserID, String reqType, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA, String MVT_Type, String MVT_UID) {

        long serialisationUID = 0;

        Parametres_Serialisation parametres_serialisation = Parametres_SerialisationOpenHelper.getParametres_Serialisation(db);
        String Serialisation_identifiant = "";
        String ClientTrxId = "";

        if (parametres_serialisation != null) {
            Serialisation_identifiant = parametres_serialisation.getFranceMVO_identifiant().replace("/", "-");
            if(utilisateur.getIdentifiant().contentEquals("ALCYONS"))
            {
                Serialisation_identifiant = "SWS/CHAU1001".replace("/", "-");
            }

            Date c = Calendar.getInstance().getTime();

            ClientTrxId = Serialisation_identifiant + '-' + reqType + '-' + AGL.AGL_AAMMJJ(c) + AGL.AGL_HHMMSS(c);
            serialisationUID = PH_Serialisation_Creer(UserID, reqType, ClientTrxId, ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA, MVT_Type, MVT_UID);
        }

        return serialisationUID;
    }

    // Insertion en base d'une nouvelle requête
    public static long PH_Serialisation_Creer(int UserID, String reqType, String ClientTrxId, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA, String MVT_Type, String MVT_UID) {
        long serialisationUID = 0;
        int ProduitUID = 0;
        if (ProductCode_SHEME_VA.contentEquals("GTIN")) {
            Produit produits = null;
            if(ProductCode_VALUE_VA.length() == 14)
                produits = ProduitOpenHelper.getUnProduitParGTIN(db, "01" + ProductCode_VALUE_VA);
            else
                produits = ProduitOpenHelper.getUnProduitParGTIN(db, ProductCode_VALUE_VA);
            if (produits != null)
                ProduitUID = produits.getID_produit();
            else
            {
                Random produitnRandom = new Random();
                int produitId = produitnRandom.nextInt();
                if (produitId > 0) {
                    produitId = produitId * -1;
                }
                String gtin = "01"+ProductCode_VALUE_VA;
                produits = new Produit(produitId, gtin, "Undefined", gtin, String.valueOf(utilisateur.getId()), gtin);
                ProduitOpenHelper.insererUnProduitEnBDD(db, produits);
                ProduitUID = produits.getID_produit();
            }
        }

        Random phSerialisationRandom = new Random();
        int phSerialisationID = phSerialisationRandom.nextInt();
        if (phSerialisationID > 0) {
            phSerialisationID = phSerialisationID * -1;
        }

        phSerialisationID = PH_SerialisationOpenHelper.getLastId(db)+1;

        PH_Serialisation phSerialisation = new PH_Serialisation(phSerialisationID, UserID, reqType, ClientTrxId, ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA, MVT_Type, MVT_UID, ProduitUID);
        serialisationUID = PH_SerialisationOpenHelper.insererPH_SerialisationEnBDD(db, phSerialisation);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, phSerialisation.getSerialexpressUUID(), phSerialisation.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);

        return serialisationUID;
    }

    // Vérifier
    public static long Serialisation_Verifier(int UserID, boolean muette, boolean differe, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA, String MVT_Type, String MVT_UID, String Nom_Document, String Type_Document) {
        long serialisationUID = 0;
        if (ProductCode_VALUE_VA.isEmpty() || ProductCode_SHEME_VA.isEmpty() || Batch_ID_VA.isEmpty() || Batch_EXPDATE_VA.isEmpty() || Pack_SN_VA.isEmpty()) {
            if (!muette)
            {
                String element_manquant = "";
                if(ProductCode_VALUE_VA.isEmpty())
                {
                    element_manquant = "GTIN manquant.";
                }
                else if(ProductCode_SHEME_VA.isEmpty())
                {
                    element_manquant = "Code SCHEME manquant.";
                }
                else if(Batch_ID_VA.isEmpty())
                {
                    element_manquant = "Numéro de lot manquant.";
                }
                else if(Batch_EXPDATE_VA.isEmpty())
                {
                    element_manquant = "Date de péremption manquante.";
                }
                else
                {
                    element_manquant = "Numéro de série manquant.";
                }

                Alerte.afficherAlerte(context, "Alerte", "Impossible de lancer la requête.\n"+element_manquant, "alerte");
            }
        } else {
            // Nous enregistrons dans le GTIN le AI 01, or pour les requêtes à France MVO il ne le faut pas
            if (ProductCode_VALUE_VA.length() == 16) {
                ProductCode_VALUE_VA = ProductCode_VALUE_VA.substring(2);
            }

            serialisationUID = Serialisation_Creer(UserID, "G110", ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA, MVT_Type, MVT_UID);

            if (!differe) {
                WS_SINGLE_PACK.NMVS_G110_verifySinglePack(context, db, utilisateur, (int) serialisationUID, ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA);
            }
        }
        return serialisationUID;
    }

    // Vérifier
    public static long Serialisation_VerifierG115(int UserID, boolean muette, boolean differe, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA, String MVT_Type, String MVT_UID, String Nom_Document, String Type_Document) {
        long serialisationUID = 0;
        if (ProductCode_VALUE_VA.isEmpty() || ProductCode_SHEME_VA.isEmpty() || Batch_ID_VA.isEmpty() || Batch_EXPDATE_VA.isEmpty() || Pack_SN_VA.isEmpty()) {
            if (!muette)
                Alerte.afficherAlerte(context, "Alerte", "Impossible de lancer la requête.\n Élément manquant", "alerte");
        } else {
            // Nous enregistrons dans le GTIN le AI 01, or pour les requêtes à France MVO il ne le faut pas
            if (ProductCode_VALUE_VA.length() == 16) {
                ProductCode_VALUE_VA = ProductCode_VALUE_VA.substring(2);
            }

            serialisationUID = Serialisation_Creer(UserID, "G115", ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA, MVT_Type, MVT_UID);

            if (!differe) {
                WS_SINGLE_PACK.NMVS_G110_verifySinglePack(context, db, utilisateur, (int) serialisationUID, ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA);
            }
        }
        return serialisationUID;
    }

    // Sérialiser
    public long Serialisation_Serialiser(int UserID, boolean muette, boolean differe, String action, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA, String MVT_Type, String MVT_UID) {
        long serialisationUID = 0;
        String reqType = "";

        if (ProductCode_VALUE_VA.isEmpty() || ProductCode_SHEME_VA.isEmpty() || Batch_ID_VA.isEmpty() || Batch_EXPDATE_VA.isEmpty() || Pack_SN_VA.isEmpty()) {
            if (!muette)
                Alerte.afficherAlerte(context, "Alerte", "Impossible de lancer la requête.\n Élément manquant", "alerte");
        } else {
            // Nous enregistrons dans le GTIN le AI 01, or pour les requêtes à France MVO il ne le faut pas
            if (ProductCode_VALUE_VA.length() == 16) {
                ProductCode_VALUE_VA.substring(2);
            }
            switch (action) {
                case "SUPPLIED":
                    reqType = "G120";
                    break;
                case "SAMPLE":
                    reqType = "G150";
                    break;
                case "DESTROYED":
                    reqType = "G130";
                    break;
            }

            serialisationUID = Serialisation_Creer(UserID, reqType, ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA, MVT_Type, MVT_UID);

            if (!differe) {
                switch (action) {
                    case "SUPPLIED":
                        WS_SINGLE_PACK.NMVS_G120_dispenseSinglePack(context, db, utilisateur, (int) serialisationUID, ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA);
                        break;
                    case "SAMPLE":
                        WS_SINGLE_PACK.NMVS_G150_sampleSinglePack(context, db, utilisateur, (int) serialisationUID, ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA);
                        break;
                    case "DESTROYED":
                        WS_SINGLE_PACK.NMVS_G130_destroySinglePack(context, db, utilisateur, (int) serialisationUID, ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA);
                        break;
                }
            }
        }
        return serialisationUID;
    }

    // Désérialiser
    public long Serialisation_Deserialiser(int UserID, boolean muette, boolean differe, String action, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA, String MVT_Type, String MVT_UID) {
        long serialisationUID = 0;
        String reqType = "";

        if (ProductCode_VALUE_VA.isEmpty() || ProductCode_SHEME_VA.isEmpty() || Batch_ID_VA.isEmpty() || Batch_EXPDATE_VA.isEmpty() || Pack_SN_VA.isEmpty()) {
            if (!muette)
                Alerte.afficherAlerte(context, "Alerte", "Impossible de lancer la requête.\n Élément manquant", "alerte");
        } else {
            // Nous enregistrons dans le GTIN le AI 01, or pour les requêtes à France MVO il ne le faut pas
            if (ProductCode_VALUE_VA.length() == 16) {
                ProductCode_VALUE_VA.substring(2);
            }
            switch (action) {
                case "SUPPLIED":
                    reqType = "G121";
                    break;
                case "SAMPLE":
                    reqType = "G151";
                    break;
            }

            serialisationUID = Serialisation_Creer(UserID, reqType, ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA, MVT_Type, MVT_UID);

            if (!differe) {
                switch (action) {
                    case "SUPPLIED":
                        WS_SINGLE_PACK.NMVS_G121_undoDispenseSinglePac(context, db, utilisateur, (int) serialisationUID, ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA);
                        break;
                    case "SAMPLE":
                        WS_SINGLE_PACK.NMVS_G151_undoSampleSinglePack(context, db, utilisateur, (int) serialisationUID, ProductCode_VALUE_VA, ProductCode_SHEME_VA, Batch_ID_VA, Batch_EXPDATE_VA, Pack_SN_VA);
                        break;
                }
            }
        }
        return serialisationUID;
    }
}
