package fr.alcyons.phiwms_mobile.Outils;

import static fr.alcyons.phiwms_mobile.Outils.DateUtils.returnTimestamp;
import static fr.alcyons.phiwms_mobile.Outils.DateUtils.timestampToDate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HIBCParser {

    public static class HIBCResult {
        public String productCode = "";
        public String expirationDate = "";
        public String expirationDateAffichage = "";
        public String expirationDateSQLFormat = "";
        public String lotNumber = "";
        public String serie = "";
        public Integer packaging = 0; // Valeur par défaut
    }

    public static HIBCResult parseHIBCCode(String value) {
        HIBCResult result = new HIBCResult();

        // Nettoyage du code brut
        String sanitizedValue = value.replace("\n", "").replace(" ", "");

        /**
         * Récupération du numéro de produit
         */
        String codeProduit = "";
        Pattern patternProduit = Pattern.compile(".?\\+[a-zA-Z0-9]{4}([a-zA-Z0-9]{1,18})[0-9]{1}(?:[A-Z0-9\\.\\-\\$\\/\\+\\% ]{1}$|\\/.{5,})");
        Matcher matcherProduit = patternProduit.matcher(sanitizedValue);
        while (matcherProduit.find()) {
            if (matcherProduit.group(1) != null) {
                codeProduit = matcherProduit.group(1);
            }
        }

        /**
         * Déduction du format de date
         */
        long timestamp = 0;
        //gestion de la date au format MMYY
        Pattern patternDateMMYY = Pattern.compile(".?(?:\\+|\\/)(?:\\$\\$|\\$\\$8\\d{2}|\\$\\$9\\d{5}|\\$\\$\\+)([0-1]{1}[0-9]{3})[.\\n]?");
        Matcher matcherDateMMYY = patternDateMMYY.matcher(sanitizedValue);
        while (matcherDateMMYY.find()) {
            if (matcherDateMMYY.group(1) != null) {
                timestamp = returnTimestamp(matcherDateMMYY.group(1), "MMYY");
            }
        }

        //gestion de la date au format Julian
        Pattern patternDateJulian = Pattern.compile("(?:\\/\\$\\$5|\\/\\$\\$6|\\$\\$8\\d{2}5|\\$\\$9\\d{5}5|\\$\\$8\\d{2}6|\\$\\$9\\d{5}6|\\/\\$\\$\\+5|\\/\\$\\$\\+6|\\+\\$\\$5|\\+\\$\\$6|\\+\\$\\$\\+5|\\+\\$\\$\\+6|[^\\$]\\+|\\/)([0-9]{2}(?:00[1-9]|0[1-9][0-9]|[12][0-9]{2}|3[0-5][0-9]|36[0-6]))[.\\n]?");
        Matcher matcherDateJulian = patternDateJulian.matcher(sanitizedValue);
        while (matcherDateJulian.find()) {
            if (matcherDateJulian.group(1) != null) {
                timestamp = returnTimestamp(matcherDateJulian.group(1), "yyDDD");
            }
        }

        //gestion de la date au format MMDDYY
        Pattern patternDateMMDDYY = Pattern.compile(".?(?:\\+|\\/)(?:\\$\\$2|\\$\\$8\\d{2}2|\\$\\$9\\d{5}2|\\$\\$\\+2)((?:0[1-9]|1[0-2])(?:0[1-9]|[12][0-9]|3[01])[0-9]{2})[.\\n]?");
        Matcher matcherDateMMDDYY = patternDateMMDDYY.matcher(sanitizedValue);
        while (matcherDateMMDDYY.find()) {
            if (matcherDateMMDDYY.group(1) != null) {
                timestamp = returnTimestamp(matcherDateMMDDYY.group(1), "MMDDYY");
            }
        }

        //gestion de la date au format YYMMDD
        Pattern patternDateYYMMDD = Pattern.compile(".?(?:\\+|\\/)(?:\\$\\$3|\\$\\$8\\d{2}3|\\$\\$9\\d{5}3|\\$\\$8\\d{2}4|\\$\\$9\\d{5}4|\\$\\$4|\\$\\$\\+3|\\$\\$\\+4)([0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12][0-9]|3[01]))[.\\n]?");
        Matcher matcherDateYYMMDD = patternDateYYMMDD.matcher(sanitizedValue);
        while (matcherDateYYMMDD.find()) {
            if (matcherDateYYMMDD.group(1) != null) {
                timestamp = returnTimestamp(matcherDateYYMMDD.group(1), "YYMMDD");
            }
        }

        /**
         * Récupération du numéro de lot
         */
        String lot = "";
        Pattern patternLot = Pattern.compile(".?(?:\\+(?:\\$|\\$\\$7|\\$\\$8\\d{2}7|\\$\\$8\\d{2}[0-1][0-9]{3}|\\$\\$8\\d{2}2[0-9]{6}|\\$\\$8\\d{2}3[0-9]{6}|\\$\\$8\\d{2}4[0-9]{8}|\\$\\$8\\d{2}5[0-9]{5}|\\$\\$8\\d{2}6[0-9]{7}|\\$\\$9\\d{5}7|\\$\\$9\\d{5}[0-1][0-9]{3}|\\$\\$9\\d{5}2[0-9]{6}|\\$\\$9\\d{5}3[0-9]{6}|\\$\\$9\\d{5}4[0-9]{8}|\\$\\$9\\d{5}5[0-9]{5}|\\$\\$9\\d{5}6[0-9]{7}|\\$\\$[0-1][0-9]{3}|\\$\\$2[0-9]{6}|\\$\\$3[0-9]{6}|\\$\\$4[0-9]{8}|\\$\\$5[0-9]{5}|\\$\\$6[0-9]{7})([A-Z0-9]{1,18})[A-Z0-9\\.\\-\\$\\/\\+\\% ]{2}[.\\n]?|\\/(?:\\$|\\$\\$7|\\$\\$[0-1][0-9]{3}|\\$\\$2[0-9]{6}|\\$\\$3[0-9]{6}|\\$\\$4[0-9]{8}|\\$\\$5[0-9]{5}|\\$\\$6[0-9]{7}|\\$\\$8[0-9]{6})([A-Z0-9]{1,18})[A-Z0-9\\.\\-\\$\\/\\+\\% ]{1}[.\\n]?)");
        Matcher matcherLot = patternLot.matcher(sanitizedValue);
        while (matcherLot.find()) {
            if (matcherLot.group(1) != null) {
                lot = matcherLot.group(1);
            } else if (matcherLot.group(2) != null) {
                lot = matcherLot.group(2);
            }
        }

        /**
         * Récupération du numéro de série
         */
        String serie = "";
        //Si le numéro de serie fait parti du code
        Pattern patternSerie = Pattern.compile(".?(?:\\+(?:\\$\\+|\\$\\$\\+7|\\$\\$\\+[0-1][0-9]{3}|\\$\\$\\+2[0-9]{6}|\\$\\$\\+3[0-9]{6}|\\$\\$\\+4[0-9]{8}|\\$\\$\\+5[0-9]{5}|\\$\\$\\+6[0-9]{7})([A-Z0-9]{1,18})[A-Z0-9\\.\\-\\$\\/\\+\\% ]{2}[.\\n]?|\\/(?:\\$\\+|\\$\\$\\+7|\\$\\$\\+[0-1][0-9]{3}|\\$\\$\\+2[0-9]{6}|\\$\\$\\+3[0-9]{6}|\\$\\$\\+4[0-9]{8}|\\$\\$\\+5[0-9]{5}|\\$\\$\\+6[0-9]{7})([A-Z0-9]{1,18})[A-Z0-9\\.\\-\\$\\/\\+\\% ]{1}[.\\n]?)");
        Matcher matcherSerie = patternSerie.matcher(sanitizedValue);
        while (matcherSerie.find()) {
            if (matcherSerie.group(1) != null) {
                serie = matcherSerie.group(1);
            } else if (matcherSerie.group(2) != null) {
                serie = matcherSerie.group(2);
            }
        }

        //Si le numéro de serie est en supplément
        Pattern patternSerieSupplement = Pattern.compile(".?\\/S([A-Z0-9]{1,18})(?:$|\\/)");
        Matcher matcherSerieSupplement = patternSerieSupplement.matcher(sanitizedValue);
        while (matcherSerieSupplement.find()) {
            if (matcherSerieSupplement.group(1) != null) {
                serie = matcherSerieSupplement.group(1);
            }
        }

        /**
         * Quantité
         */
        String quantite = "";
        Pattern patternQuantite = Pattern.compile("/.?\\/Q([0-9]{1,5})[.\\n]?/");
        Matcher matcherQuantite = patternQuantite.matcher(sanitizedValue);
        while (matcherQuantite.find()) {
            if (matcherQuantite.group(1) != null) {
                quantite = matcherQuantite.group(1);
            }
        }

        /**
         * Unité de mesure
         */
        String uniteMesure = "";
        Pattern patternUniteMesure = Pattern.compile(".?\\+[a-zA-Z0-9]{4}[a-zA-Z0-9]{1,18}([0-9]{1})(?:[A-Z0-9\\.\\-\\$\\/\\+\\% ]{1}$|\\/.{5,})");
        Matcher matcherUniteMesure = patternUniteMesure.matcher(sanitizedValue);
        while (matcherUniteMesure.find()) {
            if (matcherUniteMesure.group(1) != null) {
                uniteMesure = matcherUniteMesure.group(1);
            }
        }

        /**
         * Caractere fin de chaine
         */
        String charFinDeChaine = "";
        Pattern patternCharFinDeChaine = Pattern.compile(".?\\+[a-zA-Z0-9]{4}[a-zA-Z0-9]{1,18}[0-9]{1}(?:([A-Z0-9\\.\\-\\$\\/\\+\\% ]{1})$|\\/.{5,}([A-Z0-9\\.\\-\\$\\/\\+\\% ]{1})$)");
        Matcher matcherCharFinDeChaine = patternCharFinDeChaine.matcher(sanitizedValue);
        while (matcherCharFinDeChaine.find()) {
            if (matcherCharFinDeChaine.group(1) != null) {
                charFinDeChaine = matcherCharFinDeChaine.group(1);
            } else if (matcherCharFinDeChaine.group(2) != null) {
                charFinDeChaine = matcherCharFinDeChaine.group(2);
            }
        }

        /**
         * Retour des données
         */
        result.productCode = codeProduit;
        result.expirationDate = timestampToDate(timestamp);
        result.lotNumber = lot;
        result.serie = serie;
        if(quantite != null && !quantite.isEmpty())
            result.packaging = Integer.parseInt(quantite);

        return result;
    }

    // Exemple d’utilisation :
    public static void main(String[] args) {
        String code = "+M684KTONP51R1/$$3231231ABCDEFE";
        HIBCParser.HIBCResult r = parseHIBCCode(code);

        System.out.println("GTIN : " + r.productCode);
        System.out.println("Date d’expiration : " + r.expirationDate);
        System.out.println("Lot : " + r.lotNumber);
        System.out.println("Série : " + r.serie);
        System.out.println("Conditionnement : " + r.packaging);
    }
}
