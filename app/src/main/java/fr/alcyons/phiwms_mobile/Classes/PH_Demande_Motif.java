package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONObject;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Demande_MotifOpenHelper;

public class PH_Demande_Motif {

    private int id;
    private String motif;
    private int ordre;
    private int etablissementUID;

    public PH_Demande_Motif(int id, String motif, int ordre, int etablissementUID)
    {
        this.id = id;
        this.motif = motif;
        this.ordre = ordre;
        this.etablissementUID = etablissementUID;
    }

    public PH_Demande_Motif(Cursor cursorDemandeMotif) {
        this.id = cursorDemandeMotif.getInt(PH_Demande_MotifOpenHelper.Constantes.NUM_COL_ID_DEMANDE_MOTIF);
        this.motif = cursorDemandeMotif.getString(PH_Demande_MotifOpenHelper.Constantes.NUM_COL_MOTIF_DEMANDE_MOTIF);
        this.ordre = cursorDemandeMotif.getInt(PH_Demande_MotifOpenHelper.Constantes.NUM_COL_ORDRE_DEMANDE_MOTIF);
        this.etablissementUID = cursorDemandeMotif.getInt(PH_Demande_MotifOpenHelper.Constantes.NUM_COL_ETABLISSEMENT_UID_DEMANDE_MOTIF);
    }

    public PH_Demande_Motif(JSONObject demandemotifJson) {
        this.id = demandemotifJson.optInt("id");
        this.motif = demandemotifJson.optString("motif");
        this.ordre = demandemotifJson.optInt("ordre");
        this.etablissementUID = demandemotifJson.optInt("Etablissement_UID");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public int getEtablissementUID() {
        return etablissementUID;
    }

    public void setEtablissementUID(int etablissementUID) {
        this.etablissementUID = etablissementUID;
    }

    @Override
    public String toString() {
        return "PH_Demande_Motif{" +
                "id=" + id +
                ", motif='" + motif + '\'' +
                ", ordre=" + ordre +
                ", etablissementUID=" + etablissementUID +
                '}';
    }
}
