package fr.alcyons.phiwms_mobile.Classes;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;

public class Produit_Utiliser_Adapte implements Serializable, Comparable {

    private int id;
    private int qte;
    private String codeGS1;
    private String numLot;
    private String datePeremption;
    private int zoneID;
    private int emplacementID;
    private String imageNom;
    private Bitmap image;

    public Produit_Utiliser_Adapte(SQLiteDatabase db, Produit produit, String gs1, Depot depot) {
        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1);
        if (gs1Decoupe.size() != 0) {
            this.id = produit.getID_produit();
            this.qte = 1;
            this.codeGS1 = gs1;
            this.datePeremption = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
            this.numLot = gs1Decoupe.get(OutilsDecodage.numeroLot);
            this.imageNom = "";

            if (depot.getDepot_Reference().contains("PAD")) {
                Depot_Zone zone = ZoneOpenHelper.getZoneByDepotEtNom(db, depot, produit.getZone_PAD_Defaut());

                if (zone != null) {
                    Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone, produit.getEmplacement_PAD_Defaut());

                    this.zoneID = zone.getZoneID();
                    this.emplacementID = emplacement.get_UID();
                }
            } else if (depot.getDepot_Reference().contains("UF")) {
                Depot_Zone zone = ZoneOpenHelper.getZoneByDepotEtNom(db, depot, produit.getZone_UF_Defaut());
                if (zone != null) {
                    Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone, produit.getEmplacement_UF_Defaut());

                    this.zoneID = zone.getZoneID();
                    this.emplacementID = emplacement.get_UID();
                }
            } else {
                Depot_Zone zone = ZoneOpenHelper.getZoneByDepotEtNom(db, depot, produit.getZone_PUI_Defaut());
                if (zone != null) {
                    Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone, produit.getEmplacement_PUI_Defaut());

                    this.zoneID = zone.getZoneID();
                    this.emplacementID = emplacement.get_UID();
                }
            }
        }
    }

    public Produit_Utiliser_Adapte(String imageNom) {
        Date date = new Date();
        this.id = (int) date.getTime();
        this.qte = 1;
        this.codeGS1 = String.valueOf(date.getTime());
        this.datePeremption = "";
        this.numLot = "";
        this.zoneID = 0;
        this.emplacementID = 0;
        this.imageNom = imageNom;
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

    public int getZoneID() {
        return zoneID;
    }

    public void setZoneID(int zoneID) {
        this.zoneID = zoneID;
    }

    public int getEmplacementID() {
        return emplacementID;
    }

    public void setEmplacementID(int emplacementID) {
        this.emplacementID = emplacementID;
    }

    public String getCodeGS1() {
        return codeGS1;
    }

    public void setCodeGS1(String codeGS1) {
        this.codeGS1 = codeGS1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public String getImage() {
        return imageNom;
    }

    public void setImage(String image) {
        this.imageNom = image;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        Produit_Utiliser_Adapte produitUtiliserAdapte = (Produit_Utiliser_Adapte) obj;
        if (produitUtiliserAdapte != null) {
            if (produitUtiliserAdapte.getId() == this.getId()) {
                valeurARetourner = true;
            }
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        if (this.getId() == ((Produit_Utiliser_Adapte) obj).getId()) {
            return 0;
        }

        return this.getId() > ((Produit_Utiliser_Adapte) obj).getId() ? 1 : -1;
    }
}
