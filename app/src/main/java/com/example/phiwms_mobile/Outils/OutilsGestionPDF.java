package com.example.phiwms_mobile.Outils;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.app.ActivityCompat;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.PH_Lot_LigneOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import com.example.phiwms_mobile.Classes.Depot;
import com.example.phiwms_mobile.Classes.PH_Lot_Ligne;
import com.example.phiwms_mobile.Classes.PH_Preparation;
import com.example.phiwms_mobile.Classes.PH_Preparation_Ligne;
import com.example.phiwms_mobile.Classes.Produit;
import com.example.phiwms_mobile.Classes.Utilisateur;
import com.example.phiwms_mobile.MenuActivity;
import com.example.phiwms_mobile.R;

/**
 * Created by jessica on 17/08/2017.
 */

public class OutilsGestionPDF {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean externalStorage;

    public OutilsGestionPDF(Boolean externalStorage) {
        this.externalStorage = externalStorage;
    }

    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void createLivraison(Activity activity, String filename, String signatureNameChauffeur, SQLiteDatabase db, PH_Preparation ph_preparation) throws IOException, DocumentException {

        File fileToCreate;
        String dirpath;

        if (externalStorage) {
            verifyStoragePermissions(activity);

            fileToCreate = new File(activity.getFilesDir().getAbsolutePath(), "Documents/"+filename);
            dirpath = activity.getFilesDir().getAbsolutePath();
        } else {
            fileToCreate = new File(activity.getFilesDir().toString(), "Documents/"+filename);
            dirpath = activity.getFilesDir().toString();
        }

        Document document = new Document();
        File filetocreate = new File(activity.getFilesDir().getAbsolutePath()+File.separator + "Documents/" + filename);
        PdfWriter.getInstance(document, new FileOutputStream(filetocreate));
        document.open();

        /*
        * ZONE COORDONNEE
        * */
        PdfPTable tableCoordonnee = new PdfPTable(2);
        tableCoordonnee.setWidthPercentage(100);
        tableCoordonnee.setSpacingBefore(20);

        tableCoordonnee.addCell("Coordonnée de livraison");
        tableCoordonnee.addCell("Livraison réalisée par");

        // COORDONNNEE CHAUFFEUR
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateDuJour = new Date();
        String date = dateFormat.format(dateDuJour);

        SimpleDateFormat heureFormat = new SimpleDateFormat("hh:mm:ss");
        Date heureDuJour = new Date();
        String heure = heureFormat.format(heureDuJour);

        String identite = ((MenuActivity) activity).utilisateurConnecte.getPrenom() + " " + ((MenuActivity) activity).utilisateurConnecte.getNom();

        List listCoordonneChauffeur = new List(List.UNORDERED);
        listCoordonneChauffeur.add(new ListItem(identite));
        listCoordonneChauffeur.add(new ListItem("Livraison N°" + ph_preparation.getUID()));
        listCoordonneChauffeur.add(new ListItem("Le " + date));
        listCoordonneChauffeur.add(new ListItem("A " + heure));

        PdfPCell cellChauffeur = new PdfPCell();
        cellChauffeur.addElement(listCoordonneChauffeur);

        // COORDONNEE DE LIVRAISON
        Depot depot = DepotOpenHelper.getDepotParID(db, ph_preparation.getDepotDestinataireID());
        String adresse;

        if (depot.getDepot_Reference().contains("PAD") && depot.isPAD_Utiliser_Adresse_Vacances()) {
            adresse = depot.getPAD_Vacances_Adr1() + ", ";
            if (depot.getPAD_Vacances_Adr2().length() > 1) {
                adresse += depot.getPAD_Vacances_Adr2() + ", ";
            }
            adresse += depot.getPAD_Vacances_CP() + " " + depot.getPAD_Vacances_Ville();
        } else {
            adresse = depot.getAdresse1() + ", ";
            if (depot.getAdresse2().length() > 1) {
                adresse += depot.getAdresse2() + ", ";
            }
            adresse += depot.getCP() + " " + depot.getVille();
        }

        List listCoordonneLivraison = new List(List.UNORDERED);
        listCoordonneLivraison.add(new ListItem(depot.getNom()));
        listCoordonneLivraison.add(new ListItem(adresse));
        listCoordonneLivraison.add(new ListItem("Tel : " + depot.getTel()));
        listCoordonneLivraison.add(new ListItem("Fax : " + depot.getFax()));

        /*
        * ZONE DETAIL LIVRAISON
        * */
        PdfPCell cellLivraison = new PdfPCell();
        cellLivraison.addElement(listCoordonneLivraison);


        tableCoordonnee.addCell(cellLivraison);
        tableCoordonnee.addCell(cellChauffeur);
        document.add(tableCoordonnee);


        // LISTE
        PdfPTable tableDetail = new PdfPTable(6);
        tableDetail.setSpacingBefore(20);
        int[] tailleCell = new int[]{100, 50, 50, 40, 40, 40};
        tableDetail.setWidths(tailleCell);
        tableDetail.setWidthPercentage(100);

        //Entete
        tableDetail.addCell("Désignation");
        tableDetail.addCell("Référence");
        tableDetail.addCell("Qté demandée");
        tableDetail.addCell("Colis/cond");
        tableDetail.addCell("Qté livrée");
        tableDetail.addCell("Unité");


        //Ligne
        for (PH_Preparation_Ligne ph_preparation_ligne : PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation)) {

            if(ph_preparation_ligne.getQte_Demander() > 0)
            {
                int nbColisVE = 0;
                int conditionnementAchat;
                int qte = ph_preparation_ligne.getQte_livrer();

                Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne.getProduitID());

                if (produit != null) {
                    conditionnementAchat = produit.getCond_achat();
                    int conditionnementSet = conditionnementAchat;
                    if (qte != 0 && conditionnementSet != 0) {
                        nbColisVE = qte / conditionnementSet;
                        if (qte % conditionnementSet != 0) {
                            nbColisVE++;
                        }
                    }
                    if (qte != 0) {
                        if (nbColisVE == 0) {
                            nbColisVE = 1;
                        }
                    }
                }
                tableDetail.addCell(ph_preparation_ligne.getProduitDesignation());
                tableDetail.addCell(ph_preparation_ligne.getProduitReference());
                tableDetail.addCell(String.valueOf(ph_preparation_ligne.getQte_Demander()));
                tableDetail.addCell(String.valueOf(nbColisVE));
                tableDetail.addCell(String.valueOf(ph_preparation_ligne.getQte_livrer()));
                if (produit != null) {
                    tableDetail.addCell(produit.getUnite());
                }
            }
        }

        document.add(tableDetail);

        /*
        * ZONE PIED DE PAGE
        * */
        PdfPTable tablePiedDePage = new PdfPTable(3);
        tablePiedDePage.setWidthPercentage(100);
        tablePiedDePage.setSpacingBefore(5);

        tablePiedDePage.addCell("Commentaire");
        tablePiedDePage.addCell("");
        tablePiedDePage.addCell("Signature");

        // COMMMENTAIRE
        tablePiedDePage.addCell(ph_preparation.getCommentaires());

        // MONTANT
        List listMontant = new List(List.UNORDERED);
        listMontant.add(new ListItem("Montant HT : " + ph_preparation.getMontant_HT()));
        listMontant.add(new ListItem("Montant TTC : " + ph_preparation.getMontant_TTC()));
        listMontant.add(new ListItem("Poids : " + ph_preparation.getPoids()));

        PdfPCell cellMontant = new PdfPCell();
        cellMontant.addElement(listMontant);

        tablePiedDePage.addCell(cellMontant);

        String path = activity.getFilesDir().getAbsolutePath()+File.separator + "Documents/" +  signatureNameChauffeur + ".jpeg";
        // SIGNATURE
        Image img = Image.getInstance(path);

        PdfPCell cellImage = new PdfPCell(img, true);
        tablePiedDePage.addCell(cellImage);

        document.add(tablePiedDePage);

        document.close();
    }

    public void createLivraisonV2(Activity activity, String filename, String signatureNameChauffeur, SQLiteDatabase db, PH_Preparation ph_preparation) throws IOException, DocumentException {

        File fileToCreate;
        String dirpath;
        boolean impressionAvecLot = true;

        if (externalStorage) {
            verifyStoragePermissions(activity);

            fileToCreate = new File(activity.getFilesDir().getAbsolutePath(), "Documents/"+filename);
            dirpath = activity.getFilesDir().getAbsolutePath();
        } else {
            fileToCreate = new File(activity.getFilesDir().toString(), "Documents/"+filename);
            dirpath = activity.getFilesDir().toString();
        }

        Document document = new Document();
        File filetocreate = new File(activity.getFilesDir().getAbsolutePath()+File.separator + "Documents/" + filename);
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filetocreate));
        /**
         * Test Header
         * */
        HeaderFooter event = new HeaderFooter(ph_preparation, activity, db, signatureNameChauffeur);
        pdfWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));
        pdfWriter.setPageEvent(event);

        document.open();

        BaseColor greyColor = WebColors.getRGBColor("#A8A8A8");
        BaseColor whiteColor = WebColors.getRGBColor("#FFFFFF");
        BaseColor blueColor = WebColors.getRGBColor("#09AFE9");
        BaseColor greyClairColor = WebColors.getRGBColor("#C7C7C7");
        BaseColor redColor = WebColors.getRGBColor("#DB0303");
        Font bold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
        Font underline = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.UNDERLINE);
        Font whiteFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        whiteFont.setColor(whiteColor);
        Font whiteFontBold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
        whiteFontBold.setColor(whiteColor);
        Font detailFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font detailFontBold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);


        // LISTE
        PdfPTable tableDetail = new PdfPTable(5);
        tableDetail.setSpacingBefore(5);
        tableDetail.setSpacingAfter(10);
        int[] tailleCell = new int[]{160, 60, 50, 50, 25};
        if(!impressionAvecLot)
        {
            tailleCell = new int[]{100, 75, 100, 25};
        }
        tableDetail.setWidths(tailleCell);
        tableDetail.setWidthPercentage(100);

        Chunk designationChunk = new Chunk("Désignation", whiteFontBold);
        Paragraph designationReference = new Paragraph(designationChunk);
        Chunk referenceChunk = new Chunk("Ref.", whiteFontBold);
        Paragraph titreReference = new Paragraph(referenceChunk);
        Chunk lotChunck = new Chunk("Lot", whiteFontBold);
        Paragraph lotCond = new Paragraph(lotChunck);
        Chunk videChunk = new Chunk("Péremption", whiteFontBold);
        Paragraph videDemande = new Paragraph(videChunk);
        if(!impressionAvecLot)
        {
            lotChunck = new Chunk("Référence", whiteFontBold);
            lotCond = new Paragraph(lotChunck);
            videChunk = new Chunk("Catégorie", whiteFontBold);
            videDemande = new Paragraph(videChunk);
        }
        Chunk qteLivreChunk = new Chunk("Livrée", whiteFontBold);
        Paragraph titreQteLivre = new Paragraph(qteLivreChunk);
        /*Chunk uniteChunk = new Chunk("Unité", whiteFontBold);
        Paragraph titreLivrée = new Paragraph(uniteChunk);*/

        PdfPCell cellDesignation = new PdfPCell();
        cellDesignation.addElement(designationReference);
        cellDesignation.setBorder(Rectangle.NO_BORDER);
        cellDesignation.setCellEvent(new DottedCell(PdfPCell.TOP));
        cellDesignation.setCellEvent(new DottedCell(PdfPCell.BOTTOM));
        cellDesignation.setCellEvent(new DottedCell(PdfPCell.LEFT));
        cellDesignation.setBackgroundColor(greyColor);
        tableDetail.addCell(cellDesignation);

        PdfPCell cellReference = new PdfPCell();
        cellReference.addElement(titreReference);
        cellReference.setBorder(Rectangle.NO_BORDER);
        cellReference.setCellEvent(new DottedCell(PdfPCell.TOP));
        cellReference.setCellEvent(new DottedCell(PdfPCell.BOTTOM));
        cellReference.setCellEvent(new DottedCell(PdfPCell.LEFT));
        cellReference.setBackgroundColor(greyColor);
        tableDetail.addCell(cellReference);

        PdfPCell cellLot = new PdfPCell();
        cellLot.addElement(lotCond);
        cellLot.setBorder(Rectangle.NO_BORDER);
        cellLot.setCellEvent(new DottedCell(PdfPCell.TOP));
        cellLot.setCellEvent(new DottedCell(PdfPCell.BOTTOM));
        cellLot.setBackgroundColor(greyColor);
        tableDetail.addCell(cellLot);

        PdfPCell cellVide = new PdfPCell();
        cellVide.addElement(videDemande);
        cellVide.setBorder(Rectangle.NO_BORDER);
        cellVide.setCellEvent(new DottedCell(PdfPCell.TOP));
        cellVide.setCellEvent(new DottedCell(PdfPCell.BOTTOM));
        cellVide.setBackgroundColor(greyColor);
        tableDetail.addCell(cellVide);

        PdfPCell cellQteLivre = new PdfPCell();
        cellQteLivre.addElement(titreQteLivre);
        cellQteLivre.setBorder(Rectangle.NO_BORDER);
        cellQteLivre.setCellEvent(new DottedCell(PdfPCell.TOP));
        cellQteLivre.setCellEvent(new DottedCell(PdfPCell.BOTTOM));
        cellQteLivre.setBackgroundColor(greyColor);
        tableDetail.addCell(cellQteLivre);

        int compteur = 0;
        //Ligne
        ArrayList<PH_Preparation_Ligne> liste_preparation_liste = new ArrayList<>();
        liste_preparation_liste = (ArrayList<PH_Preparation_Ligne>) PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationLivraison(db, ph_preparation);
        for (PH_Preparation_Ligne ph_preparation_ligne : liste_preparation_liste) {

            if(ph_preparation_ligne.getQte_Demander() > 0)
            {
                compteur ++;
                int nbColisVE = 0;
                int conditionnementAchat;
                int qte = ph_preparation_ligne.getQte_livrer();

                Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne.getProduitID());

                if (produit != null) {
                    conditionnementAchat = produit.getCond_achat();
                    int conditionnementSet = conditionnementAchat;
                    if (qte != 0 && conditionnementSet != 0) {
                        nbColisVE = qte / conditionnementSet;
                        if (qte % conditionnementSet != 0) {
                            nbColisVE++;
                        }
                    }
                    if (qte != 0) {
                        if (nbColisVE == 0) {
                            nbColisVE = 1;
                        }
                    }
                }

                Chunk designationDetailChunk = new Chunk(ph_preparation_ligne.getProduitDesignation(), detailFontBold);
                Paragraph textDesignation = new Paragraph(designationDetailChunk);
                Chunk referenceDetailChunk = new Chunk(ph_preparation_ligne.getProduitReference(), detailFont);
                Paragraph textreference = new Paragraph(referenceDetailChunk);
                Chunk lotDetailChunk = new Chunk(ph_preparation_ligne.getLotNumero(), detailFont);
                Paragraph textLot = new Paragraph(lotDetailChunk);

                String[] datePeremtionTab = ph_preparation_ligne.getPeremptionDate().split("-");
                String datePeremptionString = datePeremtionTab[2]+"/"+datePeremtionTab[1]+"/"+datePeremtionTab[0];

                Chunk datePeremptionChunk = new Chunk(datePeremptionString, detailFont);
                if(datePeremptionString.contentEquals("00-00-0000"))
                {
                    datePeremptionChunk = new Chunk("", detailFontBold);
                }
                Paragraph textPeremption = new Paragraph(datePeremptionChunk);

                if(!impressionAvecLot)
                {
                    lotDetailChunk = new Chunk(produit.getRef_fourni(), detailFont);
                    textLot = new Paragraph(lotDetailChunk);
                    datePeremptionChunk = new Chunk(produit.getCategorie(), detailFont);
                    textPeremption = new Paragraph(datePeremptionChunk);
                }

                Chunk qteLivrerDetailChunk = new Chunk(String.valueOf(ph_preparation_ligne.getQte_livrer()), bold);
                Paragraph textQteLivrer = new Paragraph(qteLivrerDetailChunk);

                PdfPCell cellDetailDesignation = new PdfPCell();
                cellDetailDesignation.addElement(textDesignation);
                cellDetailDesignation.setUseVariableBorders(true);
                cellDetailDesignation.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
                cellDetailDesignation.setBorderColor(blueColor);
                cellDetailDesignation.setVerticalAlignment(PdfPCell.ALIGN_CENTER);

                PdfPCell cellDetailReference = new PdfPCell();
                cellDetailReference.addElement(textreference);
                cellDetailReference.setUseVariableBorders(true);
                cellDetailReference.setBorder(Rectangle.BOTTOM);
                cellDetailReference.setBorderColor(blueColor);
                cellDetailReference.setVerticalAlignment(PdfPCell.ALIGN_CENTER);

                PdfPCell cellDetailLot = new PdfPCell();
                cellDetailLot.addElement(textLot);
                cellDetailLot.setBorder(Rectangle.BOTTOM);
                cellDetailLot.setBorderColor(blueColor);
                cellDetailLot.setVerticalAlignment(PdfPCell.ALIGN_CENTER);

                PdfPCell cellDetailPeremption = new PdfPCell();
                cellDetailPeremption.addElement(textPeremption);
                cellDetailPeremption.setBorder(Rectangle.BOTTOM);
                cellDetailPeremption.setBorderColor(blueColor);
                cellDetailPeremption.setVerticalAlignment(PdfPCell.ALIGN_CENTER);

                PdfPCell cellDetailQteLivrer = new PdfPCell();
                textQteLivrer.setAlignment(Element.ALIGN_RIGHT);
                cellDetailQteLivrer.addElement(textQteLivrer);
                cellDetailQteLivrer.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
                cellDetailQteLivrer.setBorderColor(redColor);
                cellDetailQteLivrer.setVerticalAlignment(PdfPCell.ALIGN_CENTER);

                tableDetail.addCell(cellDetailDesignation);
                tableDetail.addCell(cellDetailReference);
                tableDetail.addCell(cellDetailLot);
                tableDetail.addCell(cellDetailPeremption);
                tableDetail.addCell(cellDetailQteLivrer);

                int modulo = compteur%20;

                if(compteur%20 == 0) {
                    document.add(tableDetail);
                    document.newPage();
                    tableDetail = new PdfPTable(5);
                    tableDetail.setSpacingBefore(5);
                    tableDetail.setSpacingAfter(10);
                    tailleCell = new int[]{160, 60, 50, 50, 25};
                    if (!impressionAvecLot) {
                        tailleCell = new int[]{100, 75, 100, 25};
                    }
                    tableDetail.setWidths(tailleCell);
                    tableDetail.setWidthPercentage(100);

                    designationChunk = new Chunk("Désignation", whiteFontBold);
                    designationReference = new Paragraph(designationChunk);
                    referenceChunk = new Chunk("Ref.", whiteFontBold);
                    titreReference = new Paragraph(referenceChunk);
                    lotChunck = new Chunk("Lot", whiteFontBold);
                    lotCond = new Paragraph(lotChunck);
                    videChunk = new Chunk("Péremption", whiteFontBold);
                    videDemande = new Paragraph(videChunk);
                    if (!impressionAvecLot) {
                        lotChunck = new Chunk("Référence", whiteFontBold);
                        lotCond = new Paragraph(lotChunck);
                        videChunk = new Chunk("Catégorie", whiteFontBold);
                        videDemande = new Paragraph(videChunk);
                    }
                    qteLivreChunk = new Chunk("Livrée", whiteFontBold);
                    titreQteLivre = new Paragraph(qteLivreChunk);

                    cellDesignation = new PdfPCell();
                    cellDesignation.addElement(designationReference);
                    cellDesignation.setBorder(Rectangle.NO_BORDER);
                    cellDesignation.setCellEvent(new DottedCell(PdfPCell.TOP));
                    cellDesignation.setCellEvent(new DottedCell(PdfPCell.BOTTOM));
                    cellDesignation.setCellEvent(new DottedCell(PdfPCell.LEFT));
                    cellDesignation.setBackgroundColor(greyColor);
                    tableDetail.addCell(cellDesignation);

                    cellReference = new PdfPCell();
                    cellReference.addElement(titreReference);
                    cellReference.setBorder(Rectangle.NO_BORDER);
                    cellReference.setCellEvent(new DottedCell(PdfPCell.TOP));
                    cellReference.setCellEvent(new DottedCell(PdfPCell.BOTTOM));
                    cellReference.setCellEvent(new DottedCell(PdfPCell.LEFT));
                    cellReference.setBackgroundColor(greyColor);
                    tableDetail.addCell(cellReference);

                    cellLot = new PdfPCell();
                    cellLot.addElement(lotCond);
                    cellLot.setBorder(Rectangle.NO_BORDER);
                    cellLot.setCellEvent(new DottedCell(PdfPCell.TOP));
                    cellLot.setCellEvent(new DottedCell(PdfPCell.BOTTOM));
                    cellLot.setBackgroundColor(greyColor);
                    tableDetail.addCell(cellLot);

                    cellVide = new PdfPCell();
                    cellVide.addElement(videDemande);
                    cellVide.setBorder(Rectangle.NO_BORDER);
                    cellVide.setCellEvent(new DottedCell(PdfPCell.TOP));
                    cellVide.setCellEvent(new DottedCell(PdfPCell.BOTTOM));
                    cellVide.setBackgroundColor(greyColor);
                    tableDetail.addCell(cellVide);

                    cellQteLivre = new PdfPCell();
                    cellQteLivre.addElement(titreQteLivre);
                    cellQteLivre.setBorder(Rectangle.NO_BORDER);
                    cellQteLivre.setCellEvent(new DottedCell(PdfPCell.TOP));
                    cellQteLivre.setCellEvent(new DottedCell(PdfPCell.BOTTOM));
                    cellQteLivre.setBackgroundColor(greyColor);
                    tableDetail.addCell(cellQteLivre);
                }
                else if(compteur == liste_preparation_liste.size())
                {
                    document.add(tableDetail);
                }
            }
        }
        document.close();
    }
}
