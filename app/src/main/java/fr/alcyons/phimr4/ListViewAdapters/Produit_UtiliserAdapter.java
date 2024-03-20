package fr.alcyons.phimr4.ListViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Produit_Utiliser_Adapte;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionPhotos;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.Utiliser.DetailUtiliserActivity;

import static fr.alcyons.phimr4.Outils.Alerte.aNumberPicker;

/**
 * Created by quentinlanusse on 02/08/2017.
 */

public class Produit_UtiliserAdapter extends ArrayAdapter {

    public List<Produit_Utiliser_Adapte> produits;
    public List<ProduitViewHolder> produitViewHolders;
    Context context;
    SQLiteDatabase db;

    public Produit_UtiliserAdapter(Context context, List<Produit_Utiliser_Adapte> produitRecus, SQLiteDatabase db) {
        super(context, 0, produitRecus);
        produits = produitRecus;
        this.context = context;
        produitViewHolders = new ArrayList<>();
        this.db = db;
        for (Produit_Utiliser_Adapte produit : produits) {
            produitViewHolders.add(new ProduitViewHolder());
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View currentFocus = ((Activity) context).getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) ((Activity) context).getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            currentFocus.clearFocus();
        }

        // Récupération d'éléments nécessaires à l'affichage
        final ProduitViewHolder viewHolder = produitViewHolders.get(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_produit_utiliser, parent, false);
        final Produit_Utiliser_Adapte produit = produits.get(position);

        Produit produitCorrespondant = ProduitOpenHelper.getProduitByID(db, produit.getId());

        // Récupération des objets graphiques
        viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
        viewHolder.designation = (TextView) convertView.findViewById(R.id.designationProduit);
        viewHolder.qte = (TextView) convertView.findViewById(R.id.qteSaisie);
        viewHolder.numLot = (TextView) convertView.findViewById(R.id.numLot);
        viewHolder.datePeremption = (TextView) convertView.findViewById(R.id.datePeremption);
        viewHolder.emplacement = (TextView) convertView.findViewById(R.id.nomEmplacement);
        viewHolder.zone = (TextView) convertView.findViewById(R.id.nomZone);
        viewHolder.boutonMoins = (ImageView) convertView.findViewById(R.id.boutonMoins);
        viewHolder.boutonPlus = (ImageView) convertView.findViewById(R.id.boutonPlus);
        viewHolder.boutonSuppression = (ImageView) convertView.findViewById(R.id.boutonSuppression);
        viewHolder.boutonModif = (ImageView) convertView.findViewById(R.id.boutonModif);

        int qté = 1;
        if (produitCorrespondant != null) {
            viewHolder.designation.setText(produitCorrespondant.getDesignation_interne());
            qté = produit.getQte();
        } else {
            viewHolder.designation.setText("");
        }

        viewHolder.qte.setText(String.valueOf(qté));
        viewHolder.numLot.setText(produit.getNumLot());
        Depot_Zone zone = ZoneOpenHelper.getUneZoneByID(db, produit.getZoneID());
        Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, produit.getEmplacementID());

        if (zone != null) {
            viewHolder.zone.setText(zone.getZoneName());
        }
        if (emplacement != null) {
            viewHolder.emplacement.setText(emplacement.getAdressage());
        }

        Date date = null;
        String dateAAfficher = "";
        DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (produit.getDatePeremption().length() >= 10) {
                date = dateDecodeur.parse(produit.getDatePeremption().substring(0, 10));
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                dateAAfficher = dateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.datePeremption.setText(dateAAfficher);
        viewHolder.setDatePeremptionColor(date);

        Bitmap bitmap;

        if (produit.getImage() != "") {
            bitmap = OutilsGestionPhotos.loadExternalStorageImageBitmap(context, produit.getImage(), "jpg");
        } else {
            bitmap = OutilsGestionPhotos.loadExternalStorageImageBitmap(context, produit.getCodeGS1(), "jpg");
        }

        if (bitmap != null) {
            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 600, 600, true);
            viewHolder.imageView.setImageBitmap(newBitmap);
        }

        // gestion des boutons
        viewHolder.boutonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ProduitViewHolder viewHolder : produitViewHolders
                        ) {
                    if (viewHolder.boutonPlus == v) {
                        Produit_Utiliser_Adapte produit = produits.get(produitViewHolders.indexOf(viewHolder));
                        produit.setQte(Integer.parseInt(viewHolder.qte.getText().toString()) + 1);
                        notifyDataSetChanged();
                        return;
                    }
                }
            }
        });

        viewHolder.boutonMoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ProduitViewHolder viewHolder : produitViewHolders
                        ) {
                    if (viewHolder.boutonMoins == v) {
                        Produit_Utiliser_Adapte produit = produits.get(produitViewHolders.indexOf(viewHolder));
                        if (produit.getQte() > 0) {
                            produit.setQte(Integer.parseInt(viewHolder.qte.getText().toString())  - 1);
                            notifyDataSetChanged();
                        }
                        return;
                    }
                }
            }
        });
        viewHolder.boutonSuppression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ProduitViewHolder viewHolder : produitViewHolders) {
                    if (viewHolder.boutonSuppression == v) {
                        produits.remove(produits.get(produitViewHolders.indexOf(viewHolder)));
                        notifyDataSetChanged();
                        ((TextView) ((AppCompatActivity) context).findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(produits.size()));
                        return;
                    }

                }
            }
        });
        viewHolder.boutonModif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ProduitViewHolder viewHolder : produitViewHolders) {
                    if (viewHolder.boutonModif == v) {
                        Produit_Utiliser_Adapte produitUtiliserAdapte = produits.get(produitViewHolders.indexOf(viewHolder));
                        ((DetailUtiliserActivity) context).modifierZoneEtEmplacementProduit(produitUtiliserAdapte);
                        return;
                    }
                }
            }
        });

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ProduitViewHolder viewHolder : produitViewHolders) {
                    if (viewHolder.imageView == v) {
                        Produit_Utiliser_Adapte produitUtiliserAdapte = produits.get(produitViewHolders.indexOf(viewHolder));
                        ((DetailUtiliserActivity) context).modifierProduit(produitUtiliserAdapte);
                        return;
                    }
                }
            }
        });


        viewHolder.qte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ouvre une boite de dialogue avec un NumberPicker
                String title = produitViewHolders.get(position).designation.getText().toString();
                String message = "Quantité placée : ";
                int maxValue = 1000000;
                int value = 0;
                if(!produitViewHolders.get(position).qte.getText().toString().contentEquals(""))
                        value = Integer.parseInt(produitViewHolders.get(position).qte.getText().toString());

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = aNumberPicker.getValue();
                        produitViewHolders.get(position).qte.setText(String.valueOf(qteAprès));
                        produit.setQte(qteAprès);
                        notifyDataSetChanged();
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                    }
                };

                Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

            }
        });

        return convertView;

    }

    private int mod(int x, int y) {
        int result = x % y;
        if (result < 0)
            result += y;
        return result;
    }

    public class ProduitViewHolder {
        ImageView imageView;
        public TextView designation;
        public TextView qte;
        TextView numLot;
        TextView datePeremption;
        TextView emplacement;
        TextView zone;

        ImageView boutonSuppression;
        ImageView boutonMoins;
        ImageView boutonPlus;
        ImageView boutonModif;

        public void setDatePeremptionColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.rouge2));
                } else if (delai >= delai60jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.orange2));
                } else {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.vert));
                }
            } else {
                datePeremption.setTextColor(Color.BLACK);
            }

        }
    }

}
