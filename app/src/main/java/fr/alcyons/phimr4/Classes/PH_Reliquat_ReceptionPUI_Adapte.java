package fr.alcyons.phimr4.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jessica on 05/12/2017.
 */

public class PH_Reliquat_ReceptionPUI_Adapte implements Serializable {

    private int phReliquatUID;
    private List<Lot> lotList;
    private boolean SuiviParSerieActif;
    private boolean SerialiserReception;
    private String Serie;

    public PH_Reliquat_ReceptionPUI_Adapte(int phReliquatUID, String Serie, boolean SuiviParSerieActif, boolean SerialiserReception) {
        this.phReliquatUID = phReliquatUID;
        this.SuiviParSerieActif = SuiviParSerieActif;
        this.SerialiserReception = SerialiserReception;
        this.Serie = Serie;
        this.lotList = new ArrayList<>();
    }

    public PH_Reliquat_ReceptionPUI_Adapte(int phReliquatUID, List<Lot> lotList) {
        this.phReliquatUID = phReliquatUID;
        this.lotList = new ArrayList<>();
        this.lotList = lotList;
    }

    public int getPhReliquatUID() {
        return phReliquatUID;
    }

    public void setPhReliquatUID(int phReliquatUID) {
        this.phReliquatUID = phReliquatUID;
    }

    public List<Lot> getlotList() {
        return lotList;
    }

    public void setlotList(List<Lot> lotList) {
        this.lotList = lotList;
    }

    public boolean isSuiviParSerieActif() {
        return SuiviParSerieActif;
    }

    public void setSuiviParSerieActif(boolean suiviParSerieActif) {
        SuiviParSerieActif = suiviParSerieActif;
    }

    public boolean isSerialiserReception() {
        return SerialiserReception;
    }

    public void setSerialiserReception(boolean serialiserReception) {
        SerialiserReception = serialiserReception;
    }

    public String getSerie() {
        return Serie;
    }

    public void setSerie(String serie) {
        Serie = serie;
    }

    public class Lot implements Serializable {

        private String numeroLot;
        private String datePeremption;
        private String numero_serie;
        private String resultat;

        private List<ZoneEtEmplacement> zoneEtEmplacementList;

        public Lot(String numeroLot, String datePeremption, String numero_serie, String resultat) {
            this.numeroLot = numeroLot;
            this.datePeremption = datePeremption;
            this.numero_serie = numero_serie;
            this.resultat = resultat;
            this.zoneEtEmplacementList = new ArrayList<>();
        }

        public String getNumeroLot() {
            return numeroLot;
        }

        public void setNumeroLot(String numeroLot) {
            this.numeroLot = numeroLot;
        }

        public String getDatePeremption() {
            return datePeremption;
        }

        public void setDatePeremption(String datePeremption) {
            this.datePeremption = datePeremption;
        }

        public String getNumero_serie() {
            return numero_serie;
        }

        public void setNumero_serie(String numero_serie) {
            this.numero_serie = numero_serie;
        }

        public List<ZoneEtEmplacement> getZoneEtEmplacementList() {
            return zoneEtEmplacementList;
        }

        public void setZoneEtEmplacementList(List<ZoneEtEmplacement> zoneEtEmplacementList) {
            this.zoneEtEmplacementList = zoneEtEmplacementList;
        }

        public String getResultat() {
            return resultat;
        }

        public void setResultat(String resultat) {
            this.resultat = resultat;
        }
    }

    public class ZoneEtEmplacement implements Serializable {

        private int zoneId;
        private String zoneName;
        private int emplacementId;
        private String emplacementName;
        private int quantite;

        public ZoneEtEmplacement(int zoneId, String zoneName, int emplacementId, String emplacementName, int quantite) {
            this.zoneId = zoneId;
            this.zoneName = zoneName;
            this.emplacementId = emplacementId;
            this.emplacementName = emplacementName;
            this.quantite = quantite;
        }

        public int getZoneId() {
            return zoneId;
        }

        public void setZoneId(int zoneId) {
            this.zoneId = zoneId;
        }

        public String getZoneName() {
            return zoneName;
        }

        public void setZoneName(String zoneName) {
            this.zoneName = zoneName;
        }

        public int getEmplacementId() {
            return emplacementId;
        }

        public void setEmplacementId(int emplacementId) {
            this.emplacementId = emplacementId;
        }

        public String getEmplacementName() {
            return emplacementName;
        }

        public void setEmplacementName(String emplacementName) {
            this.emplacementName = emplacementName;
        }

        public int getQuantite() {
            return quantite;
        }

        public void setQuantite(int quantite) {
            this.quantite = quantite;
        }
    }
}
