package fr.alcyons.phimr4.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_SerialisationOpenHelper;

import static fr.alcyons.phimr4.Outils.OutilsGestionClasses.recupererString;

/**
 * Created by olivier on 26/02/2019.
 */

public class PH_Serialisation implements Serializable, Comparable {

    int _UID;
    int produitUID;
    String datamatrix;
    String produitCodeValue;
    String produitCodeSheme;
    String numeroLot;
    String datePeremptionAAMMJJ;
    String numeroSerie;
    String refClientTrxId;
    String reqType;
    String resultat;
    String statut;
    String NMVSTrxId;
    int userUID;
    String demandeDate;
    String demandeHeure;
    String mvtType;
    String mvtUID;
    String raison;

    private int serialexpressUUID = -1;

    public PH_Serialisation(int _UID, int UserID, String reqType, String ClientTrxId, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA, String MVT_Type, String MVT_UID, int ProduitUID) {

        this.datamatrix = "";
        if (ProductCode_SHEME_VA.contentEquals("GTIN")&& ProductCode_VALUE_VA.length() == 14) {
            this.datamatrix = "01" + ProductCode_VALUE_VA + "21" + Pack_SN_VA + "@17" + Batch_EXPDATE_VA + "10" + Batch_ID_VA;
        }

        this._UID = _UID;
        this.produitUID = ProduitUID;
        this.produitCodeValue = ProductCode_VALUE_VA;
        this.produitCodeSheme = ProductCode_SHEME_VA;
        this.numeroLot = Batch_ID_VA;
        this.datePeremptionAAMMJJ = Batch_EXPDATE_VA;
        this.numeroSerie = Pack_SN_VA;
        this.refClientTrxId = ClientTrxId;
        this.reqType = reqType;
        this.resultat = "";
        this.statut = "En attente";
        this.NMVSTrxId = "";
        this.userUID = UserID;
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        this.demandeDate = simpleDateFormatDate.format(c);
        SimpleDateFormat simpleDateFormatHeure = new SimpleDateFormat("hh:mm:ss");
        this.demandeHeure = simpleDateFormatHeure.format(c);
        this.mvtType = MVT_Type;
        this.mvtUID = MVT_UID;
        this.raison = "";

    }

    public PH_Serialisation(JSONObject jsonObject) {
        try {
            _UID = jsonObject.getInt("id");
            produitUID = jsonObject.getInt("ProduitUID");
            datamatrix = recupererString(jsonObject.getString("Datamatrix"));
            produitCodeValue = recupererString(jsonObject.getString("ProduitCodeValue"));
            produitCodeSheme = recupererString(jsonObject.getString("ProduitCodeSheme"));
            numeroLot = recupererString(jsonObject.getString("NumeroLot"));
            datePeremptionAAMMJJ = recupererString(jsonObject.getString("DatePeremptionAAMMJJ"));
            numeroSerie = recupererString(jsonObject.getString("NumeroSerie"));
            refClientTrxId = recupererString(jsonObject.getString("RefClientTrxId"));
            reqType = recupererString(jsonObject.getString("ReqType"));
            resultat = recupererString(jsonObject.getString("Resultat"));
            statut = recupererString(jsonObject.getString("Statut"));
            NMVSTrxId = recupererString(jsonObject.getString("NMVSTrxId"));
            userUID = jsonObject.getInt("UserId");
            demandeDate = recupererString(jsonObject.getString("DemandeDate"));
            demandeHeure = recupererString(jsonObject.getString("DemandeHeure"));
            mvtType = recupererString(jsonObject.getString("MvtType"));
            mvtUID = recupererString(jsonObject.getString("MvtUid"));
            raison = recupererString(jsonObject.getString("Raison"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public PH_Serialisation(Cursor cursor) {
        this._UID = cursor.getInt(PH_SerialisationOpenHelper.Constantes.NUM_COL__UID_PH_SERIALISATION);
        this.produitUID = cursor.getInt(PH_SerialisationOpenHelper.Constantes.NUM_COL_PRODUITUID_PH_SERIALISATION);
        this.datamatrix = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_DATAMATRIX_PH_SERIALISATION);
        this.produitCodeValue = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_PRODUITCODEVALUE_PH_SERIALISATION);
        this.produitCodeSheme = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_PRODUITCODESHEME_PH_SERIALISATION);
        this.numeroLot = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_NUMEROLOT_PH_SERIALISATION);
        this.datePeremptionAAMMJJ = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_DATEPEREMPTIONAAMMJJ_PH_SERIALISATION);
        this.numeroSerie = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_NUMEROSERIE_PH_SERIALISATION);
        this.refClientTrxId = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_REFCLIENTTRXID_PH_SERIALISATION);
        this.reqType = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_REQTYPE_PH_SERIALISATION);
        this.resultat = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_RESULTAT_PH_SERIALISATION);
        this.statut = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_STATUT_PH_SERIALISATION);
        this.NMVSTrxId = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_NMVSTRXID_PH_SERIALISATION);
        this.userUID = cursor.getInt(PH_SerialisationOpenHelper.Constantes.NUM_COL_USERUID_PH_SERIALISATION);
        this.demandeDate = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_DEMANDEDATE_PH_SERIALISATION);
        this.demandeHeure = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_DEMANDEHEURE_PH_SERIALISATION);
        this.mvtType = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_MVTTYPE_PH_SERIALISATION);
        this.mvtUID = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_MVTUID_PH_SERIALISATION);
        this.raison = cursor.getString(PH_SerialisationOpenHelper.Constantes.NUM_COL_RAISON_PH_SERIALISATION);
        this.serialexpressUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_UID", _UID);
            jsonObject.put("produitUID", produitUID);
            jsonObject.put("datamatrix", datamatrix);
            jsonObject.put("produitCodeValue", produitCodeValue);
            jsonObject.put("produitCodeSheme", produitCodeSheme);
            jsonObject.put("numeroLot", numeroLot);
            jsonObject.put("datePeremptionAAMMJJ", datePeremptionAAMMJJ);
            jsonObject.put("numeroSerie", numeroSerie);
            jsonObject.put("refClientTrxId", refClientTrxId);
            jsonObject.put("reqType", reqType);
            jsonObject.put("resultat", resultat);
            jsonObject.put("statut", statut);
            jsonObject.put("NMVSTrxId", NMVSTrxId);
            jsonObject.put("userUID", userUID);
            jsonObject.put("demandeDate", demandeDate);
            jsonObject.put("demandeHeure", demandeHeure);
            jsonObject.put("mvtType", mvtType);
            jsonObject.put("mvtUID", mvtUID);
            jsonObject.put("raison", raison);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public int getProduitUID() {
        return produitUID;
    }

    public void setProduitUID(int produitUID) {
        this.produitUID = produitUID;
    }

    public String getDatamatrix() {
        return datamatrix;
    }

    public void setDatamatrix(String datamatrix) {
        this.datamatrix = datamatrix;
    }

    public String getProduitCodeValue() {
        return produitCodeValue;
    }

    public void setProduitCodeValue(String produitCodeValue) {
        this.produitCodeValue = produitCodeValue;
    }

    public String getProduitCodeSheme() {
        return produitCodeSheme;
    }

    public void setProduitCodeSheme(String produitCodeSheme) {
        this.produitCodeSheme = produitCodeSheme;
    }

    public String getNumeroLot() {
        return numeroLot;
    }

    public void setNumeroLot(String numeroLot) {
        this.numeroLot = numeroLot;
    }

    public String getDatePeremptionAAMMJJ() {
        return datePeremptionAAMMJJ;
    }

    public void setDatePeremptionAAMMJJ(String datePeremptionAAMMJJ) {
        this.datePeremptionAAMMJJ = datePeremptionAAMMJJ;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getRefClientTrxId() {
        return refClientTrxId;
    }

    public void setRefClientTrxId(String refClientTrxId) {
        this.refClientTrxId = refClientTrxId;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getResultat() {
        return resultat;
    }

    public void setResultat(String resultat) {
        this.resultat = resultat;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNMVSTrxId() {
        return NMVSTrxId;
    }

    public void setNMVSTrxId(String NMVSTrxId) {
        this.NMVSTrxId = NMVSTrxId;
    }

    public int getUserUID() {
        return userUID;
    }

    public void setUserUID(int userUID) {
        this.userUID = userUID;
    }

    public String getDemandeDate() {
        return demandeDate;
    }

    public void setDemandeDate(String demandeDate) {
        this.demandeDate = demandeDate;
    }

    public String getDemandeHeure() {
        return demandeHeure;
    }

    public void setDemandeHeure(String demandeHeure) {
        this.demandeHeure = demandeHeure;
    }

    public String getMvtType() {
        return mvtType;
    }

    public void setMvtType(String mvtType) {
        this.mvtType = mvtType;
    }

    public String getMvtUID() {
        return mvtUID;
    }

    public void setMvtUID(String mvtUID) {
        this.mvtUID = mvtUID;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    public int getSerialexpressUUID() {
        return serialexpressUUID;
    }

    public void setSerialexpressUUID(int serialexpressUUID) {
        this.serialexpressUUID = serialexpressUUID;
    }

    @Override
    public String toString() {
        return "PH_Serialisation{" +
                "_UID=" + _UID +
                ", produitUID=" + produitUID +
                ", datamatrix='" + datamatrix + '\'' +
                ", produitCodeValue='" + produitCodeValue + '\'' +
                ", produitCodeSheme='" + produitCodeSheme + '\'' +
                ", numeroLot='" + numeroLot + '\'' +
                ", datePeremptionAAMMJJ='" + datePeremptionAAMMJJ + '\'' +
                ", numeroSerie='" + numeroSerie + '\'' +
                ", refClientTrxId='" + refClientTrxId + '\'' +
                ", reqType='" + reqType + '\'' +
                ", resultat='" + resultat + '\'' +
                ", statut='" + statut + '\'' +
                ", NMVSTrxId='" + NMVSTrxId + '\'' +
                ", userUID=" + userUID +
                ", demandeDate='" + demandeDate + '\'' +
                ", demandeHeure='" + demandeHeure + '\'' +
                ", mvtType='" + mvtType + '\'' +
                ", mvtUID='" + mvtUID + '\'' +
                ", raison='" + raison + '\'' +
                ", serialexpressUUID=" + serialexpressUUID +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof PH_Serialisation)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        PH_Serialisation phSerialisation = (PH_Serialisation) obj;

        if (this.get_UID() == ((PH_Serialisation) obj).get_UID()) {
            return 0;
        } else {
            return this.get_UID() > ((PH_Serialisation) obj).get_UID() ? 1 : -1;
        }
    }

}
