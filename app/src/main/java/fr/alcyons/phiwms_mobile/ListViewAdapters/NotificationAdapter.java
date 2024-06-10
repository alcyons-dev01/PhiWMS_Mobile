package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.NotificationOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Notification;
import fr.alcyons.phiwms_mobile.R;

public class NotificationAdapter extends ArrayAdapter implements Filterable {

    public List<Notification> notificationList;
    public List<Notification> notificationDeBaseList;
    public List<NotificationViewHolder> viewHoldersList;
    public HashMap<Integer, Boolean> notificationSelectedHasMap = new HashMap<>();
    Context context;
    SQLiteDatabase db;

    public NotificationAdapter(Context context, SQLiteDatabase database, List<Notification> notificationList) {
        super(context, 0, notificationList);
        this.context = context;
        this.db = database;
        this.notificationList = notificationList;

        this.viewHoldersList = new ArrayList<>();
        this.notificationDeBaseList = new ArrayList<>();
        for (Notification notification : notificationList
                ) {
            this.notificationDeBaseList.add(notification);
            this.viewHoldersList.add(new NotificationViewHolder());
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_notification, parent, false);
        }

        NotificationViewHolder viewHolder = (NotificationViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = viewHoldersList.get(position);
            viewHolder.titreNotif = (TextView) convertView.findViewById(R.id.titreNotif);
            viewHolder.texteNotif = (TextView) convertView.findViewById(R.id.texteNotif);
            viewHolder.dateNotif = (TextView) convertView.findViewById(R.id.dateNotif);
            viewHolder.heureNotif = (TextView) convertView.findViewById(R.id.heureNotif);
            viewHolder.indicateurNonLu = (ImageView) convertView.findViewById(R.id.indicateurNonLu);
            convertView.setTag(viewHolder);
        }

        Notification notification = (Notification) getItem(position);

        if (notification != null) {
            viewHolder.titreNotif.setText(notification.getTitre());
            viewHolder.texteNotif.setText(notification.getBody());

            Date date = null;
            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = dateDecodeur.parse(notification.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateFormat heureFormat = new SimpleDateFormat("HH:mm:ss");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");

            viewHolder.heureNotif.setText(heureFormat.format(date));
            viewHolder.dateNotif.setText(dateFormat.format(date));

            if (notification.isaEteGeree()) {
                viewHolder.indicateurNonLu.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                viewHolder.titreNotif.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            } else {
                viewHolder.indicateurNonLu.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                viewHolder.titreNotif.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        }

        return convertView;
    }

    public void setNewSelection(int position, boolean value) {
        notificationSelectedHasMap.put(position, value);
    }

    public boolean isPositionChecked(int position) {
        Boolean result = notificationSelectedHasMap.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return notificationSelectedHasMap.keySet();
    }

    public void removeSelection(int position) {
        notificationSelectedHasMap.remove(position);
    }

    public Integer remove() {
        int nbNotificationNonLu = 0;

        for (HashMap.Entry<Integer, Boolean> entry : notificationSelectedHasMap.entrySet()) {
            int position = entry.getKey();
            boolean checked = entry.getValue();
            if (checked) {
                Notification notification = notificationDeBaseList.get(position);
                if (!notification.isaEteGeree()) {
                    nbNotificationNonLu++;
                }
                notificationList.remove(notification);
                NotificationOpenHelper.supprimerUneNotification(db, notification);
            }
        }
        clearSelection();
        return nbNotificationNonLu;
    }

    public void clearSelection() {
        notificationSelectedHasMap = new HashMap<>();
    }

    private class NotificationViewHolder {
        public ImageView indicateurNonLu;
        public TextView titreNotif;
        public TextView texteNotif;
        public TextView dateNotif;
        public TextView heureNotif;
    }
}


