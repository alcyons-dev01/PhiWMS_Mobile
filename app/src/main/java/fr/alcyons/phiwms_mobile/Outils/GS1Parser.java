package fr.alcyons.phiwms_mobile.Outils;

import java.util.regex.*;
import java.util.*;
import java.text.*;

public class GS1Parser {
    public static class GS1Result {
        public String productCode = "";
        public String expirationDate = "";
        public String expirationDateAffichage = "";
        public String expirationDateSQLFormat = "";
        public String lotNumber = "";
        public String serie = "";
        public Integer packaging = 0; // Valeur par défaut
    }

    public static GS1Result parseGS1Code(String value) {
        GS1Result result = new GS1Result();

        // Nettoyage du code brut
        String sanitizedValue = value.replace("\n", "").replace(" ", "");

        // GS separator (ASCII 29)
        String gs = "\\u001D";

        // Expression régulière complète avec (30)
        Pattern pattern = Pattern.compile(
                "(?:\\(01\\)|01)(\\d{14})(?:" + gs + ")?"
                        + "|(?:\\(17\\)|17)(\\d{6})(?:" + gs + ")?"
                        + "|(?:\\(10\\)|10)([!%-?A-Za-z0-9_\\x22]{1,20})(?:" + gs + ")?"
                        + "|(?:\\(21\\)|21)([!%-?A-Za-z0-9_\\x22]{1,20})(?:" + gs + ")?"
                        + "|(?:\\(30\\)|30)(\\d{1,8})(?:" + gs + ")?"
        );

        Matcher matcher = pattern.matcher(sanitizedValue);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                result.productCode = matcher.group(1);
            } else if (matcher.group(2) != null) {
                result.expirationDate = DateUtils.normaliserDateYYMMDD(matcher.group(2));
                result.expirationDateAffichage = DateUtils.normaliserDateddMMyyyy(matcher.group(2));
                result.expirationDateSQLFormat = DateUtils.convertirEnDateISO(matcher.group(2));
            } else if (matcher.group(3) != null) {
                result.lotNumber = matcher.group(3);
            } else if (matcher.group(4) != null) {
                result.serie = matcher.group(4);
            } else if (matcher.group(5) != null) {
                result.packaging = Integer.parseInt(matcher.group(5));
            }
        }

        return result;
    }

    // Exemple d’utilisation :
    public static void main(String[] args) {
        String code = "(01)03453120000011(17)250101(10)LOT123(21)SERIE456(30)25";
        GS1Result r = parseGS1Code(code);

        System.out.println("GTIN : " + r.productCode);
        System.out.println("Date d’expiration : " + r.expirationDate);
        System.out.println("Lot : " + r.lotNumber);
        System.out.println("Série : " + r.serie);
        System.out.println("Conditionnement : " + r.packaging);
    }
}
