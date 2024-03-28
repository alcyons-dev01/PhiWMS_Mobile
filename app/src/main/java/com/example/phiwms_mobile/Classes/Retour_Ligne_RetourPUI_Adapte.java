package com.example.phiwms_mobile.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quentinlanusse on 03/08/2017.
 */

public class Retour_Ligne_RetourPUI_Adapte implements Serializable {

    private int retourLigneID;
    private List<EmplacementAdapte> emplacementAdaptes;

    public Retour_Ligne_RetourPUI_Adapte(int retourLigne) {
        this.retourLigneID = retourLigne;
        this.emplacementAdaptes = new ArrayList<>();
    }

    public Retour_Ligne_RetourPUI_Adapte(int retourLigne, Depot_Emplacement emplacement, int qte) {
        this.retourLigneID = retourLigne;
        this.emplacementAdaptes = new ArrayList<>();
        this.emplacementAdaptes.add(new EmplacementAdapte(emplacement.get_UID(), qte));
    }

    public int getRetourLigneID() {
        return retourLigneID;
    }

    public void setRetourLigneID(int retourLigneID) {
        this.retourLigneID = retourLigneID;
    }

    public List<EmplacementAdapte> getEmplacementAdaptes() {
        return emplacementAdaptes;
    }

    public void setEmplacementAdaptes(List<EmplacementAdapte> emplacementAdaptes) {
        this.emplacementAdaptes = emplacementAdaptes;
    }

    public class EmplacementAdapte implements Serializable {
        private int qte;
        private int emplacementID;

        public EmplacementAdapte(int emplacementID, int qte) {
            this.qte = qte;
            this.emplacementID = emplacementID;
        }

        public int getQte() {
            return qte;
        }

        public void setQte(int qte) {
            this.qte = qte;
        }

        public int getEmplacementID() {
            return emplacementID;
        }

        public void setEmplacementID(int emplacementID) {
            this.emplacementID = emplacementID;
        }
    }
}
