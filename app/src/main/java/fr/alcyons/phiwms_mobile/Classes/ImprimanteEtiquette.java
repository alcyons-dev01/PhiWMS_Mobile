package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ImprimanteEtiquetteOpenHelper;

public class ImprimanteEtiquette  implements Serializable, Comparable {
    int id;

    String nom;

    String adresseIP;

    String portIP;

    int EtablissementUID;

    int phiwms_mobileUUID;

    public ImprimanteEtiquette(int id, String nom, String adresseIP, String portIP, int EtablissementUID)
    {
        this.id = id;
        this.nom = nom;
        this.adresseIP = adresseIP;
        this.portIP = portIP;
        this.EtablissementUID = EtablissementUID;
    }

    public ImprimanteEtiquette(JSONObject imprimanteObject)
    {
        this.id = imprimanteObject.optInt("_UID");
        this.nom = imprimanteObject.optString("Numero");
        this.adresseIP = imprimanteObject.optString("raisonSociale");
        this.portIP = imprimanteObject.optString("Commande_adresse1");
        this.EtablissementUID = imprimanteObject.optInt("_UID");
    }

    public ImprimanteEtiquette(Cursor cursorImprimante) {
        this.id = cursorImprimante.getInt(ImprimanteEtiquetteOpenHelper.Constantes.NUM_COL_UID_IMPRIMANTE);
        this.nom = cursorImprimante.getString(ImprimanteEtiquetteOpenHelper.Constantes.NUM_COL_NOM_IMPRIMANTE);
        this.adresseIP = cursorImprimante.getString(ImprimanteEtiquetteOpenHelper.Constantes.NUM_COL_ADRESSEIP_IMPRIMANTE);
        this.portIP = cursorImprimante.getString(ImprimanteEtiquetteOpenHelper.Constantes.NUM_COL_PORT_IMPRIMANTE);
        this.EtablissementUID = cursorImprimante.getInt(ImprimanteEtiquetteOpenHelper.Constantes.NUM_COL_ETABLISSEMENTUID_IMPRIMANTE);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresseIP() {
        return adresseIP;
    }

    public void setAdresseIP(String adresseIP) {
        this.adresseIP = adresseIP;
    }

    public String getPortIP() {
        return portIP;
    }

    public void setPortIP(String portIP) {
        this.portIP = portIP;
    }

    public int getEtablissementUID() {
        return EtablissementUID;
    }

    public void setEtablissementUID(int etablissementUID) {
        EtablissementUID = etablissementUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
