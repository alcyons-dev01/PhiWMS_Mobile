package com.example.phiwms_mobile.Classes;

import java.io.Serializable;

/**
 * Created by olivier on 12/06/2019.
 */

public class ObjetReceptionPAD implements Serializable {

    private String designation_produit;
    private int quantite_commander;
    private int quantite_receptionner;
    private String referenceProduit;

    public ObjetReceptionPAD(String designation_produit, int quantite_commander, int quantite_receptionner, String referenceProduit)
    {
        this.designation_produit = designation_produit;
        this.quantite_commander = quantite_commander;
        this.quantite_receptionner = quantite_receptionner;
        this.referenceProduit = referenceProduit;
    }

    public ObjetReceptionPAD()
    {
        this.designation_produit = "";
        this.quantite_receptionner = 0;
        this.quantite_commander = 0;
    }

    public String getDesignation_produit() {
        return designation_produit;
    }

    public void setDesignation_produit(String designation_produit) {
        this.designation_produit = designation_produit;
    }

    public int getQuantite_commander() {
        return quantite_commander;
    }

    public void setQuantite_commander(int quantite_commander) {
        this.quantite_commander = quantite_commander;
    }

    public int getQuantite_receptionner() {
        return quantite_receptionner;
    }

    public void setQuantite_receptionner(int quantite_receptionner) {
        this.quantite_receptionner = quantite_receptionner;
    }

    public String getReferenceProduit() {
        return referenceProduit;
    }

    public void setReferenceProduit(String referenceProduit) {
        this.referenceProduit = referenceProduit;
    }
}
