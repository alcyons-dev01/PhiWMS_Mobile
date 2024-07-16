package fr.alcyons.phiwms_mobile.WebView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.alcyons.phiwms_mobile.AuthentificationV2Activity;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.R;

public class WebViewActivity extends MainActivity {

    private WebView vueActuelle;
    static FrameLayout webviewConteneur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webviewConteneur = (FrameLayout) findViewById(R.id.webview_container);
        webviewConteneur.removeAllViews();
        vueActuelle = WebViewManager.getInstance(null).getOffscreenWebView();
        webviewConteneur.addView(vueActuelle);
        Button boutonValider = findViewById(R.id.boutonEnvoi);
        boutonValider.setOnClickListener(v -> {
            vueActuelle.setVisibility(WebView.VISIBLE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
