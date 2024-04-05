package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_ReceptionPUI_Adapte;
import com.example.phiwms_mobile.R;

/**
 * Created by olivier on 30/11/2017.
 */

public class ZoneEtEmplacement_ReceptionAdapter extends ArrayAdapter {

    public List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement> zoneEtEmplacementList;
    public List<ZoneEtEmplacement_ReceptionViewHolder> zoneEtEmplacementReceptionViewHolderList;
    Context context;
    SQLiteDatabase db;
    OnDataChangeListener mOnDataChangeListener;

    int qteReliquat;
    int quantiteLivree;

    public ZoneEtEmplacement_ReceptionAdapter(Context context, SQLiteDatabase db, List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement> zoneEtEmplacementList, int qteReliquat, int quantiteLivree) {

        super(context, 0, zoneEtEmplacementList);
        this.context = context;
        this.db = db;
        this.zoneEtEmplacementList = zoneEtEmplacementList;

        this.qteReliquat = qteReliquat;
        this.quantiteLivree = quantiteLivree;

        zoneEtEmplacementReceptionViewHolderList = new ArrayList<>();
        for (PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement : zoneEtEmplacementList
                ) {
            zoneEtEmplacementReceptionViewHolderList.add(new ZoneEtEmplacement_ReceptionViewHolder());
        }

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_zone_et_emplacement_recpetion_pui, parent, false);
        }

        ZoneEtEmplacement_ReceptionViewHolder viewHolder = (ZoneEtEmplacement_ReceptionViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = zoneEtEmplacementReceptionViewHolderList.get(position);
            viewHolder.zone = (TextView) convertView.findViewById(R.id.zone);
            viewHolder.emplacement = (TextView) convertView.findViewById(R.id.emplacement);
            viewHolder.qteSaisie = (EditText) convertView.findViewById(R.id.qteSaisie);
            viewHolder.ZoneEtEmplacement = (LinearLayout) convertView.findViewById(R.id.ZoneEtEmplacement);

        }

        viewHolder.ZoneEtEmplacement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZoneEtEmplacement_ReceptionViewHolder viewHolder = null;
                for (ZoneEtEmplacement_ReceptionViewHolder viewHolderCourant : zoneEtEmplacementReceptionViewHolderList
                        ) {
                    if (viewHolderCourant.ZoneEtEmplacement == v) {
                        viewHolder = viewHolderCourant;
                    }
                }

                if (viewHolder != null) {
                    PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = zoneEtEmplacementList.get(position);
                    int qteAvant = qteReliquat;
                    int qteAprès = qteReliquat;

                    viewHolder.qteSaisie.setText(String.valueOf(qteAprès));
                    zoneEtEmplacement.setQuantite(qteAprès);
                    mOnDataChangeListener.onDataChanged(qteAvant, qteAprès);
                    qteReliquat = 0;
                }
            }
        });

        PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = (PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement) getItem(position);


        if (zoneEtEmplacement != null) {
            GestionnaireEditText gestionnaireEditText = new GestionnaireEditText(viewHolder, zoneEtEmplacement);

            viewHolder.zone.setText(zoneEtEmplacement.getZoneName());
            viewHolder.emplacement.setText(zoneEtEmplacement.getEmplacementName());
            viewHolder.qteSaisie.setText(String.valueOf(zoneEtEmplacement.getQuantite()));
        }

        return convertView;
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }

    @Override
    public void clear() {
        zoneEtEmplacementList.clear();
    }


    public interface OnDataChangeListener {
        public void onDataChanged(int quantitéAvant, int quantitéAprès);
    }

    private class ZoneEtEmplacement_ReceptionViewHolder {
        public TextView zone;
        public TextView emplacement;
        public EditText qteSaisie;
        public LinearLayout ZoneEtEmplacement;

    }

    private class GestionnaireEditText {

        public ZoneEtEmplacement_ReceptionViewHolder viewHolder;
        public PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement;

        int qteAprès;
        int qteAvant;
        int result;
        int difference;
        boolean premierPassage = true;

        TextWatcher textWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (premierPassage) {
                    qteAvant = zoneEtEmplacement.getQuantite();
                    qteAprès = 0;
                    difference = 0;
                    // Récupération des valeurs saisies
                    if (!viewHolder.qteSaisie.getText().toString().equals("")) {
                        qteAprès = Integer.parseInt(viewHolder.qteSaisie.getText().toString());
                    }

                    difference = qteAprès - qteAvant;

                    quantiteLivree = quantiteLivree + difference;

                    result = quantiteLivree - qteReliquat;

                    if (result > 0) {
                        qteAprès = qteAprès - result;
                    } else {
                        result = 0;
                    }

                    quantiteLivree = quantiteLivree - result;

                    premierPassage = false;
                    if (qteAprès == 0) {
                        viewHolder.qteSaisie.setText("");
                        viewHolder.qteSaisie.setSelection(0);
                    } else {
                        viewHolder.qteSaisie.setText(String.valueOf(qteAprès));
                        viewHolder.qteSaisie.setSelection(String.valueOf(qteAprès).length());
                    }
                    premierPassage = true;
                    zoneEtEmplacement.setQuantite(qteAprès);
                    mOnDataChangeListener.onDataChanged(qteAvant, qteAprès);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        public GestionnaireEditText(ZoneEtEmplacement_ReceptionViewHolder view, PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement) {
            this.viewHolder = view;
            this.zoneEtEmplacement = zoneEtEmplacement;
            this.viewHolder.qteSaisie.addTextChangedListener(textWatcher);
        }
    }
}
