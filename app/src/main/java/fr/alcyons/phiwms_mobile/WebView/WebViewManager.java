package fr.alcyons.phiwms_mobile.WebView;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewManager {

    private static Boolean isWebViewReady;
    private static WebViewManager instance;
    private WebView offscreenWebView;
    private Context context;

    private WebViewManager(Context context) {
        // Utilisez un contexte d'application pour éviter les fuites de mémoire
        this.context = context;
        isWebViewReady = false;
        offscreenWebView = new WebView(this.context);
        offscreenWebView.setWebViewClient(new MyWebViewClient());
        offscreenWebView.setVisibility(View.GONE);
        offscreenWebView.getSettings().setJavaScriptEnabled(true);
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String ipServ = sharedPreferences.getString("ipServeur", "");
        offscreenWebView.loadUrl("http://10.0.2.2:8000" /*+ ipServ*/);
    }

    public void authentification(String username, String password) {
        offscreenWebView.evaluateJavascript("$('#inputIdentifiant').val('" + username + "');", null);
        offscreenWebView.evaluateJavascript("$('#inputPassword').val('" + password + "');", null);
        offscreenWebView.evaluateJavascript("$('#bouton_connexion').click();", null);
    }

    public WebView getOffscreenWebView() {
        return offscreenWebView;
    }

    public static void destroy(){
        instance = null;
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
