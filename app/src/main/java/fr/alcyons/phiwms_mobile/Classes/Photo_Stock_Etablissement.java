package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

public class Photo_Stock_Etablissement implements Serializable, Comparable {

    int Code_Produit;
    int Annee;
    int Mois;
    String Ref_Cycle;
    double STOCK_DEBUT;
    double QTE_ENTREE_CDE;
    double QTE_SORTIE_DELIV;
    double QTE_ENTREE_DELIV;
    double QTE_SORTIE_RETOUR;
    double QTE_ENTREE_RETOUR;
    double QTE_SORTIE_REGUL;
    double QTE_ENTREE_REGUL;
    double QTE_SORTIE_ECART;
    double QTE_ENTREE_ECART;
    double STOCK_FIN;
    double VALEUR_STOCK_FIN;
    double VALEUR_STOCK_DEBUT;
    String CATEGORIE;
    String REFERENCE;
    String DESIGNATION;
    double PRIX_UNIT_HT;
    double Consommation_mensuelle;
    double Qte_Entree;
    double Qte_Sortie;
    double Consommation_Trimestrielle;
    double Conso_Journaliere;
    double Conso_Q1;
    double Conso_Q2;
    double Conso_Q3;
    double Conso_Q4;
    double Conso_S1;
    double Conso_S2;
    double Conso_S3;
    double Conso_S4;
    double Conso_S5;
    double Rien;
    double PMP_TTC;
    int _UID;
    int QTE_COMMANDE;
    int QTE_RAF;
    private int phiwms_mobileUUID = -1;

    public Photo_Stock_Etablissement(int code_Produit, int annee, int mois, String ref_Cycle, double STOCK_DEBUT, double QTE_ENTREE_CDE, double QTE_SORTIE_DELIV, double QTE_ENTREE_DELIV, double QTE_SORTIE_RETOUR, double QTE_ENTREE_RETOUR, double QTE_SORTIE_REGUL, double QTE_ENTREE_REGUL, double QTE_SORTIE_ECART, double QTE_ENTREE_ECART, double STOCK_FIN, double VALEUR_STOCK_FIN, double VALEUR_STOCK_DEBUT, String CATEGORIE, String REFERENCE, String DESIGNATION, double PRIX_UNIT_HT, double consommation_mensuelle, double qte_Entree, double qte_Sortie, double consommation_Trimestrielle, double conso_Journaliere, double conso_Q1, double conso_Q2, double conso_Q3, double conso_Q4, double conso_S1, double conso_S2, double conso_S3, double conso_S4, double conso_S5, double rien, double PMP_TTC, int _UID, int QTE_COMMANDE, int QTE_RAF) {
        Code_Produit = code_Produit;
        Annee = annee;
        Mois = mois;
        Ref_Cycle = ref_Cycle;
        this.STOCK_DEBUT = STOCK_DEBUT;
        this.QTE_ENTREE_CDE = QTE_ENTREE_CDE;
        this.QTE_SORTIE_DELIV = QTE_SORTIE_DELIV;
        this.QTE_ENTREE_DELIV = QTE_ENTREE_DELIV;
        this.QTE_SORTIE_RETOUR = QTE_SORTIE_RETOUR;
        this.QTE_ENTREE_RETOUR = QTE_ENTREE_RETOUR;
        this.QTE_SORTIE_REGUL = QTE_SORTIE_REGUL;
        this.QTE_ENTREE_REGUL = QTE_ENTREE_REGUL;
        this.QTE_SORTIE_ECART = QTE_SORTIE_ECART;
        this.QTE_ENTREE_ECART = QTE_ENTREE_ECART;
        this.STOCK_FIN = STOCK_FIN;
        this.VALEUR_STOCK_FIN = VALEUR_STOCK_FIN;
        this.VALEUR_STOCK_DEBUT = VALEUR_STOCK_DEBUT;
        this.CATEGORIE = CATEGORIE;
        this.REFERENCE = REFERENCE;
        this.DESIGNATION = DESIGNATION;
        this.PRIX_UNIT_HT = PRIX_UNIT_HT;
        Consommation_mensuelle = consommation_mensuelle;
        Qte_Entree = qte_Entree;
        Qte_Sortie = qte_Sortie;
        Consommation_Trimestrielle = consommation_Trimestrielle;
        Conso_Journaliere = conso_Journaliere;
        Conso_Q1 = conso_Q1;
        Conso_Q2 = conso_Q2;
        Conso_Q3 = conso_Q3;
        Conso_Q4 = conso_Q4;
        Conso_S1 = conso_S1;
        Conso_S2 = conso_S2;
        Conso_S3 = conso_S3;
        Conso_S4 = conso_S4;
        Conso_S5 = conso_S5;
        Rien = rien;
        this.PMP_TTC = PMP_TTC;
        this._UID = _UID;
        this.QTE_COMMANDE = QTE_COMMANDE;
        this.QTE_RAF = QTE_RAF;
    }

    public Photo_Stock_Etablissement(JSONObject jsonObject) {
        try {
            Code_Produit = jsonObject.getInt("Code_Produit");
            Annee = jsonObject.getInt("Annee");
            Mois = jsonObject.getInt("Mois");
            Ref_Cycle = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Cycle"));
            STOCK_DEBUT = jsonObject.getDouble("STOCK_DEBUT");
            QTE_ENTREE_CDE = jsonObject.getDouble("QTE_ENTREE_CDE");
            QTE_SORTIE_DELIV = jsonObject.getDouble("QTE_SORTIE_DELIV");
            QTE_ENTREE_DELIV = jsonObject.getDouble("QTE_ENTREE_DELIV");
            QTE_SORTIE_RETOUR = jsonObject.getDouble("QTE_SORTIE_RETOUR");
            QTE_ENTREE_RETOUR = jsonObject.getDouble("QTE_ENTREE_RETOUR");
            QTE_SORTIE_REGUL = jsonObject.getDouble("QTE_SORTIE_REGUL");
            QTE_ENTREE_REGUL = jsonObject.getDouble("QTE_ENTREE_REGUL");
            QTE_SORTIE_ECART = jsonObject.getDouble("QTE_SORTIE_ECART");
            QTE_ENTREE_ECART = jsonObject.getDouble("QTE_ENTREE_ECART");
            STOCK_FIN = jsonObject.getDouble("STOCK_FIN");
            VALEUR_STOCK_FIN = jsonObject.getDouble("VALEUR_STOCK_FIN");
            VALEUR_STOCK_DEBUT = jsonObject.getDouble("VALEUR_STOCK_DEBUT");
            CATEGORIE = OutilsGestionClasses.recupererString(jsonObject.getString("CATEGORIE"));
            REFERENCE = OutilsGestionClasses.recupererString(jsonObject.getString("REFERENCE"));
            DESIGNATION = OutilsGestionClasses.recupererString(jsonObject.getString("DESIGNATION"));
            PRIX_UNIT_HT = jsonObject.getDouble("PRIX_UNIT_HT");
            Consommation_mensuelle = jsonObject.getDouble("Consommation_mensuelle");
            Qte_Entree = jsonObject.getDouble("Qte_Entree");
            Qte_Sortie = jsonObject.getDouble("Qte_Sortie");
            Consommation_Trimestrielle = jsonObject.getDouble("Consommation_Trimestrielle");
            Conso_Journaliere = jsonObject.getDouble("Conso_Journaliere");
            Conso_Q1 = jsonObject.getDouble("Conso_Q1");
            Conso_Q2 = jsonObject.getDouble("Conso_Q2");
            Conso_Q3 = jsonObject.getDouble("Conso_Q3");
            Conso_Q4 = jsonObject.getDouble("Conso_Q4");
            Conso_S1 = jsonObject.getDouble("Conso_S1");
            Conso_S2 = jsonObject.getDouble("Conso_S2");
            Conso_S3 = jsonObject.getDouble("Conso_S3");
            Conso_S4 = jsonObject.getDouble("Conso_S4");
            Conso_S5 = jsonObject.getDouble("Conso_S5");
            Rien = jsonObject.getDouble("Rien");
            PMP_TTC = jsonObject.getDouble("PMP_TTC");
            _UID = jsonObject.getInt("_UID");
            QTE_COMMANDE = jsonObject.getInt("QTE_COMMANDE");
            QTE_RAF = jsonObject.getInt("QTE_RAF");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getCode_Produit() {
        return Code_Produit;
    }

    public void setCode_Produit(int code_Produit) {
        Code_Produit = code_Produit;
    }

    public int getAnnee() {
        return Annee;
    }

    public void setAnnee(int annee) {
        Annee = annee;
    }

    public int getMois() {
        return Mois;
    }

    public void setMois(int mois) {
        Mois = mois;
    }

    public String getRef_Cycle() {
        return Ref_Cycle;
    }

    public void setRef_Cycle(String ref_Cycle) {
        Ref_Cycle = ref_Cycle;
    }

    public double getSTOCK_DEBUT() {
        return STOCK_DEBUT;
    }

    public void setSTOCK_DEBUT(double STOCK_DEBUT) {
        this.STOCK_DEBUT = STOCK_DEBUT;
    }

    public double getQTE_ENTREE_CDE() {
        return QTE_ENTREE_CDE;
    }

    public void setQTE_ENTREE_CDE(double QTE_ENTREE_CDE) {
        this.QTE_ENTREE_CDE = QTE_ENTREE_CDE;
    }

    public double getQTE_SORTIE_DELIV() {
        return QTE_SORTIE_DELIV;
    }

    public void setQTE_SORTIE_DELIV(double QTE_SORTIE_DELIV) {
        this.QTE_SORTIE_DELIV = QTE_SORTIE_DELIV;
    }

    public double getQTE_ENTREE_DELIV() {
        return QTE_ENTREE_DELIV;
    }

    public void setQTE_ENTREE_DELIV(double QTE_ENTREE_DELIV) {
        this.QTE_ENTREE_DELIV = QTE_ENTREE_DELIV;
    }

    public double getQTE_SORTIE_RETOUR() {
        return QTE_SORTIE_RETOUR;
    }

    public void setQTE_SORTIE_RETOUR(double QTE_SORTIE_RETOUR) {
        this.QTE_SORTIE_RETOUR = QTE_SORTIE_RETOUR;
    }

    public double getQTE_ENTREE_RETOUR() {
        return QTE_ENTREE_RETOUR;
    }

    public void setQTE_ENTREE_RETOUR(double QTE_ENTREE_RETOUR) {
        this.QTE_ENTREE_RETOUR = QTE_ENTREE_RETOUR;
    }

    public double getQTE_SORTIE_REGUL() {
        return QTE_SORTIE_REGUL;
    }

    public void setQTE_SORTIE_REGUL(double QTE_SORTIE_REGUL) {
        this.QTE_SORTIE_REGUL = QTE_SORTIE_REGUL;
    }

    public double getQTE_ENTREE_REGUL() {
        return QTE_ENTREE_REGUL;
    }

    public void setQTE_ENTREE_REGUL(double QTE_ENTREE_REGUL) {
        this.QTE_ENTREE_REGUL = QTE_ENTREE_REGUL;
    }

    public double getQTE_SORTIE_ECART() {
        return QTE_SORTIE_ECART;
    }

    public void setQTE_SORTIE_ECART(double QTE_SORTIE_ECART) {
        this.QTE_SORTIE_ECART = QTE_SORTIE_ECART;
    }

    public double getQTE_ENTREE_ECART() {
        return QTE_ENTREE_ECART;
    }

    public void setQTE_ENTREE_ECART(double QTE_ENTREE_ECART) {
        this.QTE_ENTREE_ECART = QTE_ENTREE_ECART;
    }

    public double getSTOCK_FIN() {
        return STOCK_FIN;
    }

    public void setSTOCK_FIN(double STOCK_FIN) {
        this.STOCK_FIN = STOCK_FIN;
    }

    public double getVALEUR_STOCK_FIN() {
        return VALEUR_STOCK_FIN;
    }

    public void setVALEUR_STOCK_FIN(double VALEUR_STOCK_FIN) {
        this.VALEUR_STOCK_FIN = VALEUR_STOCK_FIN;
    }

    public double getVALEUR_STOCK_DEBUT() {
        return VALEUR_STOCK_DEBUT;
    }

    public void setVALEUR_STOCK_DEBUT(double VALEUR_STOCK_DEBUT) {
        this.VALEUR_STOCK_DEBUT = VALEUR_STOCK_DEBUT;
    }

    public String getCATEGORIE() {
        return CATEGORIE;
    }

    public void setCATEGORIE(String CATEGORIE) {
        this.CATEGORIE = CATEGORIE;
    }

    public String getREFERENCE() {
        return REFERENCE;
    }

    public void setREFERENCE(String REFERENCE) {
        this.REFERENCE = REFERENCE;
    }

    public String getDESIGNATION() {
        return DESIGNATION;
    }

    public void setDESIGNATION(String DESIGNATION) {
        this.DESIGNATION = DESIGNATION;
    }

    public double getPRIX_UNIT_HT() {
        return PRIX_UNIT_HT;
    }

    public void setPRIX_UNIT_HT(double PRIX_UNIT_HT) {
        this.PRIX_UNIT_HT = PRIX_UNIT_HT;
    }

    public double getConsommation_mensuelle() {
        return Consommation_mensuelle;
    }

    public void setConsommation_mensuelle(double consommation_mensuelle) {
        Consommation_mensuelle = consommation_mensuelle;
    }

    public double getQte_Entree() {
        return Qte_Entree;
    }

    public void setQte_Entree(double qte_Entree) {
        Qte_Entree = qte_Entree;
    }

    public double getQte_Sortie() {
        return Qte_Sortie;
    }

    public void setQte_Sortie(double qte_Sortie) {
        Qte_Sortie = qte_Sortie;
    }

    public double getConsommation_Trimestrielle() {
        return Consommation_Trimestrielle;
    }

    public void setConsommation_Trimestrielle(double consommation_Trimestrielle) {
        Consommation_Trimestrielle = consommation_Trimestrielle;
    }

    public double getConso_Journaliere() {
        return Conso_Journaliere;
    }

    public void setConso_Journaliere(double conso_Journaliere) {
        Conso_Journaliere = conso_Journaliere;
    }

    public double getConso_Q1() {
        return Conso_Q1;
    }

    public void setConso_Q1(double conso_Q1) {
        Conso_Q1 = conso_Q1;
    }

    public double getConso_Q2() {
        return Conso_Q2;
    }

    public void setConso_Q2(double conso_Q2) {
        Conso_Q2 = conso_Q2;
    }

    public double getConso_Q3() {
        return Conso_Q3;
    }

    public void setConso_Q3(double conso_Q3) {
        Conso_Q3 = conso_Q3;
    }

    public double getConso_Q4() {
        return Conso_Q4;
    }

    public void setConso_Q4(double conso_Q4) {
        Conso_Q4 = conso_Q4;
    }

    public double getConso_S1() {
        return Conso_S1;
    }

    public void setConso_S1(double conso_S1) {
        Conso_S1 = conso_S1;
    }

    public double getConso_S2() {
        return Conso_S2;
    }

    public void setConso_S2(double conso_S2) {
        Conso_S2 = conso_S2;
    }

    public double getConso_S3() {
        return Conso_S3;
    }

    public void setConso_S3(double conso_S3) {
        Conso_S3 = conso_S3;
    }

    public double getConso_S4() {
        return Conso_S4;
    }

    public void setConso_S4(double conso_S4) {
        Conso_S4 = conso_S4;
    }

    public double getConso_S5() {
        return Conso_S5;
    }

    public void setConso_S5(double conso_S5) {
        Conso_S5 = conso_S5;
    }

    public double getRien() {
        return Rien;
    }

    public void setRien(double rien) {
        Rien = rien;
    }

    public double getPMP_TTC() {
        return PMP_TTC;
    }

    public void setPMP_TTC(double PMP_TTC) {
        this.PMP_TTC = PMP_TTC;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public int getQTE_COMMANDE() {
        return QTE_COMMANDE;
    }

    public void setQTE_COMMANDE(int QTE_COMMANDE) {
        this.QTE_COMMANDE = QTE_COMMANDE;
    }

    public int getQTE_RAF() {
        return QTE_RAF;
    }

    public void setQTE_RAF(int QTE_RAF) {
        this.QTE_RAF = QTE_RAF;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Code_Produit", Code_Produit);
            jsonObject.put("Annee", Annee);
            jsonObject.put("Mois", Mois);
            jsonObject.put("Ref_Cycle", Ref_Cycle);
            jsonObject.put("STOCK_DEBUT", STOCK_DEBUT);
            jsonObject.put("QTE_ENTREE_CDE", QTE_ENTREE_CDE);
            jsonObject.put("QTE_SORTIE_DELIV", QTE_SORTIE_DELIV);
            jsonObject.put("QTE_ENTREE_DELIV", QTE_ENTREE_DELIV);
            jsonObject.put("QTE_SORTIE_RETOUR", QTE_SORTIE_RETOUR);
            jsonObject.put("QTE_ENTREE_RETOUR", QTE_ENTREE_RETOUR);
            jsonObject.put("QTE_SORTIE_REGUL", QTE_SORTIE_REGUL);
            jsonObject.put("QTE_ENTREE_REGUL", QTE_ENTREE_REGUL);
            jsonObject.put("QTE_SORTIE_ECART", QTE_SORTIE_ECART);
            jsonObject.put("QTE_ENTREE_ECART", QTE_ENTREE_ECART);
            jsonObject.put("STOCK_FIN", STOCK_FIN);
            jsonObject.put("VALEUR_STOCK_FIN", VALEUR_STOCK_FIN);
            jsonObject.put("VALEUR_STOCK_DEBUT", VALEUR_STOCK_DEBUT);
            jsonObject.put("CATEGORIE", CATEGORIE);
            jsonObject.put("REFERENCE", REFERENCE);
            jsonObject.put("DESIGNATION", DESIGNATION);
            jsonObject.put("PRIX_UNIT_HT", PRIX_UNIT_HT);
            jsonObject.put("Consommation_mensuelle", Consommation_mensuelle);
            jsonObject.put("Qte_Entree", Qte_Entree);
            jsonObject.put("Qte_Sortie", Qte_Sortie);
            jsonObject.put("Consommation_Trimestrielle", Consommation_Trimestrielle);
            jsonObject.put("Conso_Journaliere", Conso_Journaliere);
            jsonObject.put("Conso_Q1", Conso_Q1);
            jsonObject.put("Conso_Q2", Conso_Q2);
            jsonObject.put("Conso_Q3", Conso_Q3);
            jsonObject.put("Conso_Q4", Conso_Q4);
            jsonObject.put("Conso_S1", Conso_S1);
            jsonObject.put("Conso_S2", Conso_S2);
            jsonObject.put("Conso_S3", Conso_S3);
            jsonObject.put("Conso_S4", Conso_S4);
            jsonObject.put("Conso_S5", Conso_S5);
            jsonObject.put("Rien", Rien);
            jsonObject.put("PMP_TTC", PMP_TTC);
            jsonObject.put("_UID", _UID);
            jsonObject.put("QTE_COMMANDE", QTE_COMMANDE);
            jsonObject.put("QTE_RAF", QTE_RAF);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Photo_Stock_Etablissement)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Photo_Stock_Etablissement photo_stock_etablissement = (Photo_Stock_Etablissement) obj;

        if (this.getPhiMR4UUID() == photo_stock_etablissement.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.get_UID() > photo_stock_etablissement.get_UID() ? 1 : -1;
        }
    }
}
