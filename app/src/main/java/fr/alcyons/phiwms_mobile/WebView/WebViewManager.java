package fr.alcyons.phiwms_mobile.WebView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.webkit.JavaScriptReplyProxy;
import androidx.webkit.WebMessageCompat;
import androidx.webkit.WebViewCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.alcyons.phiwms_mobile.AuthentificationTotp.AuthentificationTotpActivity;
import fr.alcyons.phiwms_mobile.AuthentificationV2Activity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;

public class WebViewManager {

    private List<Runnable> uponLogin;
    private List<Runnable> uponLogout;
    private List<Runnable> uponLogFailed;
    private Runnable passwordRecovered;
    private static Boolean isWebViewReady;
    private static WebViewManager instance;
    private WebViewCompat.WebMessageListener myListener;
    private WebView offscreenWebView;
    private Context context;

    private WebViewManager(Context context) {
        // Utilisez un contexte d'application pour éviter les fuites de mémoire
        this.context = context;
        uponLogin = new ArrayList<Runnable>();
        uponLogout = new ArrayList<Runnable>();
        uponLogFailed = new ArrayList<Runnable>();
        isWebViewReady = false;
        offscreenWebView = new WebView(this.context);
        offscreenWebView.setWebViewClient(new MyWebViewClient());
        offscreenWebView.setVisibility(View.GONE);
        offscreenWebView.getSettings().setJavaScriptEnabled(true);
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String ipServ = sharedPreferences.getString("ipServeur", "");
        offscreenWebView.loadUrl("http://" + ipServ);

        Set<String> allowedOrigins = new HashSet<>();
        allowedOrigins.add("http://10.0.2.2:8000");
        allowedOrigins.add("http://phiwms.alcyons.fr");
        allowedOrigins.add("http://" + ipServ);
        myListener = new WebViewCompat.WebMessageListener() {
            @Override
            public void onPostMessage(WebView view, WebMessageCompat message, Uri sourceOrigin,
                                      boolean isMainFrame, JavaScriptReplyProxy replyProxy) {
                Log.d("test", message.getData().toString());
                if (message.getData().equals("userIsLoggedOut")){
                    for (Runnable fonction: uponLogout)
                    {
                        fonction.run();
                    }
                } else if (message.getData().equals("userLoginFailed")){
                    for (Runnable fonction: uponLogFailed)
                    {
                        fonction.run();
                    }
                } else if (message.getData().equals("userIsLogged")) {
                    //Log.d("test", "test");
                    for (Runnable fonction: uponLogin)
                    {
                        fonction.run();
                    }
                } else if (message.getData().equals("userRecoveryPwd")){
                    passwordRecovered.run();
                }

            }
        };

        WebViewCompat.addWebMessageListener(offscreenWebView, "androidMessageHandler", allowedOrigins, myListener);
    }

    public void deconnexion(){
        offscreenWebView.evaluateJavascript("$('#modale_demande_confirmation').find('#modal-validate').click();", null);
        offscreenWebView.setVisibility(View.GONE);
    }

    public void authentification(String username, String password) {
        offscreenWebView.evaluateJavascript("$('#inputIdentifiant').val('" + username + "');", null);
        offscreenWebView.evaluateJavascript("$('#inputPassword').val('" + password + "');", null);
        offscreenWebView.evaluateJavascript("$('#bouton_connexion').click();", null);
    }

    public WebView getOffscreenWebView() {
        return offscreenWebView;
    }

    public void addUponLogin(Runnable toRun){
        uponLogin.add(toRun);
    }

    public void removeUponLogin(Runnable toRun){
        uponLogin.remove(toRun);
    }

    public void addUponLogout(Runnable toRun){
        uponLogout.add(toRun);
    }

    public void removeUponLogout(Runnable toRun){
        uponLogout.remove(toRun);
    }

    public void addUponLogFailed(Runnable toRun){
        uponLogFailed.add(toRun);
    }

    public void setPasswordRecovered(Runnable passwordRecovered){
        this.passwordRecovered = passwordRecovered;
    }

    public void removeUponLogFailed(Runnable toRun){
        uponLogFailed.remove(toRun);
    }

    public static void destroy(){
        if (instance != null){
            instance.getOffscreenWebView().destroy();
            instance = null;
        }
    }

    public static synchronized WebViewManager getInstance(Context context) {
        if (instance == null) {
            instance = new WebViewManager(context);
        }
        return instance;
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true; // Ne pas laisser le système gérer le lien
        }
    }
}
