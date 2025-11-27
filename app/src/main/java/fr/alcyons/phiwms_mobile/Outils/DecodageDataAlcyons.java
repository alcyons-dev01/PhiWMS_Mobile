package fr.alcyons.phiwms_mobile.Outils;

import android.database.sqlite.SQLiteDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.Detail_DotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Detail_Dot;
import fr.alcyons.phiwms_mobile.Classes.Produit;

public class DecodageDataAlcyons {
    public static class DecodageDataAlcyonsResult {
        public int productCode = 0;
        public String gtin = "";
        public String emplacement = "";
        public String zone = "";
        public String designation = "";
        public String pleinvideadressage = "";
    }

    public static DecodageDataAlcyonsResult parseDataAlcyons(String value, SQLiteDatabase db) {
        DecodageDataAlcyonsResult result = new DecodageDataAlcyonsResult();

        if(value.toUpperCase().startsWith("PHITAGPLACE"))
        {
            String[] tabEmplacement = value.split(":");
            if(tabEmplacement.length >= 2)
            {
                Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, Integer.parseInt(tabEmplacement[1]));
                if(emplacement != null)
                {
                    result.emplacement = emplacement.getAdressage();
                    Depot_Zone zone = ZoneOpenHelper.getUneZoneByID(db, emplacement.getZoneID());;
                    if(zone != null)
                        result.zone = zone.getZoneName();
                    else
                        result.zone = "";
                }
                else
                {
                    result.emplacement = "";
                }

                result.designation = "";
                result.gtin = "";
                result.productCode = 0;
            }
        }
        else if(value.toUpperCase().startsWith("PHITAGTIN"))
        {
            String[] tabGtin = value.split(":");
            if(tabGtin.length >= 2)
            {
                result.gtin = tabGtin[1];

                Produit produit = ProduitOpenHelper.getUnProduitParGTIN(db, result.gtin);
                if(produit != null)
                {
                    result.productCode = produit.getID_produit();
                    result.designation = produit.getDesignation_interne();
                    result.emplacement = produit.getEmplacement_PUI_Defaut();
                    result.zone = produit.getZone_PUI_Defaut();
                }
                else
                {
                    result.productCode = 0;
                    result.designation = "";
                    result.emplacement = "";
                    result.zone = "";
                }
            }
        }
        else if(value.toUpperCase().startsWith("PHITAGREF"))
        {
            String[] tabRef = value.split(":");
            if(tabRef.length >= 2)
            {
                result.productCode = Integer.parseInt(tabRef[1]);

                Produit produit = ProduitOpenHelper.getProduitByID(db, result.productCode);
                if(produit != null)
                {
                    result.gtin = produit.getGTIN();
                    result.designation = produit.getDesignation_interne();
                    result.emplacement = produit.getEmplacement_PUI_Defaut();
                    result.zone = produit.getZone_PUI_Defaut();
                }
                else
                {
                    result.gtin = "";
                    result.designation = "";
                    result.emplacement = "";
                    result.zone = "";
                }
            }
        }
        else if(value.toUpperCase().startsWith("PHITAGPVREF"))
        {
            String[] tabPVRef = value.split(":");
            if(tabPVRef.length >= 2)
            {
                result.pleinvideadressage = tabPVRef[1];
                result.productCode = 0;
                result.gtin = "";
                result.designation = "";
                result.emplacement = "";
                result.zone = "";
            }
        }

        return result;
    }
}
