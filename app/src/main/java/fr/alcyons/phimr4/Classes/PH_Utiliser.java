package fr.alcyons.phimr4.Classes;

import android.database.Cursor;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phimr4.BaseDeDonnees.PH_UtiliserOpenHelper;

import static fr.alcyons.phimr4.Outils.OutilsGestionClasses.recupererBooleen;
import static fr.alcyons.phimr4.Outils.OutilsGestionClasses.recupererString;

/**
 * Created by jessica on 21/06/2018.
 */

public class PH_Utiliser implements Serializable, Comparable {

    int _UID;
    double lat;
    double lng;
    int SYS_USER_ID;
    int quantiteUtilisee;
    String SYS_DT_MAJ;
    String SYS_HEURE_MAJ;
    int zoneUID;
    int emplacementUID;
    int depotUID;
    String photoNom;
    int photoUID;
    int produitUID;
    String utilisationDate;
    String utilisationHeure;
    boolean controleEffectue;
    String lot;
    String peremptionDate;
    int controleQuantite;
    boolean produitSoumisTracabilite;
    private int phiMR4UUID=-1;

    public PH_Utiliser(int _UID, double lat, double lng, int quantiteUtilisee, int zoneUID, int emplacementUID, int depotUID, String photoNom, int photoUID, int produitUID, String utilisationDate, String utilisationHeure, boolean controleEffectue, String lot, String peremptionDate, int controleQuantite, boolean produitSoumisTracabilite){
        this._UID = _UID;
        this.lat = lat;
        this.lng = lng;
        this.quantiteUtilisee = quantiteUtilisee;
        this.zoneUID = zoneUID;
        this.emplacementUID = emplacementUID;
        this.depotUID = depotUID;
        this.photoNom = photoNom;
        this.photoUID = photoUID;
        this.produitUID = produitUID;
        this.utilisationDate = utilisationDate;
        this.utilisationHeure = utilisationHeure;
        this.controleEffectue = controleEffectue;
        this.lot = lot;
        this.peremptionDate = peremptionDate;
        this.controleQuantite = controleQuantite;
        this.produitSoumisTracabilite = produitSoumisTracabilite;
    }

    public PH_Utiliser(JSONObject jsonObject){
        try{
            _UID=jsonObject.getInt("_UID");
            lat=jsonObject.getDouble("lat");
            lng=jsonObject.getDouble("lng");
            SYS_USER_ID=jsonObject.getInt("SYS_USER_ID");
            quantiteUtilisee=jsonObject.getInt("quantiteUtilisee");
            SYS_DT_MAJ=recupererString(jsonObject.getString("SYS_DT_MAJ"));
            SYS_HEURE_MAJ=recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            zoneUID=jsonObject.getInt("zoneUID");
            emplacementUID=jsonObject.getInt("emplacementUID");
            depotUID=jsonObject.getInt("depotUID");
            photoNom=recupererString(jsonObject.getString("photoNom"));
            photoUID=jsonObject.getInt("photoUID");
            produitUID=jsonObject.getInt("produitUID");
            utilisationDate=recupererString(jsonObject.getString("utilisationDate"));
            utilisationHeure=recupererString(jsonObject.getString("utilisationHeure"));
            controleEffectue=recupererBooleen(jsonObject,"controleEffectue");
            lot=recupererString(jsonObject.getString("lot"));
            peremptionDate=recupererString(jsonObject.getString("peremptionDate"));
            controleQuantite=jsonObject.getInt("controleQuantite");
            produitSoumisTracabilite=recupererBooleen(jsonObject,"produitSoumisTracabilite");
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public PH_Utiliser(Cursor cursor){
        _UID=cursor.getInt(PH_UtiliserOpenHelper.Constantes.NUM_COL__UID_PH_UTILISER);
        lat=cursor.getDouble(PH_UtiliserOpenHelper.Constantes.NUM_COL_LAT_PH_UTILISER);
        lng=cursor.getDouble(PH_UtiliserOpenHelper.Constantes.NUM_COL_LNG_PH_UTILISER);
        SYS_USER_ID=cursor.getInt(PH_UtiliserOpenHelper.Constantes.NUM_COL_SYS_USER_ID_PH_UTILISER);
        quantiteUtilisee=cursor.getInt(PH_UtiliserOpenHelper.Constantes.NUM_COL_QUANTITEUTILISEE_PH_UTILISER);
        SYS_DT_MAJ=cursor.getString(PH_UtiliserOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PH_UTILISER);
        SYS_HEURE_MAJ=cursor.getString(PH_UtiliserOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PH_UTILISER);
        zoneUID=cursor.getInt(PH_UtiliserOpenHelper.Constantes.NUM_COL_ZONEUID_PH_UTILISER);
        emplacementUID=cursor.getInt(PH_UtiliserOpenHelper.Constantes.NUM_COL_EMPLACEMENTUID_PH_UTILISER);
        depotUID=cursor.getInt(PH_UtiliserOpenHelper.Constantes.NUM_COL_DEPOTUID_PH_UTILISER);
        photoNom=cursor.getString(PH_UtiliserOpenHelper.Constantes.NUM_COL_PHOTONOM_PH_UTILISER);
        photoUID=cursor.getInt(PH_UtiliserOpenHelper.Constantes.NUM_COL_PHOTOUID_PH_UTILISER);
        produitUID=cursor.getInt(PH_UtiliserOpenHelper.Constantes.NUM_COL_PRODUITUID_PH_UTILISER);
        utilisationDate=cursor.getString(PH_UtiliserOpenHelper.Constantes.NUM_COL_UTILISATIONDATE_PH_UTILISER);
        utilisationHeure=cursor.getString(PH_UtiliserOpenHelper.Constantes.NUM_COL_UTILISATIONHEURE_PH_UTILISER);
        controleEffectue=recupererBooleen(cursor, PH_UtiliserOpenHelper.Constantes.NUM_COL_CONTROLEEFFECTUE_PH_UTILISER);
        lot=cursor.getString(PH_UtiliserOpenHelper.Constantes.NUM_COL_LOT_PH_UTILISER);
        peremptionDate=cursor.getString(PH_UtiliserOpenHelper.Constantes.NUM_COL_PEREMPTIONDATE_PH_UTILISER);
        controleQuantite=cursor.getInt(PH_UtiliserOpenHelper.Constantes.NUM_COL_CONTROLEQUANTITE_PH_UTILISER);
        produitSoumisTracabilite=recupererBooleen(cursor, PH_UtiliserOpenHelper.Constantes.NUM_COL_PRODUITSOUMISTRACABILITE_PH_UTILISER);
    }

    public JSONObject toJson(){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("_UID", _UID);
            jsonObject.put("lat", lat);
            jsonObject.put("lng", lng);
            jsonObject.put("SYS_USER_ID", SYS_USER_ID);
            jsonObject.put("quantiteUtilisee", quantiteUtilisee);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("zoneUID", zoneUID);
            jsonObject.put("emplacementUID", emplacementUID);
            jsonObject.put("depotUID", depotUID);
            jsonObject.put("photoNom", photoNom);
            jsonObject.put("photoUID", photoUID);
            jsonObject.put("produitUID", produitUID);
            jsonObject.put("utilisationDate", utilisationDate);
            jsonObject.put("utilisationHeure", utilisationHeure);
            jsonObject.put("controleEffectue", controleEffectue);
            jsonObject.put("lot", lot);
            jsonObject.put("peremptionDate", peremptionDate);
            jsonObject.put("controleQuantite", controleQuantite);
            jsonObject.put("produitSoumisTracabilite", produitSoumisTracabilite);
        }catch(JSONException e){
            e.printStackTrace();
            jsonObject=null;
        }
        return jsonObject;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getSYS_USER_ID() {
        return SYS_USER_ID;
    }

    public void setSYS_USER_ID(int SYS_USER_ID) {
        this.SYS_USER_ID = SYS_USER_ID;
    }

    public int getQuantiteUtilisee() {
        return quantiteUtilisee;
    }

    public void setQuantiteUtilisee(int quantiteUtilisee) {
        this.quantiteUtilisee = quantiteUtilisee;
    }

    public String getSYS_DT_MAJ() {
        return SYS_DT_MAJ;
    }

    public void setSYS_DT_MAJ(String SYS_DT_MAJ) {
        this.SYS_DT_MAJ = SYS_DT_MAJ;
    }

    public String getSYS_HEURE_MAJ() {
        return SYS_HEURE_MAJ;
    }

    public void setSYS_HEURE_MAJ(String SYS_HEURE_MAJ) {
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
    }

    public int getZoneUID() {
        return zoneUID;
    }

    public void setZoneUID(int zoneUID) {
        this.zoneUID = zoneUID;
    }

    public int getEmplacementUID() {
        return emplacementUID;
    }

    public void setEmplacementUID(int emplacementUID) {
        this.emplacementUID = emplacementUID;
    }

    public int getDepotUID() {
        return depotUID;
    }

    public void setDepotUID(int depotUID) {
        this.depotUID = depotUID;
    }

    public String getPhotoNom() {
        return photoNom;
    }

    public void setPhotoNom(String photoNom) {
        this.photoNom = photoNom;
    }

    public int getPhotoUID() {
        return photoUID;
    }

    public void setPhotoUID(int photoUID) {
        this.photoUID = photoUID;
    }

    public int getProduitUID() {
        return produitUID;
    }

    public void setProduitUID(int produitUID) {
        this.produitUID = produitUID;
    }

    public String getUtilisationDate() {
        return utilisationDate;
    }

    public void setUtilisationDate(String utilisationDate) {
        this.utilisationDate = utilisationDate;
    }

    public String getUtilisationHeure() {
        return utilisationHeure;
    }

    public void setUtilisationHeure(String utilisationHeure) {
        this.utilisationHeure = utilisationHeure;
    }

    public boolean isControleEffectue() {
        return controleEffectue;
    }

    public void setControleEffectue(boolean controleEffectue) {
        this.controleEffectue = controleEffectue;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getPeremptionDate() {
        return peremptionDate;
    }

    public void setPeremptionDate(String peremptionDate) {
        this.peremptionDate = peremptionDate;
    }

    public int getControleQuantite() {
        return controleQuantite;
    }

    public void setControleQuantite(int controleQuantite) {
        this.controleQuantite = controleQuantite;
    }

    public boolean isProduitSoumisTracabilite() {
        return produitSoumisTracabilite;
    }

    public void setProduitSoumisTracabilite(boolean produitSoumisTracabilite) {
        this.produitSoumisTracabilite = produitSoumisTracabilite;
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }


    @Override
    public int compareTo(@NonNull Object obj) {
        PH_Utiliser phUtiliser = (PH_Utiliser) obj;

        if (this.getPhiMR4UUID() == phUtiliser.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getPhiMR4UUID() > phUtiliser.getPhiMR4UUID() ? 1 : -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((PH_Utiliser) obj).getPhiMR4UUID() == this.getPhiMR4UUID()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof PH_Utiliser)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }
}
