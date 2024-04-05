package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import com.example.phiwms_mobile.R;

/**
 * Created by olivier on 09/05/2019.
 */

public class SerieLotReceptionScanneeAdapter extends ArrayAdapter {

    private List<String> ListeGtinScan = new ArrayList<>();
    private List<StringViewHolder> stringViewHolders = new ArrayList<>();
    SQLiteDatabase db;
    private List<String> listGtin;
    private List<String> GtinProduitScan;

    public SerieLotReceptionScanneeAdapter(Context context, List<String> ListeGtinScan, SQLiteDatabase db, List<String> GTINProduitScan) {
        super(context, 0, GTINProduitScan);

        this.GtinProduitScan = GTINProduitScan;
        this.ListeGtinScan = ListeGtinScan;
        this.db = db;
        this.listGtin = new ArrayList<>();

        for (String scan : ListeGtinScan) {
            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(scan);
            if(listGtin.indexOf(gs1Decoupe.get(OutilsDecodage.codeGtin)) == -1)
            {
                stringViewHolders.add(new StringViewHolder());
            }
            listGtin.add(gs1Decoupe.get(OutilsDecodage.codeGtin));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_serie_lot_reception_scannee, parent, false);
        }

        StringViewHolder viewHolder = (StringViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new StringViewHolder();
            viewHolder.designation = (TextView) convertView.findViewById(R.id.designation);
            viewHolder.quantite = (TextView) convertView.findViewById(R.id.quantite);
            convertView.setTag(viewHolder);
        }
        String code_courant = GtinProduitScan.get(position);
        int nbScan =0;
        for(String code : listGtin)
        {
            if(code.contentEquals(code_courant))
            {
                nbScan++;
            }
        }
        Produit produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, code_courant);
        int quantite = 0;

        if(produit_courant != null)
        {
            //gestion du conditionnement dans les code GS1
            for(String gs1Courant : GtinProduitScan)
            {
                Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1Courant);
                if (gs1Decoupe.size() != 0) {
                    String gtin_courant = gs1Decoupe.get(OutilsDecodage.codeGtin);
                    if(gtin_courant.contentEquals(code_courant))
                    {
                        String string_conditionnement = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
                        if(!string_conditionnement.contentEquals(""))
                            quantite = quantite+ Integer.parseInt(string_conditionnement);
                    }
                }
            }
            if(quantite == 0)
                quantite = produit_courant.getCond_achat();

            quantite = quantite*nbScan;

            viewHolder.designation.setText(produit_courant.getDesignation_interne());
            viewHolder.quantite.setText(String.valueOf(quantite));
        }

        return convertView;
    }

    public class StringViewHolder {
        TextView designation;
        TextView quantite;
    }

}