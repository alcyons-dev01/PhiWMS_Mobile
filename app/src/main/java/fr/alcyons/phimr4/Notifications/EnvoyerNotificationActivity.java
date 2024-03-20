package fr.alcyons.phimr4.Notifications;


import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.Classes.Service;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

/**
 * Created by jessica on 22/11/2017.
 */

public class EnvoyerNotificationActivity extends ServiceAvecConnexionActivity {

    EditText titreNotificationEditText;
    EditText messageNotificationEditText;
    Spinner destinataireSpinner;

    List<String> channelListString;
    List<String> spinnerListString;

    String destinataire = "";

    @Override
    protected void onCreate(Bundle savedInstanceStat) {
        super.onCreate(savedInstanceStat);
        setContentView(R.layout.activity_envoyer_notifications);

        channelListString = new ArrayList<>();
        spinnerListString = new ArrayList<>();

        titreNotificationEditText = (EditText) findViewById(R.id.titreNotification);
        titreNotificationEditText.setText("");
        messageNotificationEditText = (EditText) findViewById(R.id.messageNotification);
        messageNotificationEditText.setText("");

        destinataireSpinner = (Spinner) findViewById(R.id.spinnerDestinataire);

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
                    spinnerListString.add(perimetreFonctionnelNom);
                }
            }
        }

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerListString);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destinataireSpinner.setAdapter(spinnerArrayAdapter);
        destinataireSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        destinataire = spinnerListString.get(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSend).setVisible(true);
        return true;
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuSend);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuSendClick();
                return true;
            }
        });
        return true;
    }

    private void onMenuSendClick() {
        String messageNotification = messageNotificationEditText.getText().toString();
        String titreNotification = titreNotificationEditText.getText().toString();
        if (messageNotification.trim().equals("") || titreNotification.trim().equals("")) {
            Alerte.afficherAlerte(EnvoyerNotificationActivity.this, "Erreur", "Veuillez saisir un objet et un message !", "alerte");
        } else {
            /* Code nécessaire afin de réaliser une requête à l' API */
            if (OutilsGestionConnexionReseau.isServerAccessible(EnvoyerNotificationActivity.this)) {
                JSONObject body = new JSONObject();

                int index = spinnerListString.indexOf(destinataire);
                String channel = channelListString.get(index);

                try {
                    body.put("body", messageNotificationEditText.getText().toString());
                    body.put("title", titreNotificationEditText.getText().toString());
                    body.put("channel", channel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue requestQueueNotification = Volley.newRequestQueue(EnvoyerNotificationActivity.this);
                String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceNotification + "/";

                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 1) {
                                Toast toast = Toast.makeText(EnvoyerNotificationActivity.this, "Notification envoyée !", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                titreNotificationEditText.setText("");
                                messageNotificationEditText.setText("");
                            } else {
                                Alerte.afficherAlerte(EnvoyerNotificationActivity.this, "Erreur", "Notification non envoyée !", "alerte");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(EnvoyerNotificationActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP envoyer notification", "alerte");
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
            } else {
                Alerte.afficherAlerte(EnvoyerNotificationActivity.this, "Erreur", "Veuillez activer le wifi ou vos données mobiles !", "alerte");
            }
        }
    }

    // Class permettant la gestion du sélecteur
    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            destinataire = parent.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
