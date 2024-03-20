package fr.alcyons.phimr4.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import java.util.ArrayList;
import java.util.List;
import fr.alcyons.phimr4.Classes.Notification;

/**
 * Created by quentinlanusse on 31/07/2017.
 */

public class NotificationOpenHelper extends DBOpenHelper {

    public NotificationOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static int getNbNotificationsNonLu(SQLiteDatabase db) {
        int nbNotificationNonLu = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_NOTIFICATION, null);

        while (cursor.moveToNext()) {
            Notification notif = new Notification(cursor);
            if (!notif.isaEteGeree()) {
                nbNotificationNonLu++;
            }
        }
        cursor.close();
        cursor = null;
        return nbNotificationNonLu;
    }

    public static void supprimerNotificationVerification(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_NOTIFICATION, NotificationOpenHelper.Constantes.CLE_COL_TITRE_NOTIFICATION + "=?", new String[]{"PHIMR4 : Code de vérification"});
    }

    public static void supprimerUneNotification(SQLiteDatabase db, Notification notification) {
        db.delete(Constantes.TABLE_NOTIFICATION, DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=?", new String[]{String.valueOf(notification.getPhiMR4UUID())});
    }

    public static List<Notification> getAllNotifications(SQLiteDatabase db) {
        List<Notification> notificationList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_NOTIFICATION, null);

        while (cursor.moveToNext()) {
            notificationList.add(new Notification(cursor));
        }
        cursor.close();
        cursor = null;
        return notificationList;
    }

    public static long insererUneNotificationEnBDD(SQLiteDatabase db, Notification notif) {
        // Récupération des éléments de la notif
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_NOTIFICATION, notif.getId());
        contentValues.put(Constantes.CLE_COL_TITRE_NOTIFICATION, notif.getTitre());
        contentValues.put(Constantes.CLE_COL_BODY_NOTIFICATION, notif.getBody());
        contentValues.put(Constantes.CLE_COL_COLOR_NOTIFICATION, notif.getColor());
        contentValues.put(Constantes.CLE_COL_TAG_NOTIFICATION, notif.getTag());
        contentValues.put(Constantes.CLE_COL_DATE_NOTIFICATION, notif.getDate());
        contentValues.put(Constantes.CLE_COL_AETEGEREE_NOTIFICATION, notif.isaEteGeree());
        contentValues.put(Constantes.CLE_COL_CHANNEL_NOTIFICATION, notif.getChannel());

        // Insertion de la notif
        long rowID = db.insert(Constantes.TABLE_NOTIFICATION, null, contentValues);

        notif.setPhiMR4UUID((int) rowID);

        return rowID;
    }

    public static Notification getUneNotificationByID(SQLiteDatabase db, Notification notif) {
        Notification notification = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_NOTIFICATION + " WHERE " + Constantes.CLE_COL_TITRE_NOTIFICATION + "=? AND " +
                        Constantes.CLE_COL_BODY_NOTIFICATION + "=? AND " +
                        Constantes.CLE_COL_COLOR_NOTIFICATION + "=? AND " +
                        Constantes.CLE_COL_TAG_NOTIFICATION + "=? AND " +
                        Constantes.CLE_COL_DATE_NOTIFICATION + "=? AND " +
                        Constantes.CLE_COL_CHANNEL_NOTIFICATION + "=?",
                new String[]{notif.getTitre(), notif.getBody(), notif.getColor(), notif.getTag(), notif.getDate(), notif.getChannel()});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            notification = new Notification(cursor);
        }
        cursor.close();
        cursor = null;
        return notification;
    }

    public static long mettraAjourNotification(SQLiteDatabase db, Notification notif) {
        // Récupération des éléments de la notif
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_NOTIFICATION, notif.getId());
        contentValues.put(Constantes.CLE_COL_TITRE_NOTIFICATION, notif.getTitre());
        contentValues.put(Constantes.CLE_COL_BODY_NOTIFICATION, notif.getBody());
        contentValues.put(Constantes.CLE_COL_COLOR_NOTIFICATION, notif.getColor());
        contentValues.put(Constantes.CLE_COL_TAG_NOTIFICATION, notif.getTag());
        contentValues.put(Constantes.CLE_COL_DATE_NOTIFICATION, notif.getDate());
        contentValues.put(Constantes.CLE_COL_AETEGEREE_NOTIFICATION, notif.isaEteGeree());
        contentValues.put(Constantes.CLE_COL_CHANNEL_NOTIFICATION, notif.getChannel());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID, notif.getPhiMR4UUID());

        // Mise à jour
        return db.update(Constantes.TABLE_NOTIFICATION, contentValues, DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=?", new String[]{String.valueOf(notif.getPhiMR4UUID())});
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_NOTIFICATION = "Notification";

        public static final String CLE_COL_TITRE_NOTIFICATION = "titre";
        public static final int NUM_COL_TITRE_NOTIFICATION = 1;
        public static final String TYPE_COL_TITRE_NOTIFICATION = "TEXT";

        public static final String CLE_COL_BODY_NOTIFICATION = "body";
        public static final int NUM_COL_BODY_NOTIFICATION = 2;
        public static final String TYPE_COL_BODY_NOTIFICATION = "TEXT";

        public static final String CLE_COL_COLOR_NOTIFICATION = "color";
        public static final int NUM_COL_COLOR_NOTIFICATION = 3;
        public static final String TYPE_COL_COLOR_NOTIFICATION = "TEXT";

        public static final String CLE_COL_TAG_NOTIFICATION = "tag";
        public static final int NUM_COL_TAG_NOTIFICATION = 4;
        public static final String TYPE_COL_TAG_NOTIFICATION = "TEXT";

        public static final String CLE_COL_DATE_NOTIFICATION = "date";
        public static final int NUM_COL_DATE_NOTIFICATION = 5;
        public static final String TYPE_COL_DATE_NOTIFICATION = "TEXT";

        public static final String CLE_COL_AETEGEREE_NOTIFICATION = "aEteGeree";
        public static final int NUM_COL_AETEGEREE_NOTIFICATION = 6;
        public static final String TYPE_COL_AETEGEREE_NOTIFICATION = "INTEGER";

        public static final String CLE_COL_ID_NOTIFICATION = "id";
        public static final int NUM_COL_ID_NOTIFICATION = 7;
        public static final String TYPE_COL_ID_NOTIFICATION = "INTEGER";

        public static final String CLE_COL_CHANNEL_NOTIFICATION = "channel";
        public static final int NUM_COL_CHANNEL_NOTIFICATION = 8;
        public static final String TYPE_COL_CHANNEL_NOTIFICATION = "TEXT";


        public static final String CREATION_TABLE_NOTIFICATION = "CREATE TABLE " + TABLE_NOTIFICATION
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + CLE_COL_TITRE_NOTIFICATION + " " + TYPE_COL_TITRE_NOTIFICATION + ","
                + CLE_COL_BODY_NOTIFICATION + " " + TYPE_COL_BODY_NOTIFICATION + ","
                + CLE_COL_COLOR_NOTIFICATION + " " + TYPE_COL_COLOR_NOTIFICATION + ","
                + CLE_COL_TAG_NOTIFICATION + " " + TYPE_COL_TAG_NOTIFICATION + ","
                + CLE_COL_DATE_NOTIFICATION + " " + TYPE_COL_DATE_NOTIFICATION + ","
                + CLE_COL_AETEGEREE_NOTIFICATION + " " + TYPE_COL_AETEGEREE_NOTIFICATION + ","
                + CLE_COL_ID_NOTIFICATION + " " + TYPE_COL_ID_NOTIFICATION + ","
                + CLE_COL_CHANNEL_NOTIFICATION + " " + TYPE_COL_CHANNEL_NOTIFICATION
                + ");";
    }
}
