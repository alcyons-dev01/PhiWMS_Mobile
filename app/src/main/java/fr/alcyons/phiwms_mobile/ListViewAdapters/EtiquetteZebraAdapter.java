package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.R;

public class EtiquetteZebraAdapter extends BaseAdapter {
    private final Context context;
    private final List<PH_Reliquat> items;
    private final Set<Long> selectionIds = new HashSet<>(); // ids sélectionnés
    private final List<PH_Reliquat> reliquatAImprimer;      // liste cible à tenir à jour

    public EtiquetteZebraAdapter(Context context, List<PH_Reliquat> items, List<PH_Reliquat> reliquatAImprimer) {
        this.context = context;
        this.items = items;
        this.reliquatAImprimer = reliquatAImprimer;
    }

    @Override public int getCount() { return items.size(); }
    @Override public PH_Reliquat getItem(int position) { return items.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(16, 16, 16, 16);
            layout.setGravity(Gravity.CENTER_VERTICAL);

            TextView tv = new TextView(context);
            tv.setTextSize(16);
            LinearLayout.LayoutParams lpTv = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f // prend tout l’espace restant
            );
            tv.setLayoutParams(lpTv);

            // --- CheckBox (taille auto, collée à droite) ---
            CheckBox cb = new CheckBox(context);
            LinearLayout.LayoutParams lpCb = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int color = ContextCompat.getColor(context, R.color.bleu_clair_alcyons);
            CompoundButtonCompat.setButtonTintList(cb, ColorStateList.valueOf(color));
            lpCb.gravity = Gravity.END | Gravity.CENTER_VERTICAL; // droite + centré verticalement
            cb.setLayoutParams(lpCb);

            // Ajout au layout
            layout.addView(tv);
            layout.addView(cb);

            h = new ViewHolder();
            h.textView = tv;
            h.checkBox = cb;
            convertView = layout;
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        PH_Reliquat item = getItem(position);
        long idStable = item.getReliquat_UID(); // ← assure-toi d’avoir un identifiant stable (long). Sinon, crée-en un.

        // éviter le callback lors du setChecked
        h.checkBox.setOnCheckedChangeListener(null);

        String textDesignation = item.getDesignationCourte();

        if(item.getDesignationCourte().length() > 40)
        {
            textDesignation = item.getDesignationCourte().substring(0, 20)+"..."+(item.getDesignationCourte().substring(item.getDesignationCourte().length()-20));
        }

        h.textView.setText(textDesignation+"\nLot n°"+item.getLot());
        h.textView.setTextColor(Color.BLACK);
        h.checkBox.setChecked(selectionIds.contains(idStable));

        // toggle via clic sur la ligne entière
        convertView.setOnClickListener(v -> h.checkBox.performClick());

        // maj sélection + liste externe
        h.checkBox.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) {
                if (selectionIds.add(idStable)) {
                    if (!reliquatAImprimer.contains(item)) reliquatAImprimer.add(item);
                }
            } else {
                if (selectionIds.remove(idStable)) {
                    reliquatAImprimer.remove(item);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
        CheckBox checkBox;
    }
}
