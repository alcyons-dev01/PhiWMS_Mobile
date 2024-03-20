package fr.alcyons.phimr4.ActionUtilisateur;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.github.clans.fab.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.Service;
import fr.alcyons.phimr4.ListViewAdapters.ActionUtilisateurAdapter;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class ServiceActionUtilisateurActivity extends ServiceActivity {

    public boolean passageParOnCreate = false;
    public SwipeRefreshLayout swipeRefreshLayout;
    public ProgressDialog mProgressDialog;
    public Handler handler;
    public RetryPolicy retryPolicy;
    List<String> channels = new ArrayList<>();
    ListView actionListView;
    ActionUtilisateurAdapter actionUtilisateurAdapter;
    FloatingActionButton versPhoto;
    List<String> listeDate;

    LinearLayout contenuPage;
    LinearLayout layout_aucune_action;

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_utilisateur);
        contenuPage = ((LinearLayout) findViewById(R.id.contenuPage));
        layout_aucune_action = ((LinearLayout) findViewById(R.id.layout_aucune_action));
        actionListView = (ListView) findViewById(R.id.listeView);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //récupération de la liste pour afficher les actions

        actionListView.setDivider(footer);
        List<ActionUtilisateur> actionUtilisateurList = new ArrayList<>();
        actionUtilisateurList = ActionUtilisateurOpenHelper.getAllAction(db);

        if (actionUtilisateurList.size() != 0) {
            //tri du tableau
            Collections.sort(actionUtilisateurList, new Comparator<ActionUtilisateur>() {
                DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                @Override
                public int compare(ActionUtilisateur lhs, ActionUtilisateur rhs) {
                    try {
                        return f.parse(rhs.getDate()).compareTo(f.parse(lhs.getDate()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });

            actionUtilisateurAdapter = new ActionUtilisateurAdapter(ServiceActionUtilisateurActivity.this, db);
            listeDate = new ArrayList<>();
            for (ActionUtilisateur action_courante : actionUtilisateurList) {
                Service service_courant = ServiceOpenHelper.getServiceByID(db, action_courante.getServiceId());
                if(service_courant != null)
                {
                    DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                    DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();

                    try {
                        date = dateFormat1.parse(action_courante.getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String date_courante = dateFormat2.format(date);
                    if (listeDate.indexOf(date_courante) == -1) {
                        listeDate.add(date_courante);
                        actionUtilisateurAdapter.addSectionHeaderItem(action_courante);
                    }

                    actionUtilisateurAdapter.addItem(action_courante);
                }

                actionUtilisateurAdapter.setListActionUtilisateur(actionUtilisateurList);
                actionListView.setDivider(footer);
                actionListView.setAdapter(actionUtilisateurAdapter);
            }

            actionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (actionUtilisateurAdapter.sectionHeader.contains(position) == false) {
                        ActionUtilisateur actionSelectionne = (ActionUtilisateur) actionUtilisateurAdapter.getItem(position);

                        Intent versDetailsIntent = new Intent(ServiceActionUtilisateurActivity.this, DetailsActionActivity.class);
                        Bundle versDetailBundle = new Bundle();
                        versDetailBundle.putInt("actionId", actionSelectionne.getId());
                        versDetailBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        versDetailsIntent.putExtras(versDetailBundle);
                        ServiceActionUtilisateurActivity.this.startActivity(versDetailsIntent);
                    }
                }
            });
        }
        else
        {
            actionListView.setVisibility(View.GONE);
            layout_aucune_action.setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.boutonRetourBureau)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
