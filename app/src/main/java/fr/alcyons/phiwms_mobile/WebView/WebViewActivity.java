package fr.alcyons.phiwms_mobile.WebView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.alcyons.phiwms_mobile.AuthentificationV2Activity;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.R;

public class WebViewActivity extends MainActivity {

    static WebView maWebView;
    //static List<Method> attenteLoad;

    public static void authentification(String username, String password) {
        maWebView.evaluateJavascript("javascript: $(\"inputIdentifiant\").val(" + username + ");", null);
        maWebView.evaluateJavascript("$(\"#inputPassword\").val(" + password + ");", null);
        maWebView.evaluateJavascript("$(\"#bouton_connexion\").click();", null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //attenteLoad = new ArrayList<Method>();
        setContentView(R.layout.activity_webview);
        maWebView = (WebView) findViewById(R.id.webview);
        maWebView.setWebViewClient(new MyWebViewClient());
        maWebView.getSettings().setJavaScriptEnabled(true);
        maWebView.loadUrl("phiwms.alcyons.fr");
        Log.d("", "test");
        Intent auth = new Intent(WebViewActivity.this, AuthentificationV2Activity.class);
        startActivityIfNeeded(auth,0);
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true; // Ne pas laisser le système gérer le lien
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        /*for (Method courante : attenteLoad){
            courante.
        }*/
    }

}
