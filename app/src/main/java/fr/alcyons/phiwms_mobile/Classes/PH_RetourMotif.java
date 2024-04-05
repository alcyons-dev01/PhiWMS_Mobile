package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_RetourMotifOpenHelper;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by jessica on 29/11/2017.
 */

public class PH_RetourMotif implements Serializable, Comparable {

    private int _UID;
    private String motifRetour;
    private int phiMR4UUID = -1;

    public PH_RetourMotif(JSONObject jsonObject) {
        try {
            this._UID = jsonObject.getInt("_UID");
            this.motifRetour = OutilsGestionClasses.recupererString(jsonObject.getString("motifRetour"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public PH_RetourMotif(Cursor cursor) {
        this._UID = cursor.getInt(PH_RetourMotifOpenHelper.Constantes.NUM_COL__UID_PH_RETOURMOTIF);
        this.motifRetour = cursor.getString(PH_RetourMotifOpenHelper.Constantes.NUM_COL_MOTIFRETOUR_PH_RETOURMOTIF);
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public String getMotifRetour() {
        return motifRetour;
    }

    public void setMotifRetour(String motifRetour) {
        this.motifRetour = motifRetour;
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_UID", _UID);
            jsonObject.put("motifRetour", motifRetour);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        PH_RetourMotif ph_retourMotif = (PH_RetourMotif) obj;

        if (this.getPhiMR4UUID() == ph_retourMotif.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getPhiMR4UUID() > ph_retourMotif.getPhiMR4UUID() ? 1 : -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((PH_RetourMotif) obj).getPhiMR4UUID() == this.getPhiMR4UUID()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof PH_RetourMotif)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

}
