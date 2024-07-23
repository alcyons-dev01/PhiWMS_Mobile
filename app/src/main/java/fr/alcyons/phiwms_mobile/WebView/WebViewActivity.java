package fr.alcyons.phiwms_mobile.WebView;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

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

    }

}
