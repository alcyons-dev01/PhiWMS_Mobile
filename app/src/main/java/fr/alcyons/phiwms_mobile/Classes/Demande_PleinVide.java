package fr.alcyons.phiwms_mobile.Classes;

import java.io.Serializable;

public class Demande_PleinVide implements Serializable {

    private int depot_UID;
    private int dotation_UID;
    private int detailDot_UID;

    public Demande_PleinVide(int depot_UID, Detail_Dot detailDot) {
        this.depot_UID = depot_UID;
        this.dotation_UID = detailDot.getDotation_UID();
        this.detailDot_UID = detailDot.get_UID();
    }

    public int getDepot_UID() {
        return depot_UID;
    }

    public void setDepot_UID(int depot_UID) {
        this.depot_UID = depot_UID;
    }

    public int getDotation_UID() {
        return dotation_UID;
    }

    public void setDotation_UID(int dotation_UID) {
        this.dotation_UID = dotation_UID;
    }

    public int getDetailDot_UID() {
        return detailDot_UID;
    }

    public void setDetailDot_UID(int detailDot_UID) {
        this.detailDot_UID = detailDot_UID;
    }
}
