package fr.alcyons.phimr4.Classes;

import java.io.Serializable;

/**
 * Created by jessica on 03/04/2018.
 */

public class PAD_Proposition_Ligne  implements Serializable {

    public int produitID;
    public int composantID;
    public int precriptionID;
    public int dotationID;

    public int qteConditionnementAchat;
    public int qteCartonFerme;
    public int qteUnite;

    public boolean recevoirProduit;
    public boolean choisirQteProduit;
    public int qteALivrer;

    public boolean confirmer;


    public PAD_Proposition_Ligne(int produitID, int composantID, int precriptionID, int qteConditionnementAchat, int qteCartonFerme, int qteUnite){
        this.produitID = produitID;
        this.composantID = composantID;
        this.precriptionID = precriptionID;
        this.dotationID = 0;
        this.qteConditionnementAchat = qteConditionnementAchat;
        this.qteCartonFerme = qteCartonFerme;
        this.qteUnite = qteUnite;
        this.recevoirProduit = false;
        this.choisirQteProduit = false;
        this.qteALivrer = 0;
        this.confirmer = false;
    }

    public PAD_Proposition_Ligne(int produitID, int dotationID, boolean recevoirProduit, boolean choisirQteProduit, int qteALivrer){
        this.produitID = produitID;
        this.composantID = 0;
        this.precriptionID = 0;
        this.dotationID = dotationID;
        this.qteConditionnementAchat = 0;
        this.qteCartonFerme = 0;
        this.qteUnite = 0;
        this.recevoirProduit = recevoirProduit;
        this.choisirQteProduit = choisirQteProduit;
        this.qteALivrer = qteALivrer;
        this.confirmer = false;
    }


}
