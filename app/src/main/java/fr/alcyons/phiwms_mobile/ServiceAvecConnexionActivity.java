package fr.alcyons.phiwms_mobile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

import java.util.Objects;

import fr.alcyons.phiwms_mobile.R;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;

public class ServiceAvecConnexionActivity extends ServiceActivity {

    /*
    * Permet de ne faire de requête que si l'on passe par la fonction onCreate
    * /!\ Doit être passé à false dans l'activité fille après la requête /!\
    */
    public boolean passageParOnCreate = false;
    public SwipeRefreshLayout swipeRefreshLayout;

    public AlertDialog alertDialog;
    public Handler handler;
    public RetryPolicy retryPolicy;

    View.OnClickListener clicRefreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (statutConnexion) {
                Intent serviceAvecConnexionIntent = ServiceAvecConnexionActivity.this.getIntent();
                ServiceAvecConnexionActivity.this.finish();
                ServiceAvecConnexionActivity.this.startActivity(serviceAvecConnexionIntent);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passageParOnCreate = true;

        retryPolicy = new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        };

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message mesg) {
                throw new RuntimeException();
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void afficherSpinner(Context context, LayoutInflater inflater)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.progress_bar, null);
        builder.setView(layout);
        alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();
    }

    public void arreterSpinner()
    {
        if(alertDialog != null)
            alertDialog.dismiss();
    }

    public void connexionNecessaire() {
        super.setContentView(R.layout.activity_service_avec_connexion);
        findViewById(R.id.boutonRefresh).setOnClickListener(clicRefreshListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public void setContentView(int layoutRef) {
        super.setContentView(layoutRef);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    startActivity(getIntent());
                    finish();
                }
            });
        }
    }
}
