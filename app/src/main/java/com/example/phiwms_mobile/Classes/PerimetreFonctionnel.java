package com.example.phiwms_mobile.Classes;

import android.database.Cursor;

import java.io.Serializable;
import java.util.List;

import com.example.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.PerimetreFonctionnelOpenHelper;

/**
 * Created by quentinlanusse on 13/04/2017.
 */

public class PerimetreFonctionnel implements Serializable {
    private int id;
    private String nom;
    private int phiMR4UUID = -1;

    public PerimetreFonctionnel(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public PerimetreFonctionnel(Cursor cursor) {
        this.id = cursor.getInt(PerimetreFonctionnelOpenHelper.Constantes.NUM_COL_ID_PERIMETRE_FONCTIONNEL);
        this.nom = cursor.getString(PerimetreFonctionnelOpenHelper.Constantes.NUM_COL_NOM_PERIMETRE_FONCTIONNEL);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
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

    @Override
    public String toString() {
        return this.getNom();
    }

    public String getFiltre(List<Service> services) {
        String string = " ";
        for (Service service : services
                ) {
            string += " " + service.getNom();
        }
        return this.getNom() + string;
    }

}
