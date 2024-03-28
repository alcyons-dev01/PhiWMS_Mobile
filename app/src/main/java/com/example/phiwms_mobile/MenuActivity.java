package com.example.phiwms_mobile;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.SearchView;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.phiwms_mobile.Classes.Service;
import com.example.phiwms_mobile.Outils.OutilsGestionPhraseSnackbar;

public class MenuActivity extends OriginalActivity {

    public static List<String> serviceIndicateurNom = new ArrayList<>();
    public static List<Integer> serviceIndicateurValeur = new ArrayList<>();
    public static List<Service> serviceList = new ArrayList<>();
    public static String nomServiceVide = "";
    public static Boolean vide;
    int nbElementInAdapter = 0;
    TextView nbElementInAdapterTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recherche, menu);
        inflater.inflate(R.menu.menu_suppression, menu);

        menu.findItem(R.id.deleteMenu).setVisible(false);
        menu.findItem(R.id.rechercheMenu).setVisible(false);
        return true;
    }

    //Méthode qui se déclenchera au clic sur un item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //On regarde quel item a été cliqué grâce à son id et on déclenche une action
        if (item.getItemId() == R.id.deconnexion) {
            Intent intent = new Intent(MenuActivity.this, AuthentificationActivity.class);
            finishAffinity();
            MenuActivity.this.startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        prepareOptionsMenu(menu, null, null, "Rechercher...");
        return true;
    }

    public void prepareOptionsMenu(Menu menu, final ArrayAdapter adapter, MenuItem.OnMenuItemClickListener clickListener, String placeholder) {
        /* Si l'adapter vaut null c'est qu'on ne veut pas effectuer de recherche */
        if (adapter != null) {
            nbElementInAdapter = adapter.getCount();
            nbElementInAdapterTextView = ((TextView) findViewById(R.id.nbElementInAdapter));

            MenuItem searchMenuItem = menu.findItem(R.id.rechercheMenu);
            searchMenuItem.setVisible(true);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.getFilter().filter(query);
                    if (nbElementInAdapterTextView != null) {
                        nbElementInAdapterTextView.setText(String.valueOf(adapter.getCount()));
                    }

                    return false;
                }

                @Override
                public boolean onQueryTextChange(final String newText) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.getFilter().filter(newText);
                            if (nbElementInAdapterTextView != null) {
                                nbElementInAdapterTextView.setText(String.valueOf(adapter.getCount()));
                            }
                        }
                    });
                    return false;
                }
            });
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    searchView.setQuery("", true);
                    if (nbElementInAdapterTextView != null) {
                        nbElementInAdapterTextView.setText(String.valueOf(adapter.getCount()));
                    }
                    return false;
                }
            });



            MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    if (nbElementInAdapterTextView != null) {
                        nbElementInAdapterTextView.setText(String.valueOf(adapter.getCount()));
                    }
                    return true;  // Return true to collapse action view
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    if (nbElementInAdapterTextView != null) {
                        nbElementInAdapterTextView.setText(String.valueOf(adapter.getCount()));
                    }
                    return true;  // Return true to expand action view
                }
            });

            searchView.setQueryHint(placeholder);
        }

    }

    public void afficherSnackBar(String nomService) {
        String erreur = OutilsGestionPhraseSnackbar.obtenirPhraseSnackbar(nomService);
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>"+erreur+"</b>", 0), Snackbar.LENGTH_LONG);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = layout.findViewById(R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getChildAt(0).getLayoutParams();
        params.gravity = Gravity.FILL_HORIZONTAL | Gravity.BOTTOM;
        snackBarView.getChildAt(0).setLayoutParams(params);
        snackbar.show();
    }
}
