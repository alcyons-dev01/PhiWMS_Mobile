package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import fr.alcyons.phiwms_mobile.Classes.PH_Utiliser;
public class PH_UtiliserOpenHelper extends DBOpenHelper {

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PH_UTILISER="PH_Utiliser";
        public static final String CLE_COL__UID_PH_UTILISER="_UID";
        public static final int NUM_COL__UID_PH_UTILISER=1;
        public static final String TYPE_COL__UID_PH_UTILISER="INTEGER";
        public static final String CLE_COL_LAT_PH_UTILISER="lat";
        public static final int NUM_COL_LAT_PH_UTILISER=2;
        public static final String TYPE_COL_LAT_PH_UTILISER="REAL";
        public static final String CLE_COL_LNG_PH_UTILISER="lng";
        public static final int NUM_COL_LNG_PH_UTILISER=3;
        public static final String TYPE_COL_LNG_PH_UTILISER="REAL";
        public static final String CLE_COL_SYS_USER_ID_PH_UTILISER="SYS_USER_ID";
        public static final int NUM_COL_SYS_USER_ID_PH_UTILISER=4;
        public static final String TYPE_COL_SYS_USER_ID_PH_UTILISER="INTEGER";
        public static final String CLE_COL_QUANTITEUTILISEE_PH_UTILISER="quantiteUtilisee";
        public static final int NUM_COL_QUANTITEUTILISEE_PH_UTILISER=5;
        public static final String TYPE_COL_QUANTITEUTILISEE_PH_UTILISER="INTEGER";
        public static final String CLE_COL_SYS_DT_MAJ_PH_UTILISER="SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PH_UTILISER=6;
        public static final String TYPE_COL_SYS_DT_MAJ_PH_UTILISER="TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PH_UTILISER="SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PH_UTILISER=7;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PH_UTILISER="TEXT";
        public static final String CLE_COL_ZONEUID_PH_UTILISER="zoneUID";
        public static final int NUM_COL_ZONEUID_PH_UTILISER=8;
        public static final String TYPE_COL_ZONEUID_PH_UTILISER="INTEGER";
        public static final String CLE_COL_EMPLACEMENTUID_PH_UTILISER="emplacementUID";
        public static final int NUM_COL_EMPLACEMENTUID_PH_UTILISER=9;
        public static final String TYPE_COL_EMPLACEMENTUID_PH_UTILISER="INTEGER";
        public static final String CLE_COL_DEPOTUID_PH_UTILISER="depotUID";
        public static final int NUM_COL_DEPOTUID_PH_UTILISER=10;
        public static final String TYPE_COL_DEPOTUID_PH_UTILISER="INTEGER";
        public static final String CLE_COL_PHOTONOM_PH_UTILISER="photoNom";
        public static final int NUM_COL_PHOTONOM_PH_UTILISER=11;
        public static final String TYPE_COL_PHOTONOM_PH_UTILISER="TEXT";
        public static final String CLE_COL_PHOTOUID_PH_UTILISER="photoUID";
        public static final int NUM_COL_PHOTOUID_PH_UTILISER=12;
        public static final String TYPE_COL_PHOTOUID_PH_UTILISER="INTEGER";
        public static final String CLE_COL_PRODUITUID_PH_UTILISER="produitUID";
        public static final int NUM_COL_PRODUITUID_PH_UTILISER=13;
        public static final String TYPE_COL_PRODUITUID_PH_UTILISER="INTEGER";
        public static final String CLE_COL_UTILISATIONDATE_PH_UTILISER="utilisationDate";
        public static final int NUM_COL_UTILISATIONDATE_PH_UTILISER=14;
        public static final String TYPE_COL_UTILISATIONDATE_PH_UTILISER="TEXT";
        public static final String CLE_COL_UTILISATIONHEURE_PH_UTILISER="utilisationHeure";
        public static final int NUM_COL_UTILISATIONHEURE_PH_UTILISER=15;
        public static final String TYPE_COL_UTILISATIONHEURE_PH_UTILISER="TEXT";
        public static final String CLE_COL_CONTROLEEFFECTUE_PH_UTILISER="controleEffectue";
        public static final int NUM_COL_CONTROLEEFFECTUE_PH_UTILISER=16;
        public static final String TYPE_COL_CONTROLEEFFECTUE_PH_UTILISER="INTEGER";
        public static final String CLE_COL_LOT_PH_UTILISER="lot";
        public static final int NUM_COL_LOT_PH_UTILISER=17;
        public static final String TYPE_COL_LOT_PH_UTILISER="TEXT";
        public static final String CLE_COL_PEREMPTIONDATE_PH_UTILISER="peremptionDate";
        public static final int NUM_COL_PEREMPTIONDATE_PH_UTILISER=18;
        public static final String TYPE_COL_PEREMPTIONDATE_PH_UTILISER="TEXT";
        public static final String CLE_COL_CONTROLEQUANTITE_PH_UTILISER="controleQuantite";
        public static final int NUM_COL_CONTROLEQUANTITE_PH_UTILISER=19;
        public static final String TYPE_COL_CONTROLEQUANTITE_PH_UTILISER="INTEGER";
        public static final String CLE_COL_PRODUITSOUMISTRACABILITE_PH_UTILISER="produitSoumisTracabilite";
        public static final int NUM_COL_PRODUITSOUMISTRACABILITE_PH_UTILISER=20;
        public static final String TYPE_COL_PRODUITSOUMISTRACABILITE_PH_UTILISER="INTEGER";

        public static final String CREATION_TABLE_PH_UTILISER = " CREATE TABLE       " + Constantes.TABLE_PH_UTILISER
                +"("+
                DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID+"    PRIMARY KEY,"
                + Constantes.CLE_COL__UID_PH_UTILISER + "   " + Constantes.TYPE_COL__UID_PH_UTILISER + " , "
                + Constantes.CLE_COL_LAT_PH_UTILISER + "   " + Constantes.TYPE_COL_LAT_PH_UTILISER + " , "
                + Constantes.CLE_COL_LNG_PH_UTILISER + "   " + Constantes.TYPE_COL_LNG_PH_UTILISER + " , "
                + Constantes.CLE_COL_SYS_USER_ID_PH_UTILISER + "   " + Constantes.TYPE_COL_SYS_USER_ID_PH_UTILISER + " , "
                + Constantes.CLE_COL_QUANTITEUTILISEE_PH_UTILISER + "   " + Constantes.TYPE_COL_QUANTITEUTILISEE_PH_UTILISER + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_PH_UTILISER + "   " + Constantes.TYPE_COL_SYS_DT_MAJ_PH_UTILISER + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PH_UTILISER + "   " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PH_UTILISER + " , "
                + Constantes.CLE_COL_ZONEUID_PH_UTILISER + "   " + Constantes.TYPE_COL_ZONEUID_PH_UTILISER + " , "
                + Constantes.CLE_COL_EMPLACEMENTUID_PH_UTILISER + "   " + Constantes.TYPE_COL_EMPLACEMENTUID_PH_UTILISER + " , "
                + Constantes.CLE_COL_DEPOTUID_PH_UTILISER + "   " + Constantes.TYPE_COL_DEPOTUID_PH_UTILISER + " , "
                + Constantes.CLE_COL_PHOTONOM_PH_UTILISER + "   " + Constantes.TYPE_COL_PHOTONOM_PH_UTILISER + " , "
                + Constantes.CLE_COL_PHOTOUID_PH_UTILISER + "   " + Constantes.TYPE_COL_PHOTOUID_PH_UTILISER + " , "
                + Constantes.CLE_COL_PRODUITUID_PH_UTILISER + "   " + Constantes.TYPE_COL_PRODUITUID_PH_UTILISER + " , "
                + Constantes.CLE_COL_UTILISATIONDATE_PH_UTILISER + "   " + Constantes.TYPE_COL_UTILISATIONDATE_PH_UTILISER + " , "
                + Constantes.CLE_COL_UTILISATIONHEURE_PH_UTILISER + "   " + Constantes.TYPE_COL_UTILISATIONHEURE_PH_UTILISER + " , "
                + Constantes.CLE_COL_CONTROLEEFFECTUE_PH_UTILISER + "   " + Constantes.TYPE_COL_CONTROLEEFFECTUE_PH_UTILISER + " , "
                + Constantes.CLE_COL_LOT_PH_UTILISER + "   " + Constantes.TYPE_COL_LOT_PH_UTILISER + " , "
                + Constantes.CLE_COL_PEREMPTIONDATE_PH_UTILISER + "   " + Constantes.TYPE_COL_PEREMPTIONDATE_PH_UTILISER + " , "
                + Constantes.CLE_COL_CONTROLEQUANTITE_PH_UTILISER + "   " + Constantes.TYPE_COL_CONTROLEQUANTITE_PH_UTILISER + " , "
                + Constantes.CLE_COL_PRODUITSOUMISTRACABILITE_PH_UTILISER + "   " + Constantes.TYPE_COL_PRODUITSOUMISTRACABILITE_PH_UTILISER
                + " ); ";

    }

    public static long insererPH_UtiliserEnBDD(SQLiteDatabase db, PH_Utiliser objet){ContentValues contentValues=new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_PH_UTILISER, objet.get_UID());
        contentValues.put(Constantes.CLE_COL_LAT_PH_UTILISER, objet.getLat());
        contentValues.put(Constantes.CLE_COL_LNG_PH_UTILISER, objet.getLng());
        contentValues.put(Constantes.CLE_COL_SYS_USER_ID_PH_UTILISER, objet.getSYS_USER_ID());
        contentValues.put(Constantes.CLE_COL_QUANTITEUTILISEE_PH_UTILISER, objet.getQuantiteUtilisee());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PH_UTILISER, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PH_UTILISER, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_ZONEUID_PH_UTILISER, objet.getZoneUID());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENTUID_PH_UTILISER, objet.getEmplacementUID());
        contentValues.put(Constantes.CLE_COL_DEPOTUID_PH_UTILISER, objet.getDepotUID());
        contentValues.put(Constantes.CLE_COL_PHOTONOM_PH_UTILISER, objet.getPhotoNom());
        contentValues.put(Constantes.CLE_COL_PHOTOUID_PH_UTILISER, objet.getPhotoUID());
        contentValues.put(Constantes.CLE_COL_PRODUITUID_PH_UTILISER, objet.getProduitUID());
        contentValues.put(Constantes.CLE_COL_UTILISATIONDATE_PH_UTILISER, objet.getUtilisationDate());
        contentValues.put(Constantes.CLE_COL_UTILISATIONHEURE_PH_UTILISER, objet.getUtilisationHeure());
        contentValues.put(Constantes.CLE_COL_CONTROLEEFFECTUE_PH_UTILISER, objet.isControleEffectue());
        contentValues.put(Constantes.CLE_COL_LOT_PH_UTILISER, objet.getLot());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONDATE_PH_UTILISER, objet.getPeremptionDate());
        contentValues.put(Constantes.CLE_COL_CONTROLEQUANTITE_PH_UTILISER, objet.getControleQuantite());
        contentValues.put(Constantes.CLE_COL_PRODUITSOUMISTRACABILITE_PH_UTILISER, objet.isProduitSoumisTracabilite());
        long rowID=db.insert(Constantes.TABLE_PH_UTILISER, null, contentValues);
        objet.setphiwms_mobileUUID((int)rowID);
        return rowID;
    }

    public PH_UtiliserOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public static PH_Utiliser getPH_UtiliserByphiwms_mobileUUID(SQLiteDatabase db, int id){
        PH_Utiliser objet=null;
        Cursor cursor=db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_UTILISER + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID+"=? ", new String[]{String.valueOf(id)});
        if(cursor.getCount()==1){
            cursor.moveToFirst();
            objet=new PH_Utiliser(cursor);
        }
        return objet;
    }

}
