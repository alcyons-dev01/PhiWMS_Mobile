package fr.alcyons.phimr4.Notifications;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.NotificationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.Classes.Notification;
import fr.alcyons.phimr4.Classes.Service;
import fr.alcyons.phimr4.ListViewAdapters.NotificationAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.Outils.SimpleMultiChoiceModeListener;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;
import me.leolin.shortcutbadger.ShortcutBadger;

public class ServiceNotificationsActivity extends ServiceAvecConnexionActivity {

    public TextView nbNotificationTextView;
    List<Notification> notificationList;
    NotificationAdapter notificationAdapter;
    ListView notificationListView;
    JSONArray notificationJSONArray;
    List<String> channelListString = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_notifications);


        // Récupération de la liste_view à remplir
        notificationListView = (ListView) findViewById(R.id.listeView);

        // Initialiser la liste des channels
        String etablissementNom = ParametresServeurOpenHelper.getEtablissementNom(db);
        String port = ParametresServeurOpenHelper.getPortServeur(db);

        if (etablissementNom.contentEquals("")) {
            if (port.contentEquals("81")) {
                etablissementNom = "CALYDIAL";
            } else if (port.contentEquals("82")) {
                etablissementNom = "APAIR";
            } else if (port.contentEquals("83")) {
                etablissementNom = "APURAD";
            } else if (port.contentEquals("84")) {
                etablissementNom = "ADH";
            } else if (port.contentEquals("85")) {
                etablissementNom = "ATIR";
            }
        }

        if (etablissementNom.length() > 0) {

            for (Service service : utilisateurConnecte.getServicesHabilites()) {
                String perimetreFonctionnelNom = service.getNomPerimetrefonctionnel();
                String channel = "Channel_" + perimetreFonctionnelNom.toLowerCase().replaceAll(" ", "") + "_" + etablissementNom;
                if (channelListString.indexOf(channel) == -1) {
                    channelListString.add(channel);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        notificationList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceNotificationsActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ServiceNotificationsActivity.this, "Veuillez patienter", "Synchronisation des notifications en cours");
            }

            // Vérifier qu'il n'y a pas de notification n'étant pas à destination de l'utilisateur connecté
            for (Notification notification : gestionnaireNotification.getAllNotifications(db)) {
                if (channelListString.indexOf(notification.getChannel()) == -1) {
                    gestionnaireNotification.supprimerUneNotification(db, notification);
                }
            }

            // Récupération des notifications par channel
            for (String channel : channelListString) {

                RequestQueue requestQueueNotification = Volley.newRequestQueue(ServiceNotificationsActivity.this);
                String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceNotification + "/channels/" + channel;

                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                        new Response.Listener<JSONObject>() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int resultCount = response.getInt("resultCount");
                                    if (resultCount == 0) {
                                        String erreur = response.getString("erreur");
                                        if (erreur.equals(getString(R.string.tokenInvalide))) {
                                            Alerte.afficherAlerte(ServiceNotificationsActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                            DBOpenHelper.viderBasesDeDonnees(db);
                                            ServiceNotificationsActivity.this.finishAffinity();
                                            Intent serviceNotificationsIntent = new Intent(ServiceNotificationsActivity.this, AuthentificationActivity.class);
                                            ServiceNotificationsActivity.this.startActivity(serviceNotificationsIntent);
                                        } else if (erreur.equals(getString(R.string.tokenExpire))) {
                                            Alerte.afficherAlerte(ServiceNotificationsActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", "alerte");
                                            ServiceNotificationsActivity.this.finishAffinity();
                                            Intent serviceNotificationsIntent = new Intent(ServiceNotificationsActivity.this, AuthentificationActivity.class);
                                            ServiceNotificationsActivity.this.startActivity(serviceNotificationsIntent);
                                        } else if (!erreur.equals(getString(R.string.aucuneNotification))) {
                                            Alerte.afficherAlerte(ServiceNotificationsActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete service notification", "alerte");
                                        }

                                    } else {
                                        notificationJSONArray = response.getJSONArray("Notifications");
                                        for (int i = 0; i < notificationJSONArray.length(); i++) {
                                            JSONObject notificationJSONObject = notificationJSONArray.getJSONObject(i);
                                            Notification notificationCourant = new Notification(notificationJSONObject);
                                            Notification notificationEnBDD = gestionnaireNotification.getUneNotificationByID(db, notificationCourant);
                                            // Si la notification n'est pas déjà présente, alors l'enregistrer.
                                            if (notificationEnBDD == null) {
                                                gestionnaireNotification.insererUneNotificationEnBDD(db, notificationCourant);
                                            }
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                handler.sendMessage(handler.obtainMessage());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(ServiceNotificationsActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP service notification", "alerte");
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", utilisateurConnecte.getToken());
                        return headers;
                    }
                };
                obreq.setRetryPolicy(retryPolicy);
                requestQueueNotification.add(obreq);
                try {
                    Looper.loop();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
            passageParOnCreate = false;
        }
        // Récupération de la liste des notifications
        notificationList = gestionnaireNotification.getAllNotifications(db);
        // Tri par Date : de la plus récente à la plus ancienne
        Collections.sort(notificationList, new Comparator<Notification>() {
            @Override
            public int compare(Notification o1, Notification o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        // Nombre de notification non lu
        int nbNotificationNonLu = gestionnaireNotification.getNbNotificationsNonLu(db);
        nbNotificationTextView = ((TextView) findViewById(R.id.nbNotification));
        nbNotificationTextView.setText(String.valueOf(nbNotificationNonLu));

        notificationAdapter = new NotificationAdapter(ServiceNotificationsActivity.this, db, notificationList);
        notificationListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        final SimpleMultiChoiceModeListener cml = new SimpleMultiChoiceModeListener(ServiceNotificationsActivity.this, nbNotificationTextView, notificationAdapter);
        notificationListView.setMultiChoiceModeListener(cml);
        notificationListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                view.setActivated(true);
                ((ListView) view).setItemChecked(position, !((NotificationAdapter) adapterView.getAdapter()).isPositionChecked(position));

                return false;
            }
        });

        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification notificationSelectionne = (Notification) notificationAdapter.getItem(position);
                if (!notificationSelectionne.isaEteGeree()) {

                    // Permet de mettre à jour la " pastille " contenant le nombre de notification reçu
                    Context contextFirebase = ServiceNotificationsActivity.this.getApplicationContext();

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contextFirebase);
                    // The SharedPreferences editor - must use commit() to submit changes
                    SharedPreferences.Editor editor = preferences.edit();

                    int badgeCount = preferences.getInt("badgeCount", 0);
                    if (badgeCount > 0) {

                        badgeCount--;

                        // Edit the saved preferences
                        editor.putInt("badgeCount", badgeCount);
                        editor.apply();


                        if (ShortcutBadger.isBadgeCounterSupported(contextFirebase)) {
                            ShortcutBadger.applyCount(contextFirebase, badgeCount);
                        }
                    }

                    // Mise à jour de la notification en base de donnée
                    notificationSelectionne.setaEteGeree(true);
                    NotificationOpenHelper.mettraAjourNotification(db, notificationSelectionne);

                    int nbNotificationNonLu = Integer.parseInt(nbNotificationTextView.getText().toString());
                    nbNotificationNonLu--;
                    nbNotificationTextView.setText(String.valueOf(nbNotificationNonLu));

                    notificationAdapter.notifyDataSetChanged();
                }
            }
        });

        notificationListView.setDivider(footer);
        notificationListView.setAdapter(notificationAdapter);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu action et utilisation de l'item ADD
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuAdd).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Récupération de l'item ADD et affectation de l'action à réaliser lors d'un clic
        MenuItem item = menu.findItem(R.id.menuAdd);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuAddClick();
                return true;
            }
        });
        return true;
    }

    private void onMenuAddClick() {
        passageParOnCreate = true;
        Intent serviceNotificationsIntent = new Intent(ServiceNotificationsActivity.this, EnvoyerNotificationActivity.class);
        // Récupération des éléments à transmettre à la prochaine activité
        Bundle serviceNotificationsBundle = ServiceNotificationsActivity.super.getBundle();
        serviceNotificationsBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        serviceNotificationsIntent.putExtras(serviceNotificationsBundle);
        // Appel de la prochaine activité
        ServiceNotificationsActivity.this.startActivity(serviceNotificationsIntent);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
