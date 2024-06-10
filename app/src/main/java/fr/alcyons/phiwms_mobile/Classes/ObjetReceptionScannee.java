package fr.alcyons.phiwms_mobile.Classes;

import java.io.Serializable;
public class ObjetReceptionScannee implements Serializable {

    private String gs1_scannee;
    private int emplacement_uid;
    private int quantiteScannee;
    private String resultat_france_mvo;
    private String referenceProduit;


    public ObjetReceptionScannee(String gs1_scannee, int emplacement_uid, int quantiteScannee, String resultat_france_mvo)
    {
        this.gs1_scannee = gs1_scannee;
        this.emplacement_uid = emplacement_uid;
        this.quantiteScannee = quantiteScannee;
        this.resultat_france_mvo = resultat_france_mvo;
    }

    public ObjetReceptionScannee(ObjetReceptionScannee objet)
    {
        this.gs1_scannee = objet.getGs1_scannee();
        this.emplacement_uid = objet.getEmplacement_uid();
        this.quantiteScannee = objet.getQuantiteScannee();
        this.resultat_france_mvo = objet.getResultat_france_mvo();
    }

    public ObjetReceptionScannee()
    {
        this.gs1_scannee = "";
        this.emplacement_uid = 0;
        this.quantiteScannee = 0;
        this.resultat_france_mvo = "";
    }

    public String getGs1_scannee() {
        return gs1_scannee;
    }

    public void setGs1_scannee(String gs1_scannee) {
        this.gs1_scannee = gs1_scannee;
    }

    public int getEmplacement_uid() {
        return emplacement_uid;
    }

    public void setEmplacement_uid(int emplacement_uid) {
        this.emplacement_uid = emplacement_uid;
    }

    public int getQuantiteScannee() {
        return quantiteScannee;
    }

    public void setQuantiteScannee(int quantiteScannee) {
        this.quantiteScannee = quantiteScannee;
    }

    public String getResultat_france_mvo() {
        return resultat_france_mvo;
    }

    public void setResultat_france_mvo(String resultat_france_mvo) {
        this.resultat_france_mvo = resultat_france_mvo;
    }

    public String getReferenceProduit() {
        return referenceProduit;
    }

    public void setReferenceProduit(String referenceProduit) {
        this.referenceProduit = referenceProduit;
    }
}
