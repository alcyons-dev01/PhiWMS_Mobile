package fr.alcyons.phiwms_mobile.Outils;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {

    /**
     * Corrige une date au format "yyMMdd".
     * Si le jour est "00", remplace par le dernier jour du mois correspondant.
     *
     * @param dateEntree chaîne au format "yyMMdd" (ex : "250200")
     * @return chaîne corrigée au format "yyMMdd" (ex : "250229")
     */
    public static String normaliserDateYYMMDD(String dateEntree) {
        if (dateEntree == null || dateEntree.length() != 6) return dateEntree;

        String yy = dateEntree.substring(0, 2);
        String mm = dateEntree.substring(2, 4);
        String dd = dateEntree.substring(4, 6);

        try {
            int annee = Integer.parseInt(yy);
            int mois = Integer.parseInt(mm);
            int jour = Integer.parseInt(dd);

            // Si le jour est 00 → dernier jour du mois
            if (jour == 0) {
                Calendar calendrier = Calendar.getInstance(Locale.FRANCE);
                calendrier.clear();
                calendrier.set(Calendar.YEAR, 2000 + annee); // année sur base 2000
                calendrier.set(Calendar.MONTH, mois - 1);
                int dernierJour = calendrier.getActualMaximum(Calendar.DAY_OF_MONTH);
                jour = dernierJour;
            }

            return String.format(Locale.FRANCE, "%02d%02d%02d", annee, mois, jour);

        } catch (NumberFormatException e) {
            return dateEntree;
        }
    }

    /**
     * Corrige une date au format "yyMMdd".
     * Si le jour est "00", remplace par le dernier jour du mois correspondant.
     *
     * @param dateEntree chaîne au format "yyMMdd" (ex : "250200")
     * @return chaîne corrigée au format "dd/MM/yyyy" (ex : "250229")
     */
    public static String normaliserDateddMMyyyy(String dateEntree) {
        if (dateEntree == null || dateEntree.length() != 6) return dateEntree;

        String yy = dateEntree.substring(0, 2);
        String mm = dateEntree.substring(2, 4);
        String dd = dateEntree.substring(4, 6);

        try {
            int annee = Integer.parseInt(yy);
            int mois = Integer.parseInt(mm);
            int jour = Integer.parseInt(dd);

            Calendar calendrier = Calendar.getInstance(Locale.FRANCE);
            calendrier.clear();
            calendrier.set(Calendar.YEAR, 2000 + annee);
            calendrier.set(Calendar.MONTH, mois - 1);

            // Si le jour est 00 → dernier jour du mois
            if (jour == 0) {
                jour = calendrier.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            calendrier.set(Calendar.DAY_OF_MONTH, jour);

            // Format français jj/MM/aaaa
            SimpleDateFormat formatFrancais = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            return formatFrancais.format(calendrier.getTime());

        } catch (NumberFormatException e) {
            return dateEntree;
        }
    }

    /**
     * Convertit une date "yyMMdd" en format ISO "yyyy-MM-dd" pour stockage en base.
     * Si le jour est "00", utilise le dernier jour du mois.
     *
     * @param dateEntree chaîne au format "yyMMdd" (ex : "250200")
     * @return chaîne au format "yyyy-MM-dd" (ex : "2025-02-28")
     */
    public static String convertirEnDateISO(String dateEntree) {
        if (dateEntree == null || dateEntree.length() != 6) return null;

        String yy = dateEntree.substring(0, 2);
        String mm = dateEntree.substring(2, 4);
        String dd = dateEntree.substring(4, 6);

        try {
            int annee = 2000 + Integer.parseInt(yy);
            int mois = Integer.parseInt(mm);
            int jour = Integer.parseInt(dd);

            Calendar calendrier = Calendar.getInstance(Locale.FRANCE);
            calendrier.clear();
            calendrier.set(Calendar.YEAR, annee);
            calendrier.set(Calendar.MONTH, mois - 1);

            // Si jour = 00 → dernier jour du mois
            if (jour == 0) {
                jour = calendrier.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            calendrier.set(Calendar.DAY_OF_MONTH, jour);

            // Conversion au format ISO
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
            return sdf.format(calendrier.getTime());

        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static long returnTimestamp(String chaineDate, String format)
    {
        try {
            DateTimeFormatter fmt = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fmt = new DateTimeFormatterBuilder()
                        .parseCaseSensitive()
                        .appendPattern(format)
                        // Par défaut, si le format ne contient pas de jour (ex: "yyMM"), on met 01
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        // Par défaut, si le format ne contient pas d’heure, on met 00:00:00
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.STRICT);

                // Interprétation en UTC (comme dayjs.utc)
                LocalDateTime ldt = null;
                ldt = LocalDateTime.parse(chaineDate, fmt);
                return ldt.toEpochSecond(ZoneOffset.UTC); // secondes depuis 1970
            }
            else
            {
                return -1;
            }
        }
        catch (Exception e) {
            return -1;
        }
    }

    public static String timestampToDate(long timestamp)
    {
        if (timestamp == -1) {
            return "00/00/0000";
        }

        // secondes → Instant
        Instant instant_VI = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instant_VI = Instant.ofEpochSecond(timestamp);
            // UTC (équivalent getUTC*)
            ZonedDateTime zdt_VD = instant_VI.atZone(ZoneOffset.UTC);

            int jour_VE = zdt_VD.getDayOfMonth();
            int mois_VE = zdt_VD.getMonthValue();
            int annee_VE = zdt_VD.getYear() % 100; // 2 chiffres

            return String.format("%02d/%02d/%02d", jour_VE, mois_VE, annee_VE);
        }
        else
        {
            return "00/00/0000";
        }
    }

    // Exemple d’utilisation :
    public static void main(String[] args) {
        System.out.println(normaliserDateYYMMDD("250200")); // → 250229
        System.out.println(convertirEnDateISO("250200"));   // → 2025-02-28

        System.out.println(normaliserDateYYMMDD("241100")); // → 241130
        System.out.println(convertirEnDateISO("241100"));   // → 2024-11-30
    }
}
