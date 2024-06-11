package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PerimetreFonctionnelOpenHelper;

public class PerimetreFonctionnel implements Serializable {
    private int id;
    private String nom;
    private int phiwms_mobileUUID = -1;

    public PerimetreFonctionnel(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public PerimetreFonctionnel(Cursor cursor) {
        this.id = cursor.getInt(PerimetreFonctionnelOpenHelper.Constantes.NUM_COL_ID_PERIMETRE_FONCTIONNEL);
        this.nom = cursor.getString(PerimetreFonctionnelOpenHelper.Constantes.NUM_COL_NOM_PERIMETRE_FONCTIONNEL);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
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

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj instanceof PerimetreFonctionnel) {
            PerimetreFonctionnel objet = (PerimetreFonctionnel) obj;
            if (objet.getId() == this.getId()) {
                valeurARetourner = true;
            }
        }
        return valeurARetourner;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getNom();
    }

}
