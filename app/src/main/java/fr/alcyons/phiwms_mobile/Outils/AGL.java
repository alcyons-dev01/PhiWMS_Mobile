package fr.alcyons.phiwms_mobile.Outils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AGL {

    public static String AGL_AAMMJJ(Date date) {

        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("YYMMdd");
        return simpleDateFormatDate.format(date);
    }

    public static String AGL_HHMMSS(Date date) {
        SimpleDateFormat simpleDateFormatHeure = new SimpleDateFormat("Hms");
        return simpleDateFormatHeure.format(date);
    }
}
