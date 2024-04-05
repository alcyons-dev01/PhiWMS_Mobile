package fr.alcyons.phiwms_mobile.Outils;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import fr.alcyons.phiwms_mobile.ListViewAdapters.Depot_EmplacementAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Depot_ZoneAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Emplacement_RetourPUIAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.NotificationAdapter;
import com.example.phiwms_mobile.R;

/**
 * Created by jessica on 15/12/2017.
 */

public class SimpleMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {
    int count = 0;
    android.view.ActionMode actionMode = null;
    Context context;

    NotificationAdapter notificationAdapter;
    TextView nbNotificationTextView;

    Emplacement_RetourPUIAdapter emplacementRetourPUIAdapter;

    Depot_ZoneAdapter depotZoneAdapter;
    TextView nbZoneTextView;

    Depot_EmplacementAdapter depotEmplacementAdapter;
    TextView nbEmplacementsTextView;

    FloatingActionButton boutonAjoutEmplacementPui;
    FloatingActionButton boutonSuppressionEmplacementPui;

    public SimpleMultiChoiceModeListener(Context context, TextView nbNotificationTextView, NotificationAdapter adapter) {
        this.context = context;
        this.nbNotificationTextView = nbNotificationTextView;
        this.notificationAdapter = adapter;
    }

    public SimpleMultiChoiceModeListener(Context context, Emplacement_RetourPUIAdapter adapter, FloatingActionButton boutonAjout, FloatingActionButton boutonSuppression) {
        this.context = context;
        this.emplacementRetourPUIAdapter = adapter;
        this.boutonAjoutEmplacementPui = boutonAjout;
        this.boutonSuppressionEmplacementPui = boutonSuppression;
    }

    public SimpleMultiChoiceModeListener(Context context, TextView nbZoneTextView, Depot_ZoneAdapter adapter) {
        this.context = context;
        this.nbZoneTextView = nbZoneTextView;
        this.depotZoneAdapter = adapter;
    }

    public SimpleMultiChoiceModeListener(Context context, TextView nbEmplacementsTextView, Depot_EmplacementAdapter adapter) {
        this.context = context;
        this.nbEmplacementsTextView = nbEmplacementsTextView;
        this.depotEmplacementAdapter = adapter;
    }

    public void closeActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
        if (notificationAdapter != null) {
            notificationAdapter.clearSelection();
        }
        if (emplacementRetourPUIAdapter != null) {
            emplacementRetourPUIAdapter.clearSelection();
            if(emplacementRetourPUIAdapter.qteRestanteARetourner != 0)
                this.boutonAjoutEmplacementPui.setVisibility(View.VISIBLE);
            this.boutonSuppressionEmplacementPui.setVisibility(View.GONE);
        }
        if (depotZoneAdapter != null) {
            depotZoneAdapter.clearSelection();
        }
        if (depotEmplacementAdapter != null) {
            depotEmplacementAdapter.clearSelection();
        }
    }

    @Override
    public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
        this.actionMode = mode;
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        if(emplacementRetourPUIAdapter == null)
            menu.findItem(R.id.menuDelete).setVisible(true);
        else
        {
            this.boutonAjoutEmplacementPui.setVisibility(View.GONE);
            this.boutonSuppressionEmplacementPui.setVisibility(View.VISIBLE);

            this.boutonSuppressionEmplacementPui.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emplacementRetourPUIAdapter.remove();
                    emplacementRetourPUIAdapter.notifyDataSetChanged();
                    closeActionMode();
                }
            });
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDelete:
                if (notificationAdapter != null) {
                    int nbNotificaionNonLuSupprimer = notificationAdapter.remove();
                    int nbNotificationNonLu = Integer.parseInt(nbNotificationTextView.getText().toString());
                    nbNotificationNonLu = nbNotificationNonLu - nbNotificaionNonLuSupprimer;
                    nbNotificationTextView.setText(String.valueOf(nbNotificationNonLu));
                    notificationAdapter.notifyDataSetChanged();
                }
                if (emplacementRetourPUIAdapter != null) {
                    emplacementRetourPUIAdapter.remove();
                    emplacementRetourPUIAdapter.notifyDataSetChanged();
                }
                if (depotZoneAdapter != null) {
                    int nbZoneSupprimer = depotZoneAdapter.remove();
                    int nbZone = Integer.parseInt(nbZoneTextView.getText().toString());
                    nbZone = nbZone - nbZoneSupprimer;
                    nbZoneTextView.setText(String.valueOf(nbZone));
                }
                if (depotEmplacementAdapter != null) {
                    int nbEmplacementSupprimer = depotEmplacementAdapter.remove();
                    int nbEmplacement = Integer.parseInt(nbEmplacementsTextView.getText().toString());
                    nbEmplacement = nbEmplacement - nbEmplacementSupprimer;
                    nbEmplacementsTextView.setText(String.valueOf(nbEmplacement));
                }
                break;
        }
        closeActionMode();
        return true;
    }

    @Override
    public void onDestroyActionMode(android.view.ActionMode mode) {
        count = 0;
        actionMode.getMenu().findItem(R.id.menuDelete).setVisible(false);
        actionMode = null;
        if(emplacementRetourPUIAdapter != null)
        {
            if(emplacementRetourPUIAdapter.qteRestanteARetourner != 0)
                this.boutonAjoutEmplacementPui.setVisibility(View.VISIBLE);
            this.boutonSuppressionEmplacementPui.setVisibility(View.GONE);
        }
        closeActionMode();
    }

    @Override
    public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            count++;
            if (notificationAdapter != null) {
                notificationAdapter.setNewSelection(position, checked);
            }
            if (emplacementRetourPUIAdapter != null) {
                emplacementRetourPUIAdapter.setNewSelection(position, checked);
            }
            if (depotZoneAdapter != null) {
                depotZoneAdapter.setNewSelection(position, checked);
            }
            if (depotEmplacementAdapter != null) {
                depotEmplacementAdapter.setNewSelection(position, checked);
            }
        } else {
            count--;
            if (notificationAdapter != null) {
                notificationAdapter.removeSelection(position);
            }
            if (emplacementRetourPUIAdapter != null) {
                emplacementRetourPUIAdapter.removeSelection(position);
            }
            if (depotZoneAdapter != null) {
                depotZoneAdapter.removeSelection(position);
            }
            if (depotEmplacementAdapter != null) {
                depotEmplacementAdapter.removeSelection(position);
            }
        }
        mode.setTitle(String.valueOf(count));
    }
}