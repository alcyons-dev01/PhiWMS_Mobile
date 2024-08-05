package fr.alcyons.phiwms_mobile.WebView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;

import fr.alcyons.phiwms_mobile.AuthentificationTotp.AuthentificationTotpActivity;
import fr.alcyons.phiwms_mobile.AuthentificationV2Activity;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.R;

public class WebViewActivity extends MainActivity {

    private static FrameLayout webviewConteneur;
    private WebView vueActuelle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webviewConteneur = (FrameLayout) findViewById(R.id.webview_container);
        webviewConteneur.removeAllViews();
        vueActuelle = WebViewManager.getInstance(null).getOffscreenWebView();
        webviewConteneur.addView(vueActuelle);
        vueActuelle.setVisibility(View.VISIBLE);

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent backToAuth = new Intent(WebViewActivity.this, AuthentificationV2Activity.class);
                backToAuth.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                WebViewManager.getInstance(WebViewActivity.this).deconnexion();
                WebViewManager.destroy();
                startActivity(backToAuth);
                finish();
            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
        webviewConteneur.removeAllViews();
    }

}
