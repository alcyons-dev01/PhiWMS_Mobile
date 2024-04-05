package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.MenuActivity;
import com.example.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 03/05/2017.
 */

public class Depot_EmplacementAdapter extends ArrayAdapter implements Filterable {

    public List<Depot_Emplacement> depotEmplacementList;
    public List<Depot_Emplacement> depotEmplacementDeBaseList;
    public List<Depot_EmplacementViewHolder> viewHolderList;
    public HashMap<Integer, Boolean> depotEmplacementSelectedHasMap = new HashMap<>();
    Context context;
    SQLiteDatabase db;
    Depot_EmplacementFilter filter;

    public Depot_EmplacementAdapter(Context context, SQLiteDatabase db, List<Depot_Emplacement> depotEmplacementList) {
        super(context, 0, depotEmplacementList);
        this.context = context;
        this.db = db;

        this.depotEmplacementList = depotEmplacementList;

        // Permet de garder une version de la liste complète des éléments
        this.depotEmplacementDeBaseList = new ArrayList<>();
        this.viewHolderList = new ArrayList<>();

        // La boucle foreach permet d'insérer les valeurs d'un objet dans l'autre, "emplacementDeBase = emplacements" aurait "fusionné" les deux objets et modifier l'un des deux aurait modifié l'autre
        for (Depot_Emplacement emplacementCourant : depotEmplacementList) {
            this.depotEmplacementDeBaseList.add(emplacementCourant);
            this.viewHolderList.add(new Depot_EmplacementViewHolder());
        }
        this.filter = null;
    }


    @Override
    public Depot_EmplacementFilter getFilter() {
        if (filter == null)
            filter = new Depot_EmplacementFilter();
        return filter;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_depot_emplacement, parent, false);
        }

        Depot_EmplacementViewHolder viewHolder = (Depot_EmplacementViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = viewHolderList.get(position);
            viewHolder.nomEmplacement = (TextView) convertView.findViewById(R.id.nomEmplacement);
            viewHolder.dataMatrixEmplacement = (TextView) convertView.findViewById(R.id.dataMatrixEmplacement);
            convertView.setTag(viewHolder);
        }

        Depot_Emplacement emplacementCourant = (Depot_Emplacement) getItem(position);

        viewHolder.nomEmplacement.setText(emplacementCourant.getAdressage());
        viewHolder.dataMatrixEmplacement.setText(emplacementCourant.getCode_GLN());

        return convertView;
    }

    public void setNewSelection(int position, boolean value) {
        depotEmplacementSelectedHasMap.put(position, value);
    }

    public boolean isPositionChecked(int position) {
        Boolean result = depotEmplacementSelectedHasMap.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return depotEmplacementSelectedHasMap.keySet();
    }

    public void removeSelection(int position) {
        depotEmplacementSelectedHasMap.remove(position);
    }

    public Integer remove() {
        int nbDepotEmplacementSupprime = 0;

        for (HashMap.Entry<Integer, Boolean> entry : depotEmplacementSelectedHasMap.entrySet()) {
            int position = entry.getKey();
            boolean checked = entry.getValue();
            if (checked) {
                nbDepotEmplacementSupprime++;
                Depot_Emplacement depotEmplacement = depotEmplacementDeBaseList.get(position);
                depotEmplacementList.remove(depotEmplacement);
            }
        }
        clearSelection();
        return nbDepotEmplacementSupprime;
    }

    public void clearSelection() {
        depotEmplacementSelectedHasMap = new HashMap<>();
    }

    private class Depot_EmplacementFilter extends android.widget.Filter {
        FilterResults result;

        @Override
        protected FilterResults performFiltering(final CharSequence constraint) {

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message mesg) {
                    throw new RuntimeException();
                }
            };
            ((MenuActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    depotEmplacementList.clear();
                    for (Depot_Emplacement emplacementCourant : depotEmplacementDeBaseList
                            ) {
                        depotEmplacementList.add(emplacementCourant);
                    }
                    //constraint = constraint.toString().toLowerCase();
                    String contrainte = constraint.toString().toLowerCase();
                    result = new FilterResults();

                    if (contrainte != null && contrainte.toString().length() > 0) {
                        List<Depot_Emplacement> founded = new ArrayList<Depot_Emplacement>();
                        for (Depot_Emplacement item : depotEmplacementList) {
                            // Vérifie le début du premier mot

                            // Valeur à modifier si on souhaite filtrer sur une autre valeur que le toString() de l'item
                            String valueText = item.toString().toLowerCase();

                            if (valueText.startsWith(String.valueOf(contrainte))) {
                                founded.add(item);
                            } else {
                                // Vérifie le début de chaque mot
                                final String[] words = valueText.split(" ");
                                for (String word : words) {
                                    if (word.startsWith(String.valueOf(contrainte))) {
                                        founded.add(item);
                                        break;
                                    }
                                }
                            }
                        }
                        result.values = founded;
                        result.count = founded.size();
                    } else {
                        // Si le texte à filtrer (constraint) est vide, on remet les emplacements de base
                        result.values = depotEmplacementDeBaseList;
                        result.count = depotEmplacementDeBaseList.size();
                    }
                    handler.sendMessage(handler.obtainMessage());
                }
            });
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.values != null) {
                depotEmplacementList.clear();
                for (Depot_Emplacement item : (List<Depot_Emplacement>) results.values) {
                    add(item);
                }
                notifyDataSetChanged();
            }
        }
    }

    public class Depot_EmplacementViewHolder {
        public TextView nomEmplacement;
        public TextView dataMatrixEmplacement;
    }

}
