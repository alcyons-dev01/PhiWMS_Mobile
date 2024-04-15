package fr.alcyons.phiwms_mobile.VerificationConnexion;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.builder.PresenceBuilder;
import com.pubnub.api.builder.SubscribeBuilder;
import com.pubnub.api.builder.UnsubscribeBuilder;
import com.pubnub.api.callbacks.Listener;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.endpoints.DeleteMessages;
import com.pubnub.api.endpoints.FetchMessages;
import com.pubnub.api.endpoints.History;
import com.pubnub.api.endpoints.MessageCounts;
import com.pubnub.api.endpoints.Time;
import com.pubnub.api.endpoints.access.Grant;
import com.pubnub.api.endpoints.access.RevokeToken;
import com.pubnub.api.endpoints.access.builder.GrantTokenBuilder;
import com.pubnub.api.endpoints.channel_groups.AddChannelChannelGroup;
import com.pubnub.api.endpoints.channel_groups.AllChannelsChannelGroup;
import com.pubnub.api.endpoints.channel_groups.DeleteChannelGroup;
import com.pubnub.api.endpoints.channel_groups.ListAllChannelGroup;
import com.pubnub.api.endpoints.channel_groups.RemoveChannelChannelGroup;
import com.pubnub.api.endpoints.files.DeleteFile;
import com.pubnub.api.endpoints.files.DownloadFile;
import com.pubnub.api.endpoints.files.GetFileUrl;
import com.pubnub.api.endpoints.files.ListFiles;
import com.pubnub.api.endpoints.files.PublishFileMessage;
import com.pubnub.api.endpoints.files.SendFile;
import com.pubnub.api.endpoints.message_actions.AddMessageAction;
import com.pubnub.api.endpoints.message_actions.GetMessageActions;
import com.pubnub.api.endpoints.message_actions.RemoveMessageAction;
import com.pubnub.api.endpoints.objects_api.channel.GetAllChannelsMetadata;
import com.pubnub.api.endpoints.objects_api.channel.GetChannelMetadata;
import com.pubnub.api.endpoints.objects_api.channel.RemoveChannelMetadata;
import com.pubnub.api.endpoints.objects_api.channel.SetChannelMetadata;
import com.pubnub.api.endpoints.objects_api.members.GetChannelMembers;
import com.pubnub.api.endpoints.objects_api.members.ManageChannelMembers;
import com.pubnub.api.endpoints.objects_api.members.RemoveChannelMembers;
import com.pubnub.api.endpoints.objects_api.members.SetChannelMembers;
import com.pubnub.api.endpoints.objects_api.memberships.GetMemberships;
import com.pubnub.api.endpoints.objects_api.memberships.ManageMemberships;
import com.pubnub.api.endpoints.objects_api.memberships.RemoveMemberships;
import com.pubnub.api.endpoints.objects_api.memberships.SetMemberships;
import com.pubnub.api.endpoints.objects_api.uuid.GetAllUUIDMetadata;
import com.pubnub.api.endpoints.objects_api.uuid.GetUUIDMetadata;
import com.pubnub.api.endpoints.objects_api.uuid.RemoveUUIDMetadata;
import com.pubnub.api.endpoints.objects_api.uuid.SetUUIDMetadata;
import com.pubnub.api.endpoints.presence.GetState;
import com.pubnub.api.endpoints.presence.HereNow;
import com.pubnub.api.endpoints.presence.SetState;
import com.pubnub.api.endpoints.presence.WhereNow;
import com.pubnub.api.endpoints.pubsub.Publish;
import com.pubnub.api.endpoints.pubsub.Signal;
import com.pubnub.api.endpoints.push.AddChannelsToPush;
import com.pubnub.api.endpoints.push.ListPushProvisions;
import com.pubnub.api.endpoints.push.RemoveAllPushChannelsForDevice;
import com.pubnub.api.endpoints.push.RemoveChannelsFromPush;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.access_manager.v3.PNToken;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;
import com.pubnub.api.v2.BasePNConfiguration;
import com.pubnub.api.v2.callbacks.EventListener;
import com.pubnub.api.v2.callbacks.StatusListener;
import com.pubnub.api.v2.endpoints.pubsub.PublishBuilder;
import com.pubnub.api.v2.endpoints.pubsub.SignalBuilder;
import com.pubnub.api.v2.entities.Channel;
import com.pubnub.api.v2.entities.ChannelGroup;
import com.pubnub.api.v2.entities.ChannelMetadata;
import com.pubnub.api.v2.entities.UserMetadata;
import com.pubnub.api.v2.subscriptions.Subscription;
import com.pubnub.api.v2.subscriptions.SubscriptionOptions;
import com.pubnub.api.v2.subscriptions.SubscriptionSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.R;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by olivier on 05/03/2018.
 */

public class VerificationConnexionActivity extends AppCompatActivity {

    public SQLiteDatabase db;
    protected UtilisateurOpenHelper gestionnaireUtilisateur;
    protected DBOpenHelper gestionnaireBDD;
    EditText verification_code;
    TextView identiteUtilisateur;
    TextView message;
    Button verification_Button;
    Button renvoyer_code;
    Utilisateur utilisateurConnecte;
    String code_verification;
    String channel;
    List<String> channels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_connexion);

        channels = new ArrayList<>();

        //Gestion de la base de données
        gestionnaireUtilisateur = new UtilisateurOpenHelper(VerificationConnexionActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireBDD = new DBOpenHelper(VerificationConnexionActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        db = gestionnaireBDD.openDB();


        //Initialisation des objets graphiques
        verification_code = (EditText) findViewById(R.id.verification_code);
        verification_Button = (Button) findViewById(R.id.verification_Button);
        renvoyer_code = (Button) findViewById(R.id.renvoyer_code);
        identiteUtilisateur = (TextView) findViewById(R.id.identiteUtilisateur);
        message = (TextView) findViewById(R.id.message);


        verification_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeSaisie = verification_code.getText().toString();

                // Access the default SharedPreferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VerificationConnexionActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                int badgeCount = preferences.getInt("badgeCount", 0);
                if (badgeCount > 0) {
                    badgeCount--;
                    if (ShortcutBadger.isBadgeCounterSupported(VerificationConnexionActivity.this)) {
                        // Edit the saved preferences
                        editor.putInt("badgeCount", badgeCount);
                        editor.commit();
                        ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
                    }
                }

                if (codeSaisie == null || codeSaisie.equals("") || codeSaisie.length() != code_verification.length()) {
                    Alerte.afficherAlerte(VerificationConnexionActivity.this, "Erreur", "Veuillez saisir un code valide avant la vérification svp", "alerte");
                    verification_code.setText("");
                } else {
                    if (codeSaisie.equals(code_verification)) {
                        Intent resultIntent = new Intent();
                        Bundle extras = new Bundle();
                        extras.putBoolean("verifier", true);
                        resultIntent.putExtras(extras);
                        setResult(CodesEchangesActivites.RESULT_VERIFICATION_UTILISATEUR, resultIntent);
                        finish();
                    } else {
                        Intent resultIntent = new Intent();
                        Bundle extras = new Bundle();
                        extras.putBoolean("verifier", false);
                        resultIntent.putExtras(extras);
                        setResult(CodesEchangesActivites.RESULT_VERIFICATION_UTILISATEUR, resultIntent);
                        finish();
                    }
                }
            }
        });

        renvoyer_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Access the default SharedPreferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VerificationConnexionActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                int badgeCount = preferences.getInt("badgeCount", 0);
                if (badgeCount > 0) {
                    badgeCount--;
                    if (ShortcutBadger.isBadgeCounterSupported(VerificationConnexionActivity.this)) {
                        // Edit the saved preferences
                        editor.putInt("badgeCount", badgeCount);
                        editor.commit();
                        ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
                    }
                }
                onResume();
            }
        });


        channel = VerificationConnexionActivity.this.getIntent().getExtras().getString("channel");

        channels.add(channel);

        Integer utilisateurID = VerificationConnexionActivity.this.getIntent().getExtras().getInt("utilisateurConnecteID");
        utilisateurConnecte = gestionnaireUtilisateur.getUtilisateurByID(db, utilisateurID);
        identiteUtilisateur.setText("Vous souhaitez-vous connecter en tant que : \n" + utilisateurConnecte.getNom() + " " + utilisateurConnecte.getPrenom());

        message.setText("Vous avez opté pour l'authentification forte. \n Pour poursuivre la connexion, veuillez saisir le code reçu (par notification ou par mail) dans la zone prévue ci-dessous. \n");

        // Access the default SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VerificationConnexionActivity.this);

        // Récupération des identifiants
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String pubnubSubKey = preferences.getString(getString(R.string.pubnubSubscribeKey), "sub-c-76c656ba-6c93-11e7-85aa-0619f8945a4f");

        // Initialisation de l'objet pubnub
        //PNConfiguration pnConfiguration = new PNConfiguration().setSubscribeKey(pubnubSubKey).setSecure(true);
        PNConfiguration pnConfiguration = null;

        PubNub pubnub = new PubNub() {
            @NonNull
            @Override
            public WhereNow whereNow() {
                return null;
            }

            @NonNull
            @Override
            public UserMetadata userMetadata(@NonNull String s) {
                return null;
            }

            @NonNull
            @Override
            public UnsubscribeBuilder unsubscribe() {
                return null;
            }

            @NonNull
            @Override
            public Time time() {
                return null;
            }

            @NonNull
            @Override
            public SubscriptionSet subscriptionSetOf(@NonNull Set<? extends Subscription> set) {
                return null;
            }

            @NonNull
            @Override
            public SubscribeBuilder subscribe() {
                return null;
            }

            @NonNull
            @Override
            public SignalBuilder signal(@NonNull Object o, @NonNull String s) {
                return null;
            }

            @NonNull
            @Override
            public Signal signal() {
                return null;
            }

            @NonNull
            @Override
            public SetUUIDMetadata setUUIDMetadata() {
                return null;
            }

            @NonNull
            @Override
            public SetState setPresenceState() {
                return null;
            }

            @NonNull
            @Override
            public SetMemberships.Builder setMemberships() {
                return null;
            }

            @NonNull
            @Override
            public SetChannelMetadata.Builder setChannelMetadata() {
                return null;
            }

            @NonNull
            @Override
            public SetChannelMembers.Builder setChannelMembers() {
                return null;
            }

            @NonNull
            @Override
            public SendFile.Builder sendFile() {
                return null;
            }

            @NonNull
            @Override
            public RevokeToken revokeToken() {
                return null;
            }

            @NonNull
            @Override
            public RemoveUUIDMetadata removeUUIDMetadata() {
                return null;
            }

            @NonNull
            @Override
            public RemoveChannelsFromPush removePushNotificationsFromChannels() {
                return null;
            }

            @NonNull
            @Override
            public RemoveMessageAction removeMessageAction() {
                return null;
            }

            @NonNull
            @Override
            public RemoveMemberships.Builder removeMemberships() {
                return null;
            }

            @NonNull
            @Override
            public RemoveChannelChannelGroup removeChannelsFromChannelGroup() {
                return null;
            }

            @NonNull
            @Override
            public RemoveChannelMetadata.Builder removeChannelMetadata() {
                return null;
            }

            @NonNull
            @Override
            public RemoveChannelMembers.Builder removeChannelMembers() {
                return null;
            }

            @NonNull
            @Override
            public RemoveAllPushChannelsForDevice removeAllPushNotificationsFromDeviceWithPushToken() {
                return null;
            }

            @Override
            public void reconnect() {

            }

            @NonNull
            @Override
            public PublishFileMessage.Builder publishFileMessage() {
                return null;
            }

            @NonNull
            @Override
            public PublishBuilder publish(@NonNull Object o, @NonNull String s) {
                return null;
            }

            @NonNull
            @Override
            public Publish publish() {
                return null;
            }

            @NonNull
            @Override
            public PresenceBuilder presence() {
                return null;
            }

            @NonNull
            @Override
            public MessageCounts messageCounts() {
                return null;
            }

            @NonNull
            @Override
            public ManageMemberships.Builder manageMemberships() {
                return null;
            }

            @NonNull
            @Override
            public ManageChannelMembers.Builder manageChannelMembers() {
                return null;
            }

            @NonNull
            @Override
            public ListFiles.Builder listFiles() {
                return null;
            }

            @NonNull
            @Override
            public AllChannelsChannelGroup listChannelsForChannelGroup() {
                return null;
            }

            @NonNull
            @Override
            public ListAllChannelGroup listAllChannelGroups() {
                return null;
            }

            @NonNull
            @Override
            public History history() {
                return null;
            }

            @NonNull
            @Override
            public HereNow hereNow() {
                return null;
            }

            @NonNull
            @Override
            public GrantTokenBuilder grantToken(int i) {
                return null;
            }

            @NonNull
            @Override
            public GrantTokenBuilder grantToken() {
                return null;
            }

            @NonNull
            @Override
            public Grant grant() {
                return null;
            }

            @NonNull
            @Override
            public GetUUIDMetadata getUUIDMetadata() {
                return null;
            }

            @NonNull
            @Override
            public List<String> getSubscribedChannels() {
                return null;
            }

            @NonNull
            @Override
            public List<String> getSubscribedChannelGroups() {
                return null;
            }

            @NonNull
            @Override
            public GetState getPresenceState() {
                return null;
            }

            @NonNull
            @Override
            public GetMessageActions getMessageActions() {
                return null;
            }

            @NonNull
            @Override
            public GetMemberships getMemberships() {
                return null;
            }

            @NonNull
            @Override
            public GetFileUrl.Builder getFileUrl() {
                return null;
            }

            @NonNull
            @Override
            public GetChannelMetadata.Builder getChannelMetadata() {
                return null;
            }

            @NonNull
            @Override
            public GetChannelMembers.Builder getChannelMembers() {
                return null;
            }

            @NonNull
            @Override
            public GetAllUUIDMetadata getAllUUIDMetadata() {
                return null;
            }

            @NonNull
            @Override
            public GetAllChannelsMetadata getAllChannelsMetadata() {
                return null;
            }

            @NonNull
            @Override
            public PublishBuilder fire(@NonNull Object o, @NonNull String s) {
                return null;
            }

            @NonNull
            @Override
            public Publish fire() {
                return null;
            }

            @NonNull
            @Override
            public FetchMessages fetchMessages() {
                return null;
            }

            @NonNull
            @Override
            public InputStream encryptInputStream(@NonNull InputStream inputStream) throws PubNubException {
                return null;
            }

            @NonNull
            @Override
            public String encrypt(@NonNull String s) throws PubNubException {
                return null;
            }

            @NonNull
            @Override
            public DownloadFile.Builder downloadFile() {
                return null;
            }

            @NonNull
            @Override
            public DeleteMessages deleteMessages() {
                return null;
            }

            @NonNull
            @Override
            public DeleteFile.Builder deleteFile() {
                return null;
            }

            @NonNull
            @Override
            public DeleteChannelGroup deleteChannelGroup() {
                return null;
            }

            @NonNull
            @Override
            public InputStream decryptInputStream(@NonNull InputStream inputStream) throws PubNubException {
                return null;
            }

            @NonNull
            @Override
            public ChannelMetadata channelMetadata(@NonNull String s) {
                return null;
            }

            @NonNull
            @Override
            public ChannelGroup channelGroup(@NonNull String s) {
                return null;
            }

            @NonNull
            @Override
            public Channel channel(@NonNull String s) {
                return null;
            }

            @NonNull
            @Override
            public ListPushProvisions auditPushChannelProvisions() {
                return null;
            }

            @NonNull
            @Override
            public AddChannelsToPush addPushNotificationsOnChannels() {
                return null;
            }

            @NonNull
            @Override
            public AddMessageAction addMessageAction() {
                return null;
            }

            @Override
            public void addListener(@NonNull SubscribeCallback subscribeCallback) {

            }

            @NonNull
            @Override
            public AddChannelChannelGroup addChannelsToChannelGroup() {
                return null;
            }

            @NonNull
            @Override
            public BasePNConfiguration getConfiguration() {
                return null;
            }

            @Override
            public void addListener(@NonNull StatusListener statusListener) {

            }

            @Override
            public void removeListener(@NonNull Listener listener) {

            }

            @Override
            public void removeAllListeners() {

            }

            @Override
            public void addListener(@NonNull EventListener eventListener) {

            }

            @Override
            public void unsubscribeAll() {

            }

            @NonNull
            @Override
            public SubscriptionSet subscriptionSetOf(@NonNull Set<String> set, @NonNull Set<String> set1, @NonNull SubscriptionOptions subscriptionOptions) {
                return null;
            }

            @Override
            public void setToken(@Nullable String s) {

            }

            @Override
            public void reconnect(long l) {

            }

            @NonNull
            @Override
            public PNToken parseToken(@NonNull String s) throws PubNubException {
                return null;
            }

            @Override
            public void forceDestroy() {

            }

            @NonNull
            @Override
            public InputStream encryptInputStream(@NonNull InputStream inputStream, @Nullable String s) throws PubNubException {
                return null;
            }

            @NonNull
            @Override
            public String encrypt(@NonNull String s, @Nullable String s1) throws PubNubException {
                return null;
            }

            @Override
            public void disconnect() {

            }

            @Override
            public void destroy() {

            }

            @NonNull
            @Override
            public InputStream decryptInputStream(@NonNull InputStream inputStream, @Nullable String s) throws PubNubException {
                return null;
            }

            @NonNull
            @Override
            public String decrypt(@NonNull String s, @Nullable String s1) throws PubNubException {
                return null;
            }

            @NonNull
            @Override
            public String decrypt(@NonNull String s) throws PubNubException {
                return null;
            }

            @NonNull
            @Override
            public String getVersion() {
                return null;
            }

            @Override
            public int getTimestamp() {
                return 0;
            }

            @NonNull
            @Override
            public String getBaseUrl() {
                return null;
            }
        };

        pubnub.subscribe().channels(channels).execute();


    }

    @Override
    public void onResume() {
        super.onResume();

        /* Code nécessaire afin de réaliser une requête à l' API */
        JSONObject body = new JSONObject();

        try {
            body.put("channel", channel);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueueNotification = Volley.newRequestQueue(VerificationConnexionActivity.this);
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceNotification + "/verification";

        // Takes the response from the JSON request

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, body, new Response.Listener<JSONObject>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int resultCount = response.getInt("resultCount");
                    if (resultCount == 0) {
                        Alerte.afficherAlerte(VerificationConnexionActivity.this, "Erreur", "Code de vérification non envoyé !", "alerte");
                    } else {
                        code_verification = response.getString("codeVerification");
                        String email = utilisateurConnecte.getMail();
                        if(utilisateurConnecte.getIdentifiant().contentEquals("ALCYONS"))
                        {
                            email = "dev01@alcyons.fr";
                        }
                        if (email != null) {
                            new SendEmailTask().execute(email);
                        }
                        ;

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
                        Alerte.afficherAlerte(VerificationConnexionActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP envoyer notification", "alerte");
                        VerificationConnexionActivity.this.finish();
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

        obreq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 3000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 3000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueueNotification.add(obreq);

        invalidateOptionsMenu();

    }

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(VerificationConnexionActivity.this, email[0], true, db);
            try {
                // Envoi du mail de vérification
                sender.sendMailVerification("Verification", "Voici le code de vérification : " + code_verification);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }

}
