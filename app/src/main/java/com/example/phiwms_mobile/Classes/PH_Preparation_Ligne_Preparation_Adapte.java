package com.example.phiwms_mobile.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jessica on 28/09/2017.
 */

public class PH_Preparation_Ligne_Preparation_Adapte implements Serializable {
    private int ph_preparationLigneID;
    private List<LotAdapte> lotAdaptes;

    public PH_Preparation_Ligne_Preparation_Adapte(int ph_preparationLigneID) {
        this.ph_preparationLigneID = ph_preparationLigneID;
        this.lotAdaptes = new ArrayList<>();
    }

    public PH_Preparation_Ligne_Preparation_Adapte(int ph_preparationLigneID, Stock_Lot_Emplacement_Light stockLotEmplacement) {
        LotAdapte lotAdapte = new LotAdapte(stockLotEmplacement);

        this.ph_preparationLigneID = ph_preparationLigneID;
        this.lotAdaptes = new ArrayList<>();
        this.lotAdaptes.add(lotAdapte);
    }

    public int getPh_preparationLigneID() {
        return ph_preparationLigneID;
    }

    public void Ph_preparationLigneID(int ph_preparationLigneID) {
        this.ph_preparationLigneID = ph_preparationLigneID;
    }

    public List<LotAdapte> getLotAdaptes() {
        return lotAdaptes;
    }

    public void setLotAdaptes(List<LotAdapte> lotAdaptes) {
        this.lotAdaptes = lotAdaptes;
    }

    public class LotAdapte implements Serializable, Comparable {
        private int qteSaisie;
        private String numLot;
        private String datePeremption;
        private String zone;
        private String emplacement;
        private int qteStock;
        private String numSerie;
        private int stockLotEmplacementID;

        public LotAdapte(String numLot)
        {
            this.numLot = numLot;
        }

        public LotAdapte(Stock_Lot_Emplacement_Light stockLotEmplacementLight) {
            this.numLot = stockLotEmplacementLight.getLot();
            this.datePeremption = stockLotEmplacementLight.getPeremptionDate();
            this.qteStock = (int) stockLotEmplacementLight.getQte();
            this.stockLotEmplacementID = stockLotEmplacementLight.get_UID();
            this.zone = stockLotEmplacementLight.getZone();
            this.emplacement = stockLotEmplacementLight.getEmplacement();
            this.qteSaisie = stockLotEmplacementLight.getQte_Preparer();
            this.numSerie = stockLotEmplacementLight.getSerie();
        }

        public LotAdapte(ObjetPreparationScannee objetPreparationScannee) {
            this.numLot = objetPreparationScannee.getLot();
            this.datePeremption = objetPreparationScannee.getPeremptionDate();
            this.qteStock = (int) objetPreparationScannee.getQte();
            this.stockLotEmplacementID = objetPreparationScannee.get_UID();
            this.zone = objetPreparationScannee.getZone();
            this.emplacement = objetPreparationScannee.getEmplacement();
            this.qteSaisie = objetPreparationScannee.getQte_Preparer();
            this.numSerie = objetPreparationScannee.getSerie();
        }

        public int getStockLotEmplacementID() {
            return stockLotEmplacementID;
        }

        public void setStockLotEmplacementID(int stockLotEmplacementID) {
            this.stockLotEmplacementID = stockLotEmplacementID;
        }

        public int getQteStock() {
            return qteStock;
        }

        public void setQteStock(int qteStock) {
            this.qteStock = qteStock;
        }

        public int getQteSaisie() {
            return qteSaisie;
        }

        public void setQteSaisie(int qteSaisie) {
            this.qteSaisie = qteSaisie;
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

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        public String getEmplacement() {
            return emplacement;
        }

        public void setEmplacement(String emplacement) {
            this.emplacement = emplacement;
        }

        public String toString() {
            return this.getNumLot();
        }

        public String getNumSerie() {
            return numSerie;
        }

        public void setNumSerie(String numSerie) {
            this.numSerie = numSerie;
        }

        @Override
        public int compareTo(Object obj) {
            LotAdapte lot = (LotAdapte) obj;

            int valeurARetourner;

            if (this.getQteSaisie() == ((LotAdapte) obj).getQteSaisie()) {

                if (this.getQteStock() == ((LotAdapte) obj).getQteStock()) {
                    valeurARetourner = 0;
                } else {
                    valeurARetourner = this.getQteStock() > ((LotAdapte) obj).getQteStock() ? -1 : 1;
                }
            } else {
                valeurARetourner = this.getQteSaisie() > ((LotAdapte) obj).getQteSaisie() ? -1 : 1;
            }
            return valeurARetourner;
        }
    }
}
