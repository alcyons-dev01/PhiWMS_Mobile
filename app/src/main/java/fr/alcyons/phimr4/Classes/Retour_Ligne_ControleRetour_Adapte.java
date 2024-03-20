package fr.alcyons.phimr4.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quentinlanusse on 04/08/2017.
 */

public class Retour_Ligne_ControleRetour_Adapte implements Serializable {
    private int retourLigneID;
    private List<LotAdapte> lotAdaptes;

    public Retour_Ligne_ControleRetour_Adapte(int retourLigneID) {
        this.retourLigneID = retourLigneID;
        this.lotAdaptes = new ArrayList<>();
    }

    public Retour_Ligne_ControleRetour_Adapte(int retourLigneID, Stock_Lot_Emplacement_Light stockLotEmplacement) {
        LotAdapte lotAdapte = new LotAdapte(stockLotEmplacement);

        this.retourLigneID = retourLigneID;
        this.lotAdaptes = new ArrayList<>();
        this.lotAdaptes.add(lotAdapte);
    }

    public int getRetourLigneID() {
        return retourLigneID;
    }

    public void setRetourLigneID(int retourLigneID) {
        this.retourLigneID = retourLigneID;
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
        private String numSerie;
        private String datePeremption;
        private int qteActuelle;
        private int stockLotEmplacementID;


        public LotAdapte(String numLot)
        {
            this.numLot = numLot;
        }

        public LotAdapte(Stock_Lot_Emplacement_Light stockLotEmplacementLight) {
            this.numLot = stockLotEmplacementLight.getLot();
            this.numSerie = stockLotEmplacementLight.getSerie();
            this.datePeremption = stockLotEmplacementLight.getPeremptionDate();
            this.qteActuelle = (int) stockLotEmplacementLight.getQte();
            this.stockLotEmplacementID = stockLotEmplacementLight.get_UID();
        }

        public LotAdapte(ObjetPreparationScannee objetPreparationScannee) {
            this.numLot = objetPreparationScannee.getLot();
            this.numSerie = objetPreparationScannee.getSerie();
            this.datePeremption = objetPreparationScannee.getPeremptionDate();
            this.qteActuelle = (int) objetPreparationScannee.getQte();
            this.qteSaisie = (int) objetPreparationScannee.getQte();
            this.stockLotEmplacementID = objetPreparationScannee.get_UID();
        }


        public int getStockLotEmplacementID() {
            return stockLotEmplacementID;
        }

        public void setStockLotEmplacementID(int stockLotEmplacementID) {
            this.stockLotEmplacementID = stockLotEmplacementID;
        }

        public int getQteActuelle() {
            return qteActuelle;
        }

        public void setQteActuelle(int qteActuelle) {
            this.qteActuelle = qteActuelle;
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

        public String getNumSerie() {
            return numSerie;
        }

        public void setNumSerie(String numSerie) {
            this.numSerie = numSerie;
        }

        @Override
        public String toString() {
            return this.getNumLot();
        }

        @Override
        public int compareTo(Object obj) {
            LotAdapte lot = (LotAdapte) obj;

            int valeurARetourner;

            if (this.getQteSaisie() == ((LotAdapte) obj).getQteSaisie()) {

                if (this.getQteActuelle() == ((LotAdapte) obj).getQteActuelle()) {
                    valeurARetourner = 0;
                } else {
                    valeurARetourner = this.getQteActuelle() > ((LotAdapte) obj).getQteActuelle() ? -1 : 1;
                }
            } else {
                valeurARetourner = this.getQteSaisie() > ((LotAdapte) obj).getQteSaisie() ? -1 : 1;
            }
            return valeurARetourner;
        }
    }
}
