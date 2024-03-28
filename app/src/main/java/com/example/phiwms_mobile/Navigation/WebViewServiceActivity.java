package com.example.phiwms_mobile.Navigation;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.phiwms_mobile.Classes.Service;
import com.example.phiwms_mobile.R;
import com.example.phiwms_mobile.ServiceActivity;

public class WebViewServiceActivity extends ServiceActivity {

    Service serviceCourant;
    TextView descriptionService;
    LinearLayout textEnCoursDeCreation;
    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_service);

        //récupération du service courant
        serviceCourant = (Service) getIntent().getExtras().getSerializable("Service_Courant");

        //récupération graphique
        myWebView = (WebView) findViewById(R.id.webview);
        descriptionService = (TextView) findViewById(R.id.descriptionService);
        textEnCoursDeCreation = (LinearLayout) findViewById(R.id.textEnCoursDeCreation);

        perimetreCourantTextView.setText(serviceCourant.getNom());


        if(serviceCourant.getWhitePaper() == null || serviceCourant.getWhitePaper().contentEquals(""))
        {
            descriptionService.setText(serviceCourant.getDescription());
        }
        else
        {
            descriptionService.setText(serviceCourant.getWhitePaper());
        }

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {

                webView.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if(serviceCourant.getLien_video().contentEquals(""))
        {
            myWebView.setVisibility(View.GONE);
            textEnCoursDeCreation.setVisibility(View.VISIBLE);
        }
        else
        {
            String vimeoVideo = "<html><body><iframe width=\"100%\" height=\"100%\" src=\""+serviceCourant.getLien_video()+"\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
            myWebView.loadData(vimeoVideo,  "text/html", "utf-8");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_retour, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem valider = menu.findItem(R.id.retourMenu);
        valider.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                WebViewServiceActivity.this.finish();
                return true;
            }
        });

        return true;
    }




}
