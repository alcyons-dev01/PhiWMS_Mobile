package fr.alcyons.phimr4.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Lot_LigneOpenHelper;
import fr.alcyons.phimr4.Outils.OutilsGestionClasses;

import static fr.alcyons.phimr4.Outils.OutilsGestionClasses.recupererBooleen;

public class PH_Lot_Ligne implements Serializable, Comparable {

    private int UID;
    private int docLigneId;
    private String numLot;
    private String datePeremption;
    private int quantite;
    private String numSerie;
    private boolean verrouiller;
    private int phiMR4UUID = -1;

    public PH_Lot_Ligne(int UID, int docLigneId, String numLot, String datePeremption, int quantite, String numSerie)
    {
        this.UID = UID;
        this.docLigneId = docLigneId;
        this.numLot = numLot;
        this.datePeremption = datePeremption;
        this.quantite = quantite;
        this.numSerie = numSerie;
    }

    public PH_Lot_Ligne(JSONObject jsonObject) {
        try {
            this.UID = jsonObject.getInt("UID");
            this.docLigneId = jsonObject.getInt("DocLigneId");
            this.numLot = OutilsGestionClasses.recupererString(jsonObject.getString("NumLot"));
            this.datePeremption = OutilsGestionClasses.recupererString(jsonObject.getString("DatePeremption"));
            this.quantite = jsonObject.getInt("Quantite");
            this.numSerie = jsonObject.getString("NumSerie");
            this.verrouiller = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public PH_Lot_Ligne(Cursor cursor) {
        this.UID = cursor.getInt(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_UID);
        this.docLigneId = cursor.getInt(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_NUM_DOC);
        this.numLot = cursor.getString(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_NUM_LOT);
        this.datePeremption = cursor.getString(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_DATE_PEREMPTION);
        this.quantite = cursor.getInt(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_QUANTITE);
        this.numSerie = cursor.getString(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_NUM_SERIE);
        this.verrouiller = recupererBooleen(cursor, PH_Lot_LigneOpenHelper.Constantes.NUM_COL_NUM_VERROUILLER);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public int getdocLigneId() {
        return docLigneId;
    }

    public void setdocLigneId(int docLigneId) {
        this.docLigneId = docLigneId;
    }

    public String getNumLot() {
        return numLot;
    }

    public void setNumLot(String numLot) {
        this.numLot = numLot;
    }

    public String getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(String datePeremption) {
        this.datePeremption = datePeremption;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getDocLigneId() {
        return docLigneId;
    }

    public void setDocLigneId(int docLigneId) {
        this.docLigneId = docLigneId;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public boolean isVerrouiller() {
        return verrouiller;
    }

    public void setVerrouiller(boolean verrouiller) {
        this.verrouiller = verrouiller;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof PH_Lot_Ligne)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        PH_Lot_Ligne ph_lot_ligne = (PH_Lot_Ligne) obj;

        if (this.getPhiMR4UUID() == ph_lot_ligne.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getUID() > ph_lot_ligne.getUID() ? 1 : -1;
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UID", UID);
            jsonObject.put("docLigneId", docLigneId);
            jsonObject.put("numLot", numLot);
            jsonObject.put("datePeremption", datePeremption);
            jsonObject.put("quantite", quantite);
            jsonObject.put("numSerie", numSerie);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }
}
