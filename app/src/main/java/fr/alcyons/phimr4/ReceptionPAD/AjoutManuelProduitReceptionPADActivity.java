package fr.alcyons.phimr4.ReceptionPAD;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import fr.alcyons.phimr4.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.Commande;
import fr.alcyons.phimr4.Classes.ObjetReceptionScannee;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.ListViewAdapters.ajoutManuelReceptionPADAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.R;

import fr.alcyons.phimr4.ServiceActivity;

import static fr.alcyons.phimr4.Outils.Alerte.aNumberPicker;

public class AjoutManuelProduitReceptionPADActivity extends ServiceActivity {

    //déclaration des variables
    private int commandeID;
    private Commande commandeCourante;
    private List<PH_Reliquat> ph_reliquatListTemp;
    private List<PH_Reliquat> ph_reliquatList;
    List<ObjetReceptionScannee> objetDejaScanne;

    //gestion des objets graphiques
    private TextView commandeNumero_textview;
    private TextView fournisseurCommande_textview;
    private ListView reliquat_listview;
    AlertDialog alertDialog;

    //gestion de l'adapter
    private ajoutManuelReceptionPADAdapter adapter;
    LinearLayout lancerScan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_produit_manuel_reception_scannee);

        //récupération de la liste des objets déja scanné
        objetDejaScanne = new ArrayList<>();
        objetDejaScanne.addAll((List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee"));

        if(objetDejaScanne.size()>1)
        {
        }

        //récupération de la commande et des ph_reliquats
        commandeID = intent.getExtras().getInt("CommandeId");
        commandeCourante = CommandeOpenHelper.getCommandeByID(db, commandeID);

        if(commandeCourante != null)
        {
            ph_reliquatListTemp = new ArrayList<>();
            ph_reliquatList = new ArrayList<>();
            ph_reliquatListTemp = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeCourante.getNumero());

            for(PH_Reliquat temp : ph_reliquatListTemp)
            {
                if(temp.getQteReliquat_X() != 0)
                {
                    ph_reliquatList.add(temp);
                }
            }

            //gestion des objets graphiques
            //initialisation
            commandeNumero_textview = (TextView) findViewById(R.id.commandeNumero);
            fournisseurCommande_textview = (TextView) findViewById(R.id.fournisseurCommande);
            lancerScan = (LinearLayout) findViewById(R.id.lancerScan);

            lancerScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent resultintent = new Intent();
                    Bundle resultBundle = new Bundle();
                    resultBundle.putSerializable("listeString", (Serializable) objetDejaScanne);
                    resultBundle.putBoolean("lancerScan", true);
                    resultintent.putExtras(resultBundle);
                    setResult(CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH, resultintent);
                    finish();
                }
            });

            //gestion des valeurs
            commandeNumero_textview.setText("#"+commandeCourante.getNumero());
            fournisseurCommande_textview.setText(commandeCourante.getFournisseur());
            //nbElementInAdapter_text_view.setText(String.valueOf(ph_reliquatList.size()));
            reliquat_listview = (ListView) findViewById(R.id.listeView);

            //gestion de l'adapter
            adapter = new ajoutManuelReceptionPADAdapter(AjoutManuelProduitReceptionPADActivity.this, ph_reliquatList, db);
            reliquat_listview.setDivider(footer);
            reliquat_listview.setAdapter(adapter);

            reliquat_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    //récupération du ph_reliquat sélectionné
                    final PH_Reliquat reliquat_selectionne = adapter.list_reliquat.get(position);

                    //initialisation de l'alerte pour saisir les informations concernant le produit sélectionné
                    AlertDialog.Builder builder = new AlertDialog.Builder(AjoutManuelProduitReceptionPADActivity.this);
                    LayoutInflater inflater = AjoutManuelProduitReceptionPADActivity.this.getLayoutInflater();
                    View layout = inflater.inflate(R.layout.alerte_ajout_manuel_reception_pad, null);

                    //récupération des objets graphiques
                    final EditText numeroLot_edittext = (EditText) layout.findViewById(R.id.numLotNouveauProduit);
                    final TextView date_peremption_textview = (TextView) layout.findViewById(R.id.datePeremptionNouveauProduit);
                    final TextView qte_reception_textview = (TextView) layout.findViewById(R.id.quantiteReceptionNouveauProduit);
                    final TextView qteRestante_textview = (TextView) layout.findViewById(R.id.qteRestante);
                    TextView nom_produit_textview = (TextView) layout.findViewById(R.id.nomProduitSelectionne);
                    LinearLayout annuler_layout = (LinearLayout) layout.findViewById(R.id.layoutAnnulation);
                    LinearLayout confirmer_layout = (LinearLayout) layout.findViewById(R.id.layoutConfirmation);
                    numeroLot_edittext.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    //initialisation des objets
                    nom_produit_textview.setText(reliquat_selectionne.getDesignationCourte());
                    qteRestante_textview.setText(String.valueOf(reliquat_selectionne.getQteReliquat_X()));
                    qte_reception_textview.setText(String.valueOf(reliquat_selectionne.getQteReliquat_X()));
                    Produit produitcourant = ProduitOpenHelper.getProduitByID(db, reliquat_selectionne.getProduitID());

                    if(!produitcourant.isSuivi_Lot())
                    {
                        //gestion de la date
                        String currentDate = new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(new Date());

                        numeroLot_edittext.setText("Phi"+currentDate);

                        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                        int currentYear = calendar.get(Calendar.YEAR)+1;
                        int currentMonth = calendar.get(Calendar.MONTH)+1;
                        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                        String dateNextYear = currentDay+"/"+currentMonth+"/"+currentYear;

                        date_peremption_textview.setText(dateNextYear);
                    }

                    //gestion du click pour la date de péremption
                    date_peremption_textview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatePickerFragment newFragment = new DatePickerFragment();
                            newFragment.setTextView(date_peremption_textview);
                            newFragment.show((AjoutManuelProduitReceptionPADActivity.this).getSupportFragmentManager(), "timePicker");
                        }
                    });

                    //gestion du click sur la quantite pour modifier la quantite de réception
                    qte_reception_textview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Ouvre une boite de dialogue avec un NumberPicker
                            String title = reliquat_selectionne.getDesignationCourte();
                            String message = "Quantité réceptionné : ";
                            int maxValue = reliquat_selectionne.getQteReliquat_X();
                            int value = reliquat_selectionne.getQteReliquat_X();

                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    int qteAprès = aNumberPicker.getValue()*reliquat_selectionne.getConditionnementAchat();
                                    qte_reception_textview.setText(String.valueOf(qteAprès));
                                    InputMethodManager imm = (InputMethodManager) AjoutManuelProduitReceptionPADActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                    dialog.dismiss();
                                }
                            };

                            Alerte.afficherAlerteNumberPickerAvecPas(AjoutManuelProduitReceptionPADActivity.this, title, message, value, maxValue, onClickListener, reliquat_selectionne.getConditionnementAchat());
                        }
                    });


                    //gestion des boutons d'annulation et de confirmation de l'alerte
                    annuler_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(alertDialog.isShowing())
                            {
                                alertDialog.dismiss();
                            }
                        }
                    });

                    confirmer_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //récupération des valeurs
                            int quantite_retourner = Integer.parseInt(qte_reception_textview.getText().toString());
                            String numLot_retourner = numeroLot_edittext.getText().toString();
                            String datePeremption_retourner = date_peremption_textview.getText().toString();


                            if(numLot_retourner.contentEquals(""))
                            {
                                Toast.makeText(AjoutManuelProduitReceptionPADActivity.this, "Saisir un numéro de lot", Toast.LENGTH_SHORT).show();
                            }
                            else if(datePeremption_retourner.contentEquals(""))
                            {
                                Toast.makeText(AjoutManuelProduitReceptionPADActivity.this, "Sélectionner une date de péremption", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String[] date_tab = datePeremption_retourner.split("/");
                                String date_gs1 = date_tab[date_tab.length-1].substring(2)+date_tab[1]+date_tab[0];
                                Produit produit_courant = ProduitOpenHelper.getProduitByID(db, reliquat_selectionne.getProduitID());

                                String gs1_reconstruit = produit_courant.getGTIN()+"17"+date_gs1+"10"+numLot_retourner;
                                if(produit_courant.getGTIN().contentEquals(""))
                                {
                                    gs1_reconstruit = "ci"+produit_courant.getCodeInconnue()+"dp17"+date_gs1+"nl10"+numLot_retourner;
                                }

                                ObjetReceptionScannee objetReceptionScannee = new ObjetReceptionScannee();
                                objetReceptionScannee.setGs1_scannee(gs1_reconstruit);
                                objetReceptionScannee.setEmplacement_uid(0);
                                objetReceptionScannee.setQuantiteScannee(quantite_retourner);
                                objetReceptionScannee.setResultat_france_mvo("");

                                objetDejaScanne.add(objetReceptionScannee);

                                //mise a jour du reliquat
                                reliquat_selectionne.setQteReliquat_X(reliquat_selectionne.getQteReliquat_X()-quantite_retourner);
                                PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquat_selectionne);

                                //fermeture de l'alerte
                                alertDialog.dismiss();

                                Intent resultintent = new Intent();
                                Bundle resultBundle = new Bundle();
                                resultBundle.putSerializable("listeString", (Serializable) objetDejaScanne);
                                resultintent.putExtras(resultBundle);
                                setResult(CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH, resultintent);
                                finish();
                            }
                        }
                    });

                    //Affichage de l'alerte
                    builder.setView(layout);
                    alertDialog = builder.create();
                    alertDialog.getWindow().setGravity(Gravity.CENTER);
                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = (int)(getResources().getDisplayMetrics().heightPixels*0.6);

                    alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, height);
                    alertDialog.setCancelable(false);
                    if (!alertDialog.isShowing()) {
                        alertDialog.show();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        AjoutManuelProduitReceptionPADActivity.this.finish();
    }

    // Class static permettant de faire apparaitre le DatePicker du téléphone
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        TextView datePeremption;

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR)+1;
            c.add(Calendar.MONTH, -1);
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            int month = c.get(Calendar.MONTH);
            int day = c.getActualMaximum(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            Date dateFournie = null;
            month++;
            String mois = "";
            if (month < 10) {
                mois += "0";
            }

            mois += String.valueOf(month);

            String date = String.valueOf(day) + "/" + mois + "/" + String.valueOf(year);
            Date datedate = null;
            String dateAAfficher = "";
            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            try {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                datedate = dateFormat.parse(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            datePeremption.setText(date);
            setDatePeremptionColor(datedate);

        }

        private void setDatePeremptionColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) {
                    datePeremption.setTextColor(getContext().getResources().getColor(R.color.rouge2));
                } else if (delai >= delai60jours) {
                    datePeremption.setTextColor(getContext().getResources().getColor(R.color.orange2));
                } else {
                    datePeremption.setTextColor(getContext().getResources().getColor(R.color.vert));
                }
            } else {
                datePeremption.setTextColor(Color.BLACK);
            }
        }

        public void setTextView(TextView editText) {
            datePeremption = editText;
        }
    }

}
