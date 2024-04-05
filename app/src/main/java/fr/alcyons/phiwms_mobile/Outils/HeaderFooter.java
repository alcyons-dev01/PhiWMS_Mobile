package fr.alcyons.phiwms_mobile.Outils;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.MenuActivity;
import fr.alcyons.phiwms_mobile.R;

public class HeaderFooter extends PdfPageEventHelper {
    /** Alternating phrase for the header. */
    Phrase[] header = new Phrase[2];
    /** Current page number (will be reset for every chapter). */
    int pagenumber;
    PH_Preparation ph_preparation;
    Activity activity;
    SQLiteDatabase db;
    BaseColor greyColor = WebColors.getRGBColor("#A8A8A8");
    BaseColor whiteColor = WebColors.getRGBColor("#FFFFFF");
    BaseColor blueColor = WebColors.getRGBColor("#09AFE9");
    BaseColor greyClairColor = WebColors.getRGBColor("#C7C7C7");
    BaseColor redColor = WebColors.getRGBColor("#DB0303");
    Font bold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
    Font underline = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.UNDERLINE);
    Font whiteFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
    Font whiteFontBold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
    Font detailFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
    Font detailFontBold = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
    String signatureNameChauffeur;

    public HeaderFooter(PH_Preparation ph_preparation, Activity activity, SQLiteDatabase database, String signature)
    {
        this.ph_preparation = ph_preparation;
        this.activity = activity;
        this.db = database;
        this.signatureNameChauffeur = signature;
        whiteFont.setColor(whiteColor);
        whiteFontBold.setColor(whiteColor);
    }

    /**
     * Initialize one of the headers.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onOpenDocument(PdfWriter writer, Document document) {
        header[0] = new Phrase("Movie history");
    }

    /**
     * Initialize one of the headers, based on the chapter title;
     * reset the page number.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onChapter(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document, float,
     *      com.itextpdf.text.Paragraph)
     */
    public void onChapter(PdfWriter writer, Document document,
                          float paragraphPosition, Paragraph title) {
        header[1] = new Phrase(title.getContent());
        pagenumber = 1;
    }

    /**
     * Increase the page number.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onStartPage(PdfWriter writer, Document document) {
        /*
         * ZONE entête
         * */
        PdfPTable tableEntete = new PdfPTable(1);
        tableEntete.setWidthPercentage(100);
        Chunk titreBon = new Chunk("Bon de livraison n°"+ph_preparation.getUID(), whiteFontBold);
        Paragraph textBon = new Paragraph(titreBon);
        PdfPCell cellNumeroDeBon = new PdfPCell();
        cellNumeroDeBon.setBackgroundColor(greyColor);
        cellNumeroDeBon.addElement(textBon);
        cellNumeroDeBon.setBorder(Rectangle.NO_BORDER);
        tableEntete.addCell(cellNumeroDeBon);
        try {
            document.add(tableEntete);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        /*
         * ZONE COORDONNEE
         * */
        PdfPTable tableCoordonnee = new PdfPTable(2);
        tableCoordonnee.setWidthPercentage(100);
        tableCoordonnee.setSpacingBefore(5);

        PdfPCell cellTitreProvenance = new PdfPCell();
        Chunk titreBoldProvenance = new Chunk("PROVENANCE", bold);
        Chunk titreBoldDestinataire = new Chunk("DESTINATAIRE", bold);

        Paragraph paragrapheTitreProvenance =  new Paragraph();
        paragrapheTitreProvenance.add(titreBoldProvenance);

        Paragraph paragrapheTitreDestinataire =  new Paragraph();
        paragrapheTitreDestinataire.add(titreBoldDestinataire);

        cellTitreProvenance.addElement(paragrapheTitreProvenance);
        cellTitreProvenance.setBorder(Rectangle.NO_BORDER);
        cellTitreProvenance.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        tableCoordonnee.addCell(cellTitreProvenance);

        PdfPCell cellTitreDestinataire = new PdfPCell();
        cellTitreDestinataire.addElement(paragrapheTitreDestinataire);
        cellTitreDestinataire.setBorder(Rectangle.NO_BORDER);
        cellTitreDestinataire.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        tableCoordonnee.addCell(cellTitreDestinataire);

        // COORDONNNEE CHAUFFEUR
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateDuJour = new Date();
        String date = dateFormat.format(dateDuJour);

        //Modification format heure
        SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm:ss");
        Date heureDuJour = new Date();
        String heure = heureFormat.format(heureDuJour);

        String identite = ((MenuActivity) activity).utilisateurConnecte.getPrenom() + " " + ((MenuActivity) activity).utilisateurConnecte.getNom();

        Depot depotPui = DepotOpenHelper.getDepotPUI(db);

        Paragraph paragrapheProvenance =  new Paragraph();
        paragrapheProvenance.add(depotPui.getNom()+"\n"+depotPui.getAdresse1()+"\n"+depotPui.getCP()+" "+depotPui.getVille()+"\nTel : "+depotPui.getTel()+"\nFax : "+depotPui.getFax());

        PdfPCell cellProvenance = new PdfPCell();
        cellProvenance.setBorder(Rectangle.NO_BORDER);
        cellProvenance.addElement(paragrapheProvenance);

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
        }

        Paragraph paragrapheLivraison =  new Paragraph();
        paragrapheLivraison.add(depot.getNom()+"\n"+adresse+"\n"+depot.getCP()+" "+depot.getVille()+"\nTel : "+depot.getTel()+"\nFax : "+depot.getFax());

        /*
         * ZONE DETAIL LIVRAISON
         * */
        PdfPCell cellLivraison = new PdfPCell();
        cellLivraison.setBorder(Rectangle.NO_BORDER);
        cellLivraison.addElement(paragrapheLivraison);

        PdfPCell cellVide2 = new PdfPCell();

        tableCoordonnee.addCell(cellProvenance);
        tableCoordonnee.addCell(cellLivraison);
        tableCoordonnee.addCell(cellVide2);
        try {
            document.add(tableCoordonnee);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        PdfPTable tableLigne2 = new PdfPTable(5);
        int[] tailleCellLigne2 = new int[]{100, 100, 25, 25, 25};
        try {
            tableLigne2.setWidths(tailleCellLigne2);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        tableLigne2.setWidthPercentage(100);
        tableLigne2.setSpacingBefore(5);
        Chunk titreBoldTransporteur = new Chunk("TRANSPORTEUR", bold);
        Chunk titreBoldDate = new Chunk("DATE", bold);

        Paragraph paragrapheTitreTransporteur =  new Paragraph();
        paragrapheTitreTransporteur.add(titreBoldTransporteur);

        Paragraph paragrapheTitreDate =  new Paragraph();
        paragrapheTitreDate.add(titreBoldDate);

        PdfPCell cellTitreTransporteur = new PdfPCell();
        cellTitreTransporteur.addElement(paragrapheTitreTransporteur);
        cellTitreTransporteur.setBorder(Rectangle.NO_BORDER);
        cellTitreTransporteur.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        tableLigne2.addCell(cellTitreTransporteur);

        PdfPCell cellTitreDate = new PdfPCell();
        cellTitreDate.addElement(paragrapheTitreDate);
        cellTitreDate.setBorder(Rectangle.NO_BORDER);
        cellTitreDate.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        tableLigne2.addCell(cellTitreDate);


        ByteArrayOutputStream streamPalette = new ByteArrayOutputStream();
        int h = 20;
        int w = 20;
        Drawable dPalette = activity.getDrawable(R.drawable.palette_preparation);
        BitmapDrawable bitDwPalettte = ((BitmapDrawable) dPalette);
        Bitmap bmpPalette = bitDwPalettte.getBitmap();
        bmpPalette.compress(Bitmap.CompressFormat.PNG, 100, streamPalette);
        Image imagePalette = null;
        try {
            imagePalette = Image.getInstance(streamPalette.toByteArray());
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagePalette.scaleAbsolute(w, h);
        PdfPCell cellPalette = new PdfPCell(imagePalette);
        cellPalette.setBorder(Rectangle.NO_BORDER);
        tableLigne2.addCell(cellPalette);

        ByteArrayOutputStream streamColis = new ByteArrayOutputStream();
        Drawable dColis = activity.getDrawable(R.drawable.red_colis);
        Bitmap bmpColis =((BitmapDrawable) dColis).getBitmap();
        bmpColis.compress(Bitmap.CompressFormat.PNG, 100, streamColis);
        Image imageColis = null;
        try {
            imageColis = Image.getInstance(streamColis.toByteArray());
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageColis.scaleAbsolute(w, h);
        PdfPCell cellColis = new PdfPCell(imageColis);
        cellColis.setBorder(Rectangle.NO_BORDER);
        tableLigne2.addCell(cellColis);

        ByteArrayOutputStream streamConteneur = new ByteArrayOutputStream();
        Drawable dConteneur = activity.getDrawable(R.drawable.conteneur);
        BitmapDrawable bitDwConteneur = ((BitmapDrawable) dConteneur);
        Bitmap bmpConteneur = bitDwConteneur.getBitmap();
        bmpConteneur.compress(Bitmap.CompressFormat.PNG, 100, streamConteneur);
        Image imageConteneur = null;
        try {
            imageConteneur = Image.getInstance(streamConteneur.toByteArray());
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageConteneur.scaleAbsolute(w, h);
        PdfPCell cellConteneur = new PdfPCell(imageConteneur);
        cellConteneur.setBorder(Rectangle.NO_BORDER);
        tableLigne2.addCell(cellConteneur);

        Utilisateur userLivreur = UtilisateurOpenHelper.getUtilisateurByID(db, ph_preparation.getLivreur_userID());
        String livrepar = "";
        if(userLivreur != null)
        {
            livrepar = userLivreur.getNom()+" "+userLivreur.getPrenom();
        }
        Paragraph paragraphTransporteur = new Paragraph(livrepar);
        PdfPCell cellTransporteur = new PdfPCell();
        cellTransporteur.addElement(paragraphTransporteur);
        cellTransporteur.setBorder(Rectangle.NO_BORDER);
        tableLigne2.addCell(cellTransporteur);

        Paragraph paragraphDate = new Paragraph(date);
        PdfPCell cellDate = new PdfPCell();
        cellDate.addElement(paragraphDate);
        cellDate.setBorder(Rectangle.NO_BORDER);
        tableLigne2.addCell(cellDate);

        int nbPalette = ph_preparation.getPaletteNB();
        Paragraph paragraphPalette = new Paragraph(String.valueOf(nbPalette));
        PdfPCell cellPaletteText = new PdfPCell();
        cellPaletteText.addElement(paragraphPalette);
        cellPaletteText.setBorder(Rectangle.NO_BORDER);
        cellPaletteText.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        tableLigne2.addCell(cellPaletteText);

        int nbColis = ph_preparation.getColisNB();
        Paragraph paragraphColis = new Paragraph(String.valueOf(nbColis));
        PdfPCell cellColisText = new PdfPCell();
        cellColisText.addElement(paragraphColis);
        cellColisText.setBorder(Rectangle.NO_BORDER);
        cellColisText.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        tableLigne2.addCell(cellColisText);

        int nbConteneur = ph_preparation.getConteneur_NB();
        Paragraph paragraphConteneur = new Paragraph(String.valueOf(nbConteneur));
        PdfPCell cellConteneurText = new PdfPCell();
        cellConteneurText.addElement(paragraphConteneur);
        cellConteneurText.setBorder(Rectangle.NO_BORDER);
        cellConteneurText.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        tableLigne2.addCell(cellConteneurText);
        try {
            document.add(tableLigne2);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        pagenumber++;
    }

    /**
     * Adds the header and the footer.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onEndPage(PdfWriter writer, Document document)
    {
        /*
         * ZONE PIED DE PAGE
         * */
        String preparer_par = "";
        String valider_par = "";
        if(ph_preparation.getPreparateur() != null)
        {
            String[] tab_preparateur = ph_preparation.getPreparateur().split("\\(");
            preparer_par = tab_preparateur[0];
            String[] tab_valider_par = tab_preparateur[1].split("\\)");
            valider_par = tab_valider_par[0];
        }
        Utilisateur userLivreur = UtilisateurOpenHelper.getUtilisateurByID(db, ph_preparation.getLivreur_userID());
        String livrepar = "";
        if(userLivreur != null)
        {
            livrepar = userLivreur.getNom()+" "+userLivreur.getPrenom();
        }
        // COORDONNNEE CHAUFFEUR
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateDuJour = new Date();
        String date = dateFormat.format(dateDuJour);

        //Modification format heure
        SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm:ss");
        Date heureDuJour = new Date();
        String heure = heureFormat.format(heureDuJour);
        PdfPTable tablePiedDePage2 = new PdfPTable(1);
        tablePiedDePage2.setWidthPercentage(100);
        Paragraph textValidePreparerBon = new Paragraph("Préparée par "+preparer_par+" > Validée par "+valider_par+" > Livrée par "+livrepar+" (Le "+date+" à "+heure+")", detailFont);
        PdfPCell cellValidePreparerFooter = new PdfPCell();
        cellValidePreparerFooter.addElement(textValidePreparerBon);
        cellValidePreparerFooter.setBorder(Rectangle.NO_BORDER);
        tablePiedDePage2.addCell(cellValidePreparerFooter);


        /*try {
            document.add(tablePiedDePage2);
        } catch (DocumentException e) {
            e.printStackTrace();
        }*/

        PdfPTable tablePiedDePage = new PdfPTable(3);
        int[] tailleCellFooter = new int[]{100, 30, 50};
        try {
            tablePiedDePage.setWidths(tailleCellFooter);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        PdfContentByte canvas = writer.getDirectContent();
        tablePiedDePage.setSpacingBefore(5);
        Rectangle rect = writer.getBoxSize("art");
        /*ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_CENTER, new Phrase(String.format("page %d", pagenumber)),
                (rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18, 0);*/


        Chunk commentaireFooterChunk = new Chunk("Commentaire", underline);
        Paragraph textCommentaireFooter = new Paragraph(commentaireFooterChunk+"\n"+ph_preparation.getCommentaires());

        PdfPCell cellTitreCommentaireFooter = new PdfPCell();
        cellTitreCommentaireFooter.addElement(textCommentaireFooter);
        cellTitreCommentaireFooter.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM);

        tablePiedDePage.addCell(cellTitreCommentaireFooter);

        String path = activity.getFilesDir().getAbsolutePath()+ File.separator + "Documents/" +  signatureNameChauffeur + ".jpeg";
        // SIGNATURE
        Image img = null;
        try {
            img = Image.getInstance(path);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PdfPCell cellImage = new PdfPCell(img, true);
        cellImage.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM);
        cellImage.setFixedHeight(30f);
        tablePiedDePage.addCell(cellImage);

        Paragraph textMontantFooter = new Paragraph("Mt HT : "+ph_preparation.getMontant_HT()+"\nMt TTC : "+ph_preparation.getMontant_TTC()+"\nPoids : "+ph_preparation.getPoids()+"\nVolume : "+ph_preparation.getVolume());
        PdfPCell cellTitreMontantFooter = new PdfPCell();
        cellTitreMontantFooter.addElement(textMontantFooter);
        cellTitreMontantFooter.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM);
        tablePiedDePage.addCell(cellTitreMontantFooter);
       // tablePiedDePage.writeSelectedRows(0, -1, (rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18, canvas);
        tablePiedDePage.setTotalWidth(document.right(document.rightMargin()) - document.left(document.leftMargin()));
        tablePiedDePage.writeSelectedRows(0, -1, document.left(document.leftMargin()), tablePiedDePage.getTotalHeight() + document.bottom(document.bottomMargin()), writer.getDirectContent());
        tablePiedDePage2.setTotalWidth(document.right(document.rightMargin()) - document.left(document.leftMargin()));
        tablePiedDePage2.writeSelectedRows(0, -1, document.left(document.leftMargin()), tablePiedDePage2.getTotalHeight() + document.bottom(document.bottomMargin()+tablePiedDePage.getTotalHeight()), writer.getDirectContent());
        /*try {

            document.add(tablePiedDePage);
        } catch (DocumentException e) {
            e.printStackTrace();
        }*/
    }
}