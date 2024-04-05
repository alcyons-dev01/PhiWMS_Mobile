package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Inventaire_Ligne_Temp implements Serializable, Comparable {

    private int produitID;
    private String produitReference;
    private String fournisseurNom;
    private String categorie;
    private String designation;
    private double stockTheorique;
    private double stockPhysique;
    private String depotReference;
    private String _SYS_DT_MAJ;
    private String _SYS_HEURE_MAJ;
    private String _SYS_USER_MAJ;
    private String zone;
    private int Inventaire_ID;
    private Boolean _NePasImprimer;
    private double PUHT;
    private double tvaTx;
    private Boolean suspendu;
    private double valeurTTC;
    private double ecart;
    private String unite;
    private double Cond_Achat;
    private String classe;
    private String emplacement;
    private String lot;
    private String PeremptionDate;
    private int _UID;
    private int phiMR4UUID = -1;

    public Inventaire_Ligne_Temp(int produitID, String produitReference, String fournisseurNom, String categorie, String designation, double stockTheorique, double stockPhysique, String depotReference, String _SYS_DT_MAJ, String _SYS_HEURE_MAJ, String _SYS_USER_MAJ, String zone, int inventaire_ID, Boolean _NePasImprimer, double PUHT, double tvaTx, Boolean suspendu, double valeurTTC, double ecart, String unite, double cond_Achat, String classe, String emplacement, String lot, String peremptionDate, int _UID) {
        this.produitID = produitID;
        this.produitReference = produitReference;
        this.fournisseurNom = fournisseurNom;
        this.categorie = categorie;
        this.designation = designation;
        this.stockTheorique = stockTheorique;
        this.stockPhysique = stockPhysique;
        this.depotReference = depotReference;
        this._SYS_DT_MAJ = _SYS_DT_MAJ;
        this._SYS_HEURE_MAJ = _SYS_HEURE_MAJ;
        this._SYS_USER_MAJ = _SYS_USER_MAJ;
        this.zone = zone;
        this.Inventaire_ID = inventaire_ID;
        this._NePasImprimer = _NePasImprimer;
        this.PUHT = PUHT;
        this.tvaTx = tvaTx;
        this.suspendu = suspendu;
        this.valeurTTC = valeurTTC;
        this.ecart = ecart;
        this.unite = unite;
        this.Cond_Achat = cond_Achat;
        this.classe = classe;
        this.emplacement = emplacement;
        this.lot = lot;
        this.PeremptionDate = peremptionDate;
        this._UID = _UID;
    }

    public Inventaire_Ligne_Temp(JSONObject jsonObject) {
        try {
            this.produitID = jsonObject.getInt("produitID");
            this.produitReference = OutilsGestionClasses.recupererString(jsonObject.getString("produitReference"));
            this.fournisseurNom = OutilsGestionClasses.recupererString(jsonObject.getString("fournisseurNom"));
            this.categorie = OutilsGestionClasses.recupererString(jsonObject.getString("categorie"));
            this.designation = OutilsGestionClasses.recupererString(jsonObject.getString("designation"));
            this.stockTheorique = jsonObject.getDouble("stockTheorique");
            this.stockPhysique = jsonObject.getDouble("stockPhysique");
            this.depotReference = OutilsGestionClasses.recupererString(jsonObject.getString("depotReference"));
            this._SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("_SYS_DT_MAJ"));
            this._SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("_SYS_HEURE_MAJ"));
            this._SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("_SYS_USER_MAJ"));
            this.zone = OutilsGestionClasses.recupererString(jsonObject.getString("zone"));
            this.Inventaire_ID = jsonObject.getInt("Inventaire_ID");
            this._NePasImprimer = OutilsGestionClasses.recupererBooleen(jsonObject, "_NePasImprimer");
            this.PUHT = jsonObject.getDouble("PUHT");
            this.tvaTx = jsonObject.getDouble("tvaTx");
            this.suspendu = OutilsGestionClasses.recupererBooleen(jsonObject, "suspendu");
            this.valeurTTC = jsonObject.getDouble("valeurTTC");
            this.ecart = jsonObject.getDouble("ecart");
            this.unite = OutilsGestionClasses.recupererString(jsonObject.getString("unite"));
            this.Cond_Achat = jsonObject.getDouble("Cond_Achat");
            this.classe = OutilsGestionClasses.recupererString(jsonObject.getString("classe"));
            this.emplacement = OutilsGestionClasses.recupererString(jsonObject.getString("emplacement"));
            this.lot = OutilsGestionClasses.recupererString(jsonObject.getString("lot"));
            this.PeremptionDate = OutilsGestionClasses.recupererString(jsonObject.getString("PeremptionDate"));
            this._UID = jsonObject.getInt("_UID");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Inventaire_Ligne_Temp(Cursor cursor) {
        this.produitID = cursor.getInt(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_PRODUITID_INVENTAIRE_LIGNE_TEMP);
        this.produitReference = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_PRODUITREFERENCE_INVENTAIRE_LIGNE_TEMP);
        this.fournisseurNom = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_FOURNISSEURNOM_INVENTAIRE_LIGNE_TEMP);
        this.categorie = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_CATEGORIE_INVENTAIRE_LIGNE_TEMP);
        this.designation = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_DESIGNATION_INVENTAIRE_LIGNE_TEMP);
        this.stockTheorique = cursor.getDouble(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_STOCKTHEORIQUE_INVENTAIRE_LIGNE_TEMP);
        this.stockPhysique = cursor.getDouble(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_STOCKPHYSIQUE_INVENTAIRE_LIGNE_TEMP);
        this.depotReference = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE_TEMP);
        this._SYS_DT_MAJ = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE_TEMP);
        this._SYS_HEURE_MAJ = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE_TEMP);
        this._SYS_USER_MAJ = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE_TEMP);
        this.zone = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_ZONE_INVENTAIRE_LIGNE_TEMP);
        this.Inventaire_ID = cursor.getInt(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_INVENTAIRE_ID_INVENTAIRE_LIGNE_TEMP);
        this._NePasImprimer = OutilsGestionClasses.recupererBooleen(cursor, Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE_TEMP);
        this.PUHT = cursor.getDouble(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_PUHT_INVENTAIRE_LIGNE_TEMP);
        this.tvaTx = cursor.getDouble(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_TVATX_INVENTAIRE_LIGNE_TEMP);
        this.suspendu = OutilsGestionClasses.recupererBooleen(cursor, Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_SUSPENDU_INVENTAIRE_LIGNE_TEMP);
        this.valeurTTC = cursor.getDouble(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_VALEURTTC_INVENTAIRE_LIGNE_TEMP);
        this.ecart = cursor.getDouble(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_ECART_INVENTAIRE_LIGNE_TEMP);
        this.unite = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_UNITE_INVENTAIRE_LIGNE_TEMP);
        this.Cond_Achat = cursor.getDouble(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_COND_ACHAT_INVENTAIRE_LIGNE_TEMP);
        this.classe = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_CLASSE_INVENTAIRE_LIGNE_TEMP);
        this.emplacement = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_EMPLACEMENT_INVENTAIRE_LIGNE_TEMP);
        this.lot = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_LOT_INVENTAIRE_LIGNE_TEMP);
        this.PeremptionDate = cursor.getString(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL_PEREMPTIONDATE_INVENTAIRE_LIGNE_TEMP);
        this._UID = cursor.getInt(Inventaire_Ligne_TempOpenHelper.Constantes.NUM_COL__UID_INVENTAIRE_LIGNE_TEMP);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public Inventaire_Ligne_Temp(Produit produit, Map<String, String> gs1Decoupe, Depot depot) {
        this.produitID = produit.getID_produit();
        this.produitReference = "";
        this.fournisseurNom = produit.getFournisseur();
        this.categorie = produit.getCategorie();
        this.designation = produit.getDesignation_interne();
        this.stockTheorique = 0;
        if(gs1Decoupe.get(OutilsDecodage.conditionnementProduit).contentEquals(""))
            this.stockPhysique = 1;
        else
        {
            String conditionnement = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
            if(conditionnement.contains("@"))
            {
                String[] tab_conditionnement = conditionnement.split("@");
                conditionnement = tab_conditionnement[0];
            }
            this.stockPhysique = Integer.parseInt(conditionnement);
        }
        this.depotReference = depot.getDepot_Reference();
        this._SYS_DT_MAJ = "";
        this._SYS_HEURE_MAJ = "";
        this._SYS_USER_MAJ = "";
        this.Inventaire_ID = 0;
        this._NePasImprimer = false;
        this.PUHT = produit.getPrix_unitaire();
        this.tvaTx = produit.getTaux_de_TVA();
        this.suspendu = false;
        this.valeurTTC = 0;
        this.ecart = 0;
        this.unite = "";
        if(gs1Decoupe.get(OutilsDecodage.conditionnementProduit).contentEquals(""))
            this.Cond_Achat = 0;
        else
        {
            String conditionnement = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
            if(conditionnement.contains("@"))
            {
                String[] tab_conditionnement = conditionnement.split("@");
                conditionnement = tab_conditionnement[0];
            }
            this.Cond_Achat = Integer.parseInt(conditionnement);
        }
        this.classe = "";
        this.lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
        this.PeremptionDate = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
        this._UID = 0;
        if (depot.getDepot_Reference().contains("PAD")) {
            this.zone = produit.getZone_PAD_Defaut();
            this.emplacement = produit.getEmplacement_PAD_Defaut();
        } else if (depot.getDepot_Reference().contains("PUF")) {
            this.zone = produit.getZone_UF_Defaut();
            this.emplacement = produit.getEmplacement_UF_Defaut();
        } else {
            this.zone = produit.getZone_PUI_Defaut();
            this.emplacement = produit.getEmplacement_PUI_Defaut();
        }
    }

    public String getGS1(SQLiteDatabase db) {
        Produit produit = ProduitOpenHelper.getProduitByID(db, produitID);
        String new_gs1 = produit.getGTIN() + "17" + getPeremptionDate().substring(2).replace("-", "") + "10" + lot;
        if((int) Cond_Achat != 0)
        {
            new_gs1 = new_gs1+"@"+String.valueOf((int)Cond_Achat);
        }
        return new_gs1;
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getProduitID() {
        return produitID;
    }

    public void setProduitID(int produitID) {
        this.produitID = produitID;
    }

    public String getProduitReference() {
        return produitReference;
    }

    public void setProduitReference(String produitReference) {
        this.produitReference = produitReference;
    }

    public String getFournisseurNom() {
        return fournisseurNom;
    }

    public void setFournisseurNom(String fournisseurNom) {
        this.fournisseurNom = fournisseurNom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getStockTheorique() {
        return stockTheorique;
    }

    public void setStockTheorique(double stockTheorique) {
        this.stockTheorique = stockTheorique;
    }

    public double getStockPhysique() {
        return stockPhysique;
    }

    public void setStockPhysique(double stockPhysique) {
        this.stockPhysique = stockPhysique;
    }

    public String getDepotReference() {
        return depotReference;
    }

    public void setDepotReference(String depotReference) {
        this.depotReference = depotReference;
    }

    public String get_SYS_DT_MAJ() {
        return _SYS_DT_MAJ;
    }

    public void set_SYS_DT_MAJ(String _SYS_DT_MAJ) {
        this._SYS_DT_MAJ = _SYS_DT_MAJ;
    }

    public String get_SYS_HEURE_MAJ() {
        return _SYS_HEURE_MAJ;
    }

    public void set_SYS_HEURE_MAJ(String _SYS_HEURE_MAJ) {
        this._SYS_HEURE_MAJ = _SYS_HEURE_MAJ;
    }

    public String get_SYS_USER_MAJ() {
        return _SYS_USER_MAJ;
    }

    public void set_SYS_USER_MAJ(String _SYS_USER_MAJ) {
        this._SYS_USER_MAJ = _SYS_USER_MAJ;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getInventaire_ID() {
        return Inventaire_ID;
    }

    public void setInventaire_ID(int inventaire_ID) {
        Inventaire_ID = inventaire_ID;
    }

    public Boolean get_NePasImprimer() {
        return _NePasImprimer;
    }

    public void set_NePasImprimer(Boolean _NePasImprimer) {
        this._NePasImprimer = _NePasImprimer;
    }

    public double getPUHT() {
        return PUHT;
    }

    public void setPUHT(double PUHT) {
        this.PUHT = PUHT;
    }

    public double getTvaTx() {
        return tvaTx;
    }

    public void setTvaTx(double tvaTx) {
        this.tvaTx = tvaTx;
    }

    public Boolean getSuspendu() {
        return suspendu;
    }

    public void setSuspendu(Boolean suspendu) {
        this.suspendu = suspendu;
    }

    public double getValeurTTC() {
        return valeurTTC;
    }

    public void setValeurTTC(double valeurTTC) {
        this.valeurTTC = valeurTTC;
    }

    public double getEcart() {
        return ecart;
    }

    public void setEcart(double ecart) {
        this.ecart = ecart;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public double getCond_Achat() {
        return Cond_Achat;
    }

    public void setCond_Achat(double cond_Achat) {
        Cond_Achat = cond_Achat;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getPeremptionDate() {
        return PeremptionDate;
    }

    public void setPeremptionDate(String peremptionDate) {
        PeremptionDate = peremptionDate;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Inventaire_Ligne_Temp)) {
            valeurARetourner = false;
        } else {
            if (this.compareTo(obj) == 0) {
                valeurARetourner = true;
            }
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Inventaire_Ligne_Temp inventaire_ligne_temp = (Inventaire_Ligne_Temp) obj;

        if (this.getLot().equals(inventaire_ligne_temp.getLot()) && this.getPeremptionDate().equals(inventaire_ligne_temp.getPeremptionDate()) && this.produitID == inventaire_ligne_temp.getProduitID()) {
            return 0;
        } else {
            return this.get_UID() > inventaire_ligne_temp.get_UID() ? 1 : -1;
        }
    }

    public String toString() {
        return this.designation;
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("produitID", produitID);
            jsonObject.put("produitReference", produitReference);
            jsonObject.put("fournisseurNom", fournisseurNom);
            jsonObject.put("categorie", categorie);
            jsonObject.put("designation", designation);
            jsonObject.put("stockTheorique", stockTheorique);
            jsonObject.put("stockPhysique", stockPhysique);
            jsonObject.put("depotReference", depotReference);
            jsonObject.put("_SYS_DT_MAJ", _SYS_DT_MAJ);
            jsonObject.put("_SYS_HEURE_MAJ", _SYS_HEURE_MAJ);
            jsonObject.put("_SYS_USER_MAJ", _SYS_USER_MAJ);
            jsonObject.put("zone", zone);
            jsonObject.put("Inventaire_ID", Inventaire_ID);
            jsonObject.put("_NePasImprimer", _NePasImprimer);
            jsonObject.put("PUHT", PUHT);
            jsonObject.put("tvaTx", tvaTx);
            jsonObject.put("suspendu", suspendu);
            jsonObject.put("valeurTTC", valeurTTC);
            jsonObject.put("ecart", ecart);
            jsonObject.put("unite", unite);
            jsonObject.put("Cond_Achat", Cond_Achat);
            jsonObject.put("classe", classe);
            jsonObject.put("emplacement", emplacement);
            jsonObject.put("lot", lot);
            jsonObject.put("PeremptionDate", PeremptionDate);
            jsonObject.put("_UID", _UID);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }
}
