package fr.alcyons.phiwms_mobile.Outils;

import java.util.Date;

public class Chronometer {
    public static Date dateLancement;
    public static Date dateFin;
    public static long difference;
    public static String heure;
    public static String minute;
    public static String seconde;

    public static void LancementChrono()
    {
        dateLancement = new Date();
    }

    public static void FinChrono()
    {
        dateFin = new Date();
    }

    public static void getChrono()
    {
        difference = dateFin.getTime() - dateLancement.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = difference / daysInMilli;
        difference = difference % daysInMilli;

        long elapsedHours = difference / hoursInMilli;
        difference = difference % hoursInMilli;

        long elapsedMinutes = difference / minutesInMilli;
        difference = difference % minutesInMilli;

        long elapsedSeconds = difference / secondsInMilli;

        heure = String.valueOf(elapsedHours);
        if(heure.length() ==1)
            heure = "0"+heure;

        minute = String.valueOf(elapsedMinutes);
        if(minute.length() ==1)
            minute = "0"+minute;

        seconde = String.valueOf(elapsedSeconds);
        if(seconde.length() ==1)
            seconde = "0"+seconde;
    }
}
