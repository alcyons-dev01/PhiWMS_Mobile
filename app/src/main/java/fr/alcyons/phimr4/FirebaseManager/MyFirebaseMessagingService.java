package fr.alcyons.phimr4.FirebaseManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.R;
import me.leolin.shortcutbadger.ShortcutBadger;

import static android.content.ContentValues.TAG;

/**
 * Created by quentinlanusse on 27/07/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        String from = remoteMessage.getFrom();
        Map data = remoteMessage.getData();
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String bodyNotification = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();
        String title = remoteMessage.getNotification().getTitle();

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + from);

        // Check if message contains a data payload.
        if (data.size() > 0) {
            Log.d(TAG, "Message data payload: " + data);
        }

        // Check if message contains a notification payload.
        if (notification != null) {
            Log.d(TAG, "Message Notification Body: " + bodyNotification);
        }

        /*Context context = getApplicationContext();
        Alerte.afficherAlerte(context, "Message reçu", "Yay !!", "alerte");*/


        // Access the default SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // The SharedPreferences editor - must use commit() to submit changes
        SharedPreferences.Editor editor = preferences.edit();

        int badgeCount = preferences.getInt("badgeCount", 0);
        badgeCount++;

        // Edit the saved preferences
        editor.putInt("badgeCount", badgeCount);
        editor.commit();

        Context context = getApplicationContext();
        if (ShortcutBadger.isBadgeCounterSupported(context)) {
            ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
        }

        sendMessage(title, bodyNotification, click_action);


    }

    @Override
    public void onDeletedMessages() {
        // Access the default SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // The SharedPreferences editor - must use commit() to submit changes
        SharedPreferences.Editor editor = preferences.edit();

        int badgeCount = preferences.getInt("badgeCount", 0);
        badgeCount--;

        // Edit the saved preferences
        editor.putInt("badgeCount", badgeCount);
        editor.commit();

        Context context = getApplicationContext();
        if (ShortcutBadger.isBadgeCounterSupported(context)) {
            ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
        }
    }

    private void sendMessage(String title, String message, String click_action) {
        Intent intent = null;
        PendingIntent pendingIntent = null;
        if(click_action == null)
        {
            click_action = "";
        }

        if (click_action.equals("notification")) {
            intent = new Intent(MyFirebaseMessagingService.this, AuthentificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if (intent != null) {
                pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            }

            //gestion d'un id random
            //gestion de l'ID unique et random
            Random random = new Random();
            int notifID = random.nextInt();
            if (notifID > 0) {
                notifID = notifID * -1;
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_colis)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(notifID, builder.build());

        } else if (click_action.equals("")) {
            //gestion d'un id random
            //gestion de l'ID unique et random
            Random random = new Random();
            int notifID = random.nextInt();
            if (notifID > 0) {
                notifID = notifID * -1;
            }

            Intent showIntent = new Intent();
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showIntent, 0);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_colis)
                    .setContentTitle(title)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setContentText(message);
            builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(notifID, builder.build());


            //removeNotification(manager, notifID);
        }

    }

}
