package fr.alcyons.phiwms_mobile.Inventaire;


import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LISTE_LOTS;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Inventaire;
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.InventaireLigneTempAdapter;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ListeLotInventaireActivity  extends ServiceAvecConnexionActivity {
    Produit produitCourant;
    List<Inventaire_Ligne_Temp> inventaireLigneTempList;
    Inventaire inventaireCourant;
    String zoneCourante;
    RecyclerView recyclerView;
    InventaireLigneTempAdapter adapter;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lot_inventaire);
        context = ListeLotInventaireActivity.this;

        inventaireCourant = InventaireOpenHelper.getInventaireById(db, intent.getExtras().getInt("inventaireId"));
        zoneCourante = intent.getExtras().getString("zoneSelectionne");
        produitCourant = ProduitOpenHelper.getProduitByID(db, intent.getExtras().getInt("produitId"));

        inventaireLigneTempList = Inventaire_Ligne_TempOpenHelper.getAllInventaireLigneTempByInventaireEtZoneEtProduit(db, inventaireCourant.getInventaire_ID(), zoneCourante, produitCourant.getID_produit());

        ((TextView) findViewById(R.id.produitDesgination)).setText(inventaireLigneTempList.get(0).getDesignation());
        ((TextView) findViewById(R.id.inventaireCourant)).setText("#"+inventaireCourant.getInventaire_ID()+" - "+inventaireCourant.getObjet());

        ((LinearLayout) findViewById(R.id.lancerScan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuDatamatrixClick();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        int decorationCount = recyclerView.getItemDecorationCount();
        for (int i = 0; i < decorationCount; i++) {
            recyclerView.removeItemDecorationAt(0);
        }
        DividerItemDecoration divider = new DividerItemDecoration(ListeLotInventaireActivity.this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(ListeLotInventaireActivity.this, R.drawable.recycler_divider));
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        adapter = new InventaireLigneTempAdapter(inventaireLigneTempList, position -> {
            Toast.makeText(this, "Supprimer " + inventaireLigneTempList.get(position), Toast.LENGTH_SHORT).show();
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder != null && viewHolder instanceof InventaireLigneTempAdapter.InventaireLigneTempViewHolder) {

                Inventaire_Ligne_Temp courant = inventaireLigneTempList.get(position);
                courant.setStockPhysique(-1);
                courant.setInventaireDate("null");
                Inventaire_Ligne_TempOpenHelper.mettreAJourInventaireLigneTemp(db, courant);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, courant.getPhiMR4UUID(), courant.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                ElementASynchroniserOpenHelper.toutSynchroniser(ListeLotInventaireActivity.this, db, utilisateurConnecte, false);
                adapter.notifyItemChanged(position);
                onResume();
            }
        }, ListeLotInventaireActivity.this);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:


                    onResume();
                    break;
            }
        }
        invalidateOptionsMenu();
    }

    // On remet les quantités à 0 et on quitte l'activité
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onMenuSaveClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);

        menu.findItem(R.id.menuSaveCircle).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemSave = menu.findItem(R.id.menuSaveCircle);

        itemSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onMenuSaveClick();
                return true;
            }
        });

        return true;
    }

    private void onMenuSaveClick() {
        String erreur = "";

        if(erreur.contentEquals(""))
        {
            Intent resultIntent = new Intent();

            Bundle extras = ListeLotInventaireActivity.super.getBundle();
            resultIntent.putExtras(extras);

            ListeLotInventaireActivity.this.setResult(RETOUR_LISTE_LOTS, resultIntent);
            ListeLotInventaireActivity.this.finish();
        }
    }

    private void onMenuDatamatrixClick() {
        int index = -1;

        Intent listeLotInventaireLigneTemp_Intent = new Intent(ListeLotInventaireActivity.this, BarcodePreparationActivity.class);
        //gestion du zebra
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            listeLotInventaireLigneTemp_Intent = new Intent(ListeLotInventaireActivity.this, ScannerPreparationActivity.class);
        }

        Bundle listeLotInventaireLigneTemp_Bundle = super.getBundle();
        listeLotInventaireLigneTemp_Bundle.putInt("UserId", utilisateurConnecte.getId());
        listeLotInventaireLigneTemp_Bundle.putInt("inventaireId", inventaireCourant.getInventaire_ID());
        listeLotInventaireLigneTemp_Bundle.putString("zoneCourante", zoneCourante);
        listeLotInventaireLigneTemp_Bundle.putSerializable("liste_inventaire_ligne_temp", (Serializable) inventaireLigneTempList);

        listeLotInventaireLigneTemp_Intent.putExtras(listeLotInventaireLigneTemp_Bundle);
        ListeLotInventaireActivity.this.startActivityForResult(listeLotInventaireLigneTemp_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
    }

    public void onClickLigne(final int position) {

        Inventaire_Ligne_Temp courant = inventaireLigneTempList.get(position);
        Produit produitCourant = ProduitOpenHelper.getProduitByID(db, courant.getProduitID());
        final int[] conditionnement = {produitCourant.getCond_achat()};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_inventaire_comptage, null);

        TextView designationReference_TV = view.findViewById(R.id.designationReference_TV);
        TextView zoneLot_TV = view.findViewById(R.id.zoneLot_TV);
        TextView emplacementLot_TV = view.findViewById(R.id.emplacementLot_TV);
        TextView textCartonFermer_TV = view.findViewById(R.id.textCartonFermer_TV);
        EditText numeroLot_ET = view.findViewById(R.id.numeroLot_ET);
        EditText dateExpirationLot_ET = view.findViewById(R.id.dateExpirationLot_ET);
        EditText quantiteComptee_ET = view.findViewById(R.id.quantiteComptee_ET);
        LinearLayout layoutCartonFermer_LL = view.findViewById(R.id.layoutCartonFermer_LL);
        LinearLayout layoutCartonOuvert_LL = view.findViewById(R.id.layoutCartonOuvert_LL);
        LinearLayout layoutMoins_LL = view.findViewById(R.id.layoutMoins_LL);
        LinearLayout layoutPlus_LL = view.findViewById(R.id.layoutPlus_LL);
        LinearLayout layoutValider_LL = view.findViewById(R.id.layoutValider_LL);
        ImageView quitterModale_IV = view.findViewById(R.id.quitterModale_IV);

        designationReference_TV.setText(courant.getDesignation());
        zoneLot_TV.setText(courant.getZone());
        emplacementLot_TV.setText(courant.getEmplacement());
        numeroLot_ET.setText(courant.getLot());

        if(courant.getPeremptionDate().contentEquals("null") || courant.getPeremptionDate().contentEquals("")  || courant.getInventaireDate().contentEquals("00/00/0000"))
            dateExpirationLot_ET.setText("");
        else
        {
            String[] dateParts = courant.getPeremptionDate().split("-");
            if(dateParts.length == 3)
                dateExpirationLot_ET.setText(dateParts[2]+"/"+dateParts[1]+"/"+dateParts[0]);
            else
                dateExpirationLot_ET.setText(courant.getPeremptionDate());
        }

        textCartonFermer_TV.setText(textCartonFermer_TV.getText()+"\n(x"+produitCourant.getCond_achat()+")");

        if(courant.getInventaireDate().contentEquals("") || courant.getInventaireDate().contentEquals("null"))
        {
            quantiteComptee_ET.setText("0");
        }
        else
        {
            quantiteComptee_ET.setText(String.valueOf((int)courant.getStockPhysique()));
        }

        layoutCartonFermer_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conditionnement[0] = produitCourant.getCond_achat();
                layoutCartonOuvert_LL.setBackgroundColor(getResources().getColor(R.color.blanc));
                layoutCartonFermer_LL.setBackgroundColor(getResources().getColor(R.color.vertTransparent));
            }
        });

        layoutCartonOuvert_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conditionnement[0] = 1;
                layoutCartonFermer_LL.setBackgroundColor(getResources().getColor(R.color.blanc));
                layoutCartonOuvert_LL.setBackgroundColor(getResources().getColor(R.color.vertTransparent));
            }
        });

        layoutPlus_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qteActuelle = Integer.parseInt(quantiteComptee_ET.getText().toString());
                qteActuelle += conditionnement[0];
                quantiteComptee_ET.setText(String.valueOf(qteActuelle));
            }
        });

        layoutMoins_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qteActuelle = Integer.parseInt(quantiteComptee_ET.getText().toString());
                qteActuelle -= conditionnement[0];
                if(qteActuelle < 0)
                    qteActuelle = 0;
                quantiteComptee_ET.setText(String.valueOf(qteActuelle));
            }
        });

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        layoutValider_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                courant.setStockPhysique(Integer.parseInt(quantiteComptee_ET.getText().toString()));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateDuJour = sdf.format(new Date());
                courant.setInventaireDate(dateDuJour);
                Inventaire_Ligne_TempOpenHelper.mettreAJourInventaireLigneTemp(db, courant);

                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, courant.getPhiMR4UUID(), courant.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                ElementASynchroniserOpenHelper.toutSynchroniser(ListeLotInventaireActivity.this, db, utilisateurConnecte, false);

                if (viewHolder instanceof InventaireLigneTempAdapter.InventaireLigneTempViewHolder) {
                    InventaireLigneTempAdapter.InventaireLigneTempViewHolder monViewHolder = (InventaireLigneTempAdapter.InventaireLigneTempViewHolder) viewHolder;
                    monViewHolder.qteStockPhysique.setText(quantiteComptee_ET.getText().toString());
                    adapter.notifyItemChanged(position);
                    alertDialog.dismiss();
                }
            }
        });

        quitterModale_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
}
