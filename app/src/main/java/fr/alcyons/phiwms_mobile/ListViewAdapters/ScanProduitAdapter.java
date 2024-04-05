package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import com.example.phiwms_mobile.R;

/**
 * Created by olivier on 10/05/2019.
 */

public class ScanProduitAdapter extends ArrayAdapter {
    private List<String> ListeLotScan = new ArrayList<>();
    public List<ScanProduitViewHolder> stringViewHolders = new ArrayList<>();
    SQLiteDatabase db;
    private List<String> listGtin;
    private HashMap<String, String> mapLotProduit;
    private HashMap<String, String> mapLotPeremption;
    private HashMap<String, Integer> mapLotNbScan;
    private Context context;

    public ScanProduitAdapter(Context context, List<String> ListeLotScan, SQLiteDatabase db, HashMap<String, String> mapLotProduit, HashMap<String, String> mapLotPeremption, HashMap<String, Integer> mapLotNbScan) {
        super(context, 0, ListeLotScan);

        this.ListeLotScan = ListeLotScan;
        this.db = db;
        this.listGtin = new ArrayList<>();
        this.mapLotProduit = mapLotProduit;
        this.mapLotPeremption = mapLotPeremption;
        this.mapLotNbScan = mapLotNbScan;
        this.context = context;

        for(String lotCourant : ListeLotScan)
        {
            stringViewHolders.add(new ScanProduitViewHolder());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_scan_produit_reception_scannee, parent, false);
        }

        ScanProduitViewHolder viewHolder = (ScanProduitViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = stringViewHolders.get(position);
            viewHolder.qteProduitScannee = (TextView) convertView.findViewById(R.id.qteProduitScannee);
            viewHolder.DesignationProduitScannee = (TextView) convertView.findViewById(R.id.DesignationProduitScannee);
            viewHolder.numLotProduitScannee = (TextView) convertView.findViewById(R.id.numLotProduitScannee);
            viewHolder.datePeremptionProduitScannee = (TextView) convertView.findViewById(R.id.datePeremptionProduitScannee);
            viewHolder.supprimerScan = (ImageView) convertView.findViewById(R.id.supprimerScan);
            convertView.setTag(viewHolder);
        }
        String lot_courant = ListeLotScan.get(position);
        String gtin_courant = mapLotProduit.get(lot_courant);
        String date_peremption_courant = mapLotPeremption.get(lot_courant);
        int nbScan = mapLotNbScan.get(lot_courant);

        viewHolder.supprimerScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanProduitViewHolder viewHolderSelectionne = null;

                for (ScanProduitViewHolder viewHolder : stringViewHolders) {
                    if (viewHolder.supprimerScan == v) {
                        viewHolderSelectionne = viewHolder;
                    }
                }

            }
        });

        DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
        DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

        Date date = new Date();

        try {
            date = dateFormat1.parse(date_peremption_courant);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String dateFinale = dateFormat2.format(date);

        Produit produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin_courant);

        viewHolder.numLotProduitScannee.setText(lot_courant);
        if(produit_courant != null)
            viewHolder.DesignationProduitScannee.setText(produit_courant.getDesignation_interne());

        viewHolder.datePeremptionProduitScannee.setText(dateFinale);

        int quantite_total = nbScan*produit_courant.getCond_achat();
        viewHolder.qteProduitScannee.setText(String.valueOf(quantite_total));

        return convertView;
    }

    public class ScanProduitViewHolder {
        public TextView qteProduitScannee;
        public TextView DesignationProduitScannee;
        public TextView numLotProduitScannee;
        public TextView datePeremptionProduitScannee;
        public ImageView supprimerScan;
    }

}