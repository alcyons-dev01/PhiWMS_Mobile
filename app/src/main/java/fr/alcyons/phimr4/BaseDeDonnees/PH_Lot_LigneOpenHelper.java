package fr.alcyons.phimr4.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.Classes.PH_Lot_Ligne;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;

public class PH_Lot_LigneOpenHelper extends DBOpenHelper {


    public PH_Lot_LigneOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static List<PH_Lot_Ligne> getListePH_Lot_LigneByPreparationLigne(SQLiteDatabase db, int numDoc) {
        List<PH_Lot_Ligne> ph_lot_lignes = new ArrayList<>();

        Cursor cursorLotLigne = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_LOT_LIGNE + " WHERE "+ Constantes.CLE_COL_NUM_DOC + "= "+numDoc, null);

        while (cursorLotLigne.moveToNext()) {
            ph_lot_lignes.add(new PH_Lot_Ligne(cursorLotLigne));
        }

        cursorLotLigne.close();
        cursorLotLigne = null;
        return ph_lot_lignes;
    }

    public static  PH_Lot_Ligne getListePH_Lot_LigneByPreparationLigneLotSerie(SQLiteDatabase db, PH_Preparation_Ligne preparation_ligne, String lot, String serie)
    {
        PH_Lot_Ligne ph_lot_ligne = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_LOT_LIGNE + " WHERE "+ Constantes.CLE_COL_NUM_DOC + "=? AND "+Constantes.CLE_COL_NUM_LOT+" =? AND "+Constantes.CLE_COL_NUM_SERIE+" =?  ", new String[]{String.valueOf(preparation_ligne.get_UID()), String.valueOf(lot), String.valueOf(serie)});

        while (cursor.moveToNext()) {
            ph_lot_ligne = new PH_Lot_Ligne(cursor);
        }

        cursor.close();
        cursor = null;
        return ph_lot_ligne;

    }

    public static boolean CheckPH_Lot_Ligne(SQLiteDatabase db, PH_Lot_Ligne ph_lot_ligne)
    {
        boolean present = false;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_LOT_LIGNE + " WHERE "+ Constantes.CLE_COL_NUM_DOC + "=? AND "+Constantes.CLE_COL_NUM_LOT+" =? AND "+Constantes.CLE_COL_DATE_PEREMPTION+" =? AND "+Constantes.CLE_COL_QUANTITE+" =?", new String[]{String.valueOf(ph_lot_ligne.getdocLigneId()), String.valueOf(ph_lot_ligne.getNumLot()), String.valueOf(ph_lot_ligne.getDatePeremption()), String.valueOf(ph_lot_ligne.getQuantite())});

        if(cursor.getCount() > 0)
        {
            present = true;
        }

        cursor.close();
        cursor = null;
        return present;
    }

    public static long  supprimerPH_LotLigne(SQLiteDatabase db, int idPreparationLigne) {
        return db.delete(Constantes.TABLE_PH_LOT_LIGNE, Constantes.CLE_COL_NUM_DOC + "=?", new String[]{String.valueOf(idPreparationLigne)});
    }

    public static long insererUnPH_Lot_LigneBDD(SQLiteDatabase db, PH_Lot_Ligne ph_lot_ligne) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_UID, ph_lot_ligne.getUID());
        contentValues.put(Constantes.CLE_COL_NUM_DOC, ph_lot_ligne.getdocLigneId());
        contentValues.put(Constantes.CLE_COL_NUM_LOT, ph_lot_ligne.getNumLot());
        contentValues.put(Constantes.CLE_COL_DATE_PEREMPTION, ph_lot_ligne.getDatePeremption());
        contentValues.put(Constantes.CLE_COL_QUANTITE, ph_lot_ligne.getQuantite());
        contentValues.put(Constantes.CLE_COL_NUM_SERIE, ph_lot_ligne.getNumSerie());
        contentValues.put(Constantes.CLE_COL_NUM_VERROUILLER, ph_lot_ligne.isVerrouiller());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_PH_LOT_LIGNE, null, contentValues);

        ph_lot_ligne.setPhiMR4UUID((int) rowId);

        return rowId;
    }

    public static long mettreAJourPHLotLigne(SQLiteDatabase db, PH_Lot_Ligne ph_lot_ligne) {
        // Récupération des valeurs à mettre à jour
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_UID, ph_lot_ligne.getUID());
        contentValues.put(Constantes.CLE_COL_NUM_DOC, ph_lot_ligne.getdocLigneId());
        contentValues.put(Constantes.CLE_COL_NUM_LOT, ph_lot_ligne.getNumLot());
        contentValues.put(Constantes.CLE_COL_DATE_PEREMPTION, ph_lot_ligne.getDatePeremption());
        contentValues.put(Constantes.CLE_COL_QUANTITE, ph_lot_ligne.getQuantite());
        contentValues.put(Constantes.CLE_COL_NUM_SERIE, ph_lot_ligne.getNumSerie());
        contentValues.put(Constantes.CLE_COL_NUM_VERROUILLER, ph_lot_ligne.isVerrouiller());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID, ph_lot_ligne.getPhiMR4UUID());

        return db.update(Constantes.TABLE_PH_LOT_LIGNE, contentValues, DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " = " + String.valueOf(ph_lot_ligne.getPhiMR4UUID()), null);
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PH_LOT_LIGNE = "PH_Lot_Ligne";

        public static final String CLE_COL_UID = "UID";
        public static final int NUM_COL_UID = 1;
        public static final String TYPE_COL_UID = "INTEGER";

        public static final String CLE_COL_NUM_DOC = "DocLigneId";
        public static final int NUM_COL_NUM_DOC = 2;
        public static final String TYPE_COL_NUM_DOC = "INTEGER";

        public static final String CLE_COL_NUM_LOT = "NumLot";
        public static final int NUM_COL_NUM_LOT = 3;
        public static final String TYPE_COL_NUM_LOT = "TEXT";

        public static final String CLE_COL_DATE_PEREMPTION = "DatePeremption";
        public static final int NUM_COL_DATE_PEREMPTION = 4;
        public static final String TYPE_COL_DATE_PEREMPTION = "TEXT";

        public static final String CLE_COL_QUANTITE = "Quantite";
        public static final int NUM_COL_QUANTITE = 5;
        public static final String TYPE_COL_QUANTITE = "INTEGER";

        public static final String CLE_COL_NUM_SERIE = "NumSerie";
        public static final int NUM_COL_NUM_SERIE = 6;
        public static final String TYPE_COL_NUM_SERIE = "TEXT";

        public static final String CLE_COL_NUM_VERROUILLER = "Verrouiller";
        public static final int NUM_COL_NUM_VERROUILLER = 7;
        public static final String TYPE_COL_NUM_VERROUILLER = "INTEGER";

        public static final String CREATION_TABLE_PH_LOT_LIGNE = "CREATE TABLE "
                + PH_Lot_LigneOpenHelper.Constantes.TABLE_PH_LOT_LIGNE + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + PH_Lot_LigneOpenHelper.Constantes.CLE_COL_UID + " " + PH_Lot_LigneOpenHelper.Constantes.TYPE_COL_UID + ","
                + PH_Lot_LigneOpenHelper.Constantes.CLE_COL_NUM_DOC + " " + PH_Lot_LigneOpenHelper.Constantes.TYPE_COL_NUM_DOC + ","
                + PH_Lot_LigneOpenHelper.Constantes.CLE_COL_NUM_LOT + " " + PH_Lot_LigneOpenHelper.Constantes.TYPE_COL_NUM_LOT + ","
                + PH_Lot_LigneOpenHelper.Constantes.CLE_COL_DATE_PEREMPTION + " " + PH_Lot_LigneOpenHelper.Constantes.TYPE_COL_DATE_PEREMPTION + ","
                + PH_Lot_LigneOpenHelper.Constantes.CLE_COL_QUANTITE + " " + PH_Lot_LigneOpenHelper.Constantes.TYPE_COL_QUANTITE+ ","
                + PH_Lot_LigneOpenHelper.Constantes.CLE_COL_NUM_SERIE + " " + PH_Lot_LigneOpenHelper.Constantes.TYPE_COL_NUM_SERIE+ ","
                + PH_Lot_LigneOpenHelper.Constantes.CLE_COL_NUM_VERROUILLER + " " + PH_Lot_LigneOpenHelper.Constantes.TYPE_COL_NUM_VERROUILLER
                + ");";
    }
}
