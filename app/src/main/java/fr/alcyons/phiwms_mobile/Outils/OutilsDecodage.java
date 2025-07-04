package fr.alcyons.phiwms_mobile.Outils;

import android.annotation.TargetApi;
import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.M)
public class OutilsDecodage {

    public static String codeGtin = "codeGtin";
    public static String codeGtinSansAi = "codeGtinSansAi";
    public static String numeroLot = "numLot";
    public static String dateDePeremption = "datePeremption";
    public static String conditionnementProduit = "conditionnement";
    public static String numeroSerie = "numeroSerie";
    public static String gtin_Reconstruit_AvecSerie = "GTIN_Reconstruit_AvecSerie";
    public static String gtin_Reconstruit_SansSerie = "GTIN_Reconstruit_SansSerie";
    public static String dateDeFabrication = "dateDeFabrication";
    public static String dateUtilisation = "dateUtilisation";
    private static String dateFormatScan;
    public static String gtin_code_ascii = "GTIN_Code_Ascii";
    private static String gtin_ascii = "";


    public static Map<String, String> decouperCodeInconnnu(String gs1)
    {
        SimpleDateFormat dateFormat;
        String code_inconnu = "";
        String date = "";
        String lot = "";
        Map<String, String> mapResultat = new HashMap<>();
        String[] tab_num_inconnu = gs1.split("dp17");
        if(tab_num_inconnu.length > 1)
        {
            code_inconnu = tab_num_inconnu[0].substring(2);
            String[] tab_date = tab_num_inconnu[1].split("nl10");
            date = tab_date[0];
            lot = tab_date[1];

            date = date.substring(0, 2) + "-" + date.substring(2, 4) + "-" + date.substring(4, 6);

            if (date.endsWith("00")) {
                date = date.substring(0, 5);
                dateFormat = new SimpleDateFormat("yy-MM");
            } else {
                dateFormat = new SimpleDateFormat("yy-MM-dd");
            }

            Date date_VD = null;
            try {
                date_VD = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (date_VD == null) {
                date = "";
            } else {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.format(date_VD);
            }
        }
        else
        {
            String[] tab_lot = gs1.split("nl10");
            if(tab_lot.length > 1)
                lot = tab_lot[1];
            else
                lot = "";
            code_inconnu = tab_lot[0].substring(2);
        }

        if(lot == null)
        {
            lot = "";
        }

        if(date == null)
        {
            date = "";
        }

        if(code_inconnu == null)
        {
            code_inconnu = "";
        }

        mapResultat.put("Code_Inconnu", code_inconnu);
        mapResultat.put("Date", date);
        mapResultat.put("Lot", lot);

        return mapResultat;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Map<String, String> decouperGTIN(String scanner_VA) {

        for (int i = 0; i < scanner_VA.length(); i++) {
            int ascii = (int) scanner_VA.charAt(i);
            chaineAscii(String.valueOf(ascii));
        }

        Map<String, String> map = new HashMap<>();

        String GTIN_AI_VA = "01";
        int GTIN_Lenght_VN = 2+14;  // AI + n14
        String GTIN_VA = "";
        String GTIN_VA_SANS_AI = "";

        // Taille variable
        String SerieNumero_AI_VA = "21";
        int SerieNumero_Lenght_VN = 0;
        int SerieNumero_Lenght_Max_VN = 2+20+1;  // AI + n20 + GS
        String SerieNumero_VA = "";

        String Lot_AI_VA = "10";
        int Lot_Lenght_VN = 0;
        int Lot_Lenght_Max_VN = 2+20+1;  // AI + n20 + GS
        String Lot_VA = "";

        String Conditionnement_AI_VA = "30";
        int Conditionnement_Lenght_VN = 0;
        int Conditionnement_Lenght_Max_VN = 2+8+1;  // AI + n8 + GS
        String Conditionnement_VA = "";

        // Date
        String PeremptionDate_AI_VA = "17";
        int PeremptionDate_Lenght_VN = 2+6 ; // AI + n6
        String PeremptionDate_VA = "";
        String PeremptionAAAAMM_VA = "";

        String FabricationDate_AI_VA = "11";
        int FabricationDate_Lenght_VN = 2+6 ; // AI + n6
        String FabricationDate_VA = "";

        String UtilisationDate_AI_VA = "15";
        int UtilisationDate_Lenght_VN = 2+6 ; // AI + n6
        String UtilisationDate_VA = "";

        if (!scanner_VA.startsWith("01") && !scanner_VA.startsWith("02")) {
            if (scanner_VA.length() > 3) {
                if (scanner_VA.substring(1, 3).contains("01") || scanner_VA.substring(1, 3).contains("02")) {
                    scanner_VA = scanner_VA.substring(1);
                }
                else if(scanner_VA.contains("]C1")){
                    scanner_VA = scanner_VA.substring(3);
                }
                else if(scanner_VA.contains("}C1")){
                    scanner_VA = scanner_VA.substring(3);
                }
                else if(scanner_VA.contains("}d2010")){
                    scanner_VA = scanner_VA.substring(3);
                }
                else if(scanner_VA.contains("]d2010")){
                    scanner_VA = scanner_VA.substring(3);
                }
                else {
                    scanner_VA = "";
                }
            } else {
                scanner_VA = "";
            }
        }

        if (scanner_VA.length() > 16) {
            int i=0;
            while (i<7) {
                String AI_Courant = "";
                if(!scanner_VA.isEmpty())
                    if(scanner_VA.length()>2)
                        AI_Courant = scanner_VA.substring(0,2);

                switch (AI_Courant) {
                    case "01":
                    case "02":
                        GTIN_VA = scanner_VA.substring(0, GTIN_Lenght_VN);
                        GTIN_VA = GTIN_VA.substring(2);
                        scanner_VA = scanner_VA.substring(GTIN_Lenght_VN);
                        GTIN_VA_SANS_AI = GTIN_VA;
                        GTIN_VA = GTIN_AI_VA + GTIN_VA;
                        break;
                    case "21":
                        SerieNumero_Lenght_VN = AI_LengthVariable_Controle(scanner_VA, SerieNumero_Lenght_Max_VN);
                        if(SerieNumero_Lenght_VN != - 1){
                            SerieNumero_Lenght_VN++;
                            if(SerieNumero_Lenght_VN>scanner_VA.length())
                                SerieNumero_Lenght_VN = scanner_VA.length();
                            SerieNumero_VA = scanner_VA.substring(0, SerieNumero_Lenght_VN);
                            SerieNumero_VA = SerieNumero_VA.substring(2);
                            SerieNumero_VA = LastCharSerieLot(SerieNumero_VA);
                            scanner_VA = scanner_VA.substring(SerieNumero_Lenght_VN);
                        }
                        break;
                    case "10":
                        Lot_Lenght_VN = AI_LengthVariable_Controle(scanner_VA, Lot_Lenght_Max_VN);
                        if(Lot_Lenght_VN != - 1){
                            Lot_Lenght_VN++;
                            if(Lot_Lenght_VN>scanner_VA.length())
                                Lot_Lenght_VN = scanner_VA.length();
                            Lot_VA = scanner_VA.substring(0, Lot_Lenght_VN);
                            Lot_VA = Lot_VA.substring(2);
                            Lot_VA = LastCharSerieLot(Lot_VA);
                            scanner_VA = scanner_VA.substring(Lot_Lenght_VN);
                        }
                        break;

                    case "30":
                        Conditionnement_Lenght_VN = AI_LengthVariable_Controle(scanner_VA, Conditionnement_Lenght_Max_VN);
                        if(Conditionnement_Lenght_VN != - 1){
                            Conditionnement_Lenght_VN++;
                            if(Conditionnement_Lenght_VN>scanner_VA.length())
                                Conditionnement_Lenght_VN = scanner_VA.length();
                            Conditionnement_VA =scanner_VA.substring(0, Conditionnement_Lenght_VN);
                            Conditionnement_VA =Conditionnement_VA.substring(2);
                            scanner_VA = scanner_VA.substring(Conditionnement_Lenght_VN);
                        }
                        break;

                    case "17":
                        PeremptionDate_VA = AI_Date_Controle(scanner_VA);
                        if(PeremptionDate_VA.length() > 0)
                            scanner_VA = scanner_VA.substring(PeremptionDate_Lenght_VN);
                        break;

                    case "11":
                        FabricationDate_VA = AI_Date_Controle(scanner_VA);
                        if(FabricationDate_VA.length() > 0)
                            scanner_VA = scanner_VA.substring(FabricationDate_Lenght_VN);
                        break;
                    case "15":
                        UtilisationDate_VA = AI_Date_Controle(scanner_VA);
                        if(UtilisationDate_VA.length() > 0)
                            scanner_VA = scanner_VA.substring(UtilisationDate_Lenght_VN);
                        break;
                }
                i++;
            }
        }


        String GTIN_Reconstruit_AvecSerie = GTIN_VA+"21"+SerieNumero_VA+"@17"+dateFormatScan+"10"+Lot_VA;
        String GTIN_Reconstruit_SansSerie = GTIN_VA+"17"+dateFormatScan+"10"+Lot_VA;

        if (GTIN_VA.length() == 16) {
            map.put(codeGtin, GTIN_VA);
            map.put(codeGtinSansAi, GTIN_VA_SANS_AI);
            map.put(numeroSerie, SerieNumero_VA);
            map.put(numeroLot, Lot_VA);
            map.put(dateDePeremption, PeremptionDate_VA);
            map.put(conditionnementProduit, Conditionnement_VA);
            map.put(gtin_Reconstruit_AvecSerie, GTIN_Reconstruit_AvecSerie);
            map.put(gtin_Reconstruit_SansSerie, GTIN_Reconstruit_SansSerie);
        }
        map.put(gtin_code_ascii, gtin_ascii);
        return map;
    }

    public static int AI_LengthVariable_Controle(String chaine, int longueurMax){

        int longueurChaine = chaine.length();
        if(longueurMax>longueurChaine)
            longueurMax = longueurChaine;

        chaine = chaine.substring(0, longueurMax);

        int positionFinDeChaine = longueurChaine;
        boolean separateurTrouve = false;
        for (int i = 0; i < longueurChaine; i++) {
            int ascii = 0;

            if(i < chaine.length())
                ascii = (int) chaine.charAt(i);

            if(ascii == 29)
            {
                separateurTrouve = true;
            }
            else
            {
                if(ascii == 33 || ascii == 34 || (ascii >=37 && ascii <=47) || (ascii >= 48 && ascii <=57) || (ascii >=65 && ascii <=90) || ascii == 95 || (ascii >= 97 && ascii <=122))
                {
                    separateurTrouve = false;
                }
                else
                {
                    separateurTrouve = true;
                }
            }


            if (separateurTrouve) {
                positionFinDeChaine = i;
                break;
            }
        }

        return positionFinDeChaine;
    }

    public static String AI_Date_Controle(String chaine){

        SimpleDateFormat dateFormat;

        String date_VA = chaine.substring(0, 8);
        dateFormatScan = date_VA.substring(2, date_VA.length());
        date_VA = date_VA.substring(2, 4) + "-" + date_VA.substring(4, 6) + "-" + date_VA.substring(6, 8);

        if (date_VA.endsWith("00")) {
            date_VA = date_VA.substring(0, 5);
            dateFormat = new SimpleDateFormat("yy-MM");
        } else {
            dateFormat = new SimpleDateFormat("yy-MM-dd");
        }

        Date date_VD = null;
        try {
            date_VD = dateFormat.parse(date_VA);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date_VD == null) {
            return "";
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date_VA = dateFormat.format(date_VD);
            return date_VA;
        }
    }

    private static String LastCharSerieLot(String string)
    {
        String lastChar = string.substring(string.length()-1);
        if(lastChar.contentEquals("@"))
            string = string.substring(0, string.length()-1);

        return string;
    }

    private static void chaineAscii(String codeAscii)
    {
        gtin_ascii += codeAscii+"|";
    }
}
