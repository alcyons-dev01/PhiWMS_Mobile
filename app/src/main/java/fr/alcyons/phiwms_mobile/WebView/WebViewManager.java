package fr.alcyons.phiwms_mobile.WebView;

import android.content.Context;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

public class WebViewManager {

    private static Boolean isWebViewReady;
    private static WebViewManager instance;
    private WebView offscreenWebView;
    private Context context;

    private WebViewManager(Context context) {
        // Utilisez un contexte d'application pour éviter les fuites de mémoire
        this.context = context.getApplicationContext();
        isWebViewReady = false;
        offscreenWebView = new WebView(this.context);
        offscreenWebView.setWebViewClient(new MyWebViewClient());
        offscreenWebView.setVisibility(View.GONE);
        offscreenWebView.getSettings().setJavaScriptEnabled(true);
        offscreenWebView.loadUrl("phiwms.alcyons.fr");
    }

    public void authentification(String username, String password) {
        offscreenWebView.evaluateJavascript("$('#inputIdentifiant').val('" + username + "');", null);
        offscreenWebView.evaluateJavascript("$('#inputPassword').val('" + password + "');", null);
        offscreenWebView.evaluateJavascript("$('#bouton_connexion').click();", null);
    }

    public static synchronized WebViewManager getInstance(Context context) {
        if (instance == null) {
            instance = new WebViewManager(context);
        }
        return instance;
    }

    public WebView getOffscreenWebView() {
        return offscreenWebView;
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true; // Ne pas laisser le système gérer le lien
        }
    }
}
