package fr.alcyons.phimr4.DemandeReassort.fragment;

import androidx.fragment.app.Fragment;

/**
 * Created by jessica on 05/10/2017.
 */

public class InformationDemandeReassort extends Fragment {
/*
    PH_Reassort ph_reassort;

    Context context;
    Calendar myCalendar;

    TextView datePrevisionDu;
    TextView datePrevisionAu;
    TextView dateLivraisonProchaine;
    TextView dateLivraisonSuivante;
    EditText commentaire;
    CheckBox urgent;
    FloatingActionButton boutonPreparer;

    DatePickerDialog.OnDateSetListener datePrevisionDuDatePicker;
    DatePickerDialog.OnDateSetListener datePrevisionAuDatePicker;

    View.OnClickListener boutonPreparerOnClickListener;

    String dateInventaireString;
    String dateLivraisonProchaineString;
    String dateLivraisonSuivanteString;

    // Fonctions permettant au parent de nous transmettre des paramètres, possibilité d'en faire plusieurs
    public void setParametres(PH_Reassort ph_reassort, String dateInventaireString, String dateLivraisonProchaineString, String dateLivraisonSuivanteString) {
        this.ph_reassort = ph_reassort;
        this.dateInventaireString = dateInventaireString;
        this.dateLivraisonProchaineString = dateLivraisonProchaineString;
        this.dateLivraisonSuivanteString = dateLivraisonSuivanteString;
    }

    public void setParametres(View.OnClickListener boutonPreparerOnClickListener) {
        this.boutonPreparerOnClickListener = boutonPreparerOnClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.information_demande_reassort, container, false);

        context = getContext();

        // Récupération et initialisation du bonton permettant de valider la demande
        boutonPreparer = (FloatingActionButton) v.findViewById(R.id.boutonPreparer);
        boutonPreparer.setOnClickListener(boutonPreparerOnClickListener);


        *//* Gestion des dates et des DatePicker *//*

        // Récupération du " Calendar " du téléphone
        myCalendar = Calendar.getInstance();

        // Initialisation datePrevisionDu
        datePrevisionDu = ((TextView) v.findViewById(R.id.datePrevisionDu));

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        // Si dans l'écran précédent initialisation de la date d'inventaire sinon date du jour
        if (dateInventaireString != "00/00/0000") {
            datePrevisionDu.setText(dateInventaireString);
        } else {
            datePrevisionDu.setText(sdf.format(myCalendar.getTime()));
        }

        datePrevisionDuDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(datePrevisionDu);
            }

        };

        datePrevisionDu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Restriction de la selection de la date MAX à celle de datePrevisionAu
                String dateMaxString = datePrevisionAu.getText().toString();

                int year = myCalendar.get(Calendar.YEAR);
                int month = myCalendar.get(Calendar.MONTH);
                int day = myCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePrevisionDuPickerDialog = new DatePickerDialog(getActivity(), datePrevisionDuDatePicker, year, month, day);

                if (!dateMaxString.contentEquals("00/00/0000")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateMax = new Date();
                    try {
                        dateMax = dateFormat.parse(dateMaxString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    datePrevisionDuPickerDialog.getDatePicker().setMaxDate(dateMax.getTime());
                }

                datePrevisionDuPickerDialog.show();

            }
        });

        // Initialisation datePrevisionAu
        datePrevisionAu = ((TextView) v.findViewById(R.id.datePrevisionAu));
        datePrevisionAu.setText(dateLivraisonSuivanteString);

        datePrevisionAuDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(datePrevisionAu);
            }

        };

        datePrevisionAu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Restriction de la selection de la date MIN à la date du jour par défaut sinon à celle de dateLivraisonProchaine
                String dateMinString = dateLivraisonProchaine.getText().toString();

                int year = myCalendar.get(Calendar.YEAR);
                int month = myCalendar.get(Calendar.MONTH);
                int day = myCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePrevisionAuPickerDialog = new DatePickerDialog(getActivity(), datePrevisionAuDatePicker, year, month, day);
                datePrevisionAuPickerDialog.getDatePicker().setMinDate(myCalendar.getTime().getTime());

                if (!dateMinString.contentEquals("00/00/0000")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateMin = new Date();
                    try {
                        dateMin = dateFormat.parse(dateMinString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    year = dateMin.getYear();
                    month = dateMin.getMonth();
                    day = dateMin.getDay();

                    // Create a new instance of DatePickerDialog and return it
                    datePrevisionAuPickerDialog = new DatePickerDialog(getActivity(), datePrevisionAuDatePicker, year, month, day);
                    datePrevisionAuPickerDialog.getDatePicker().setMinDate(dateMin.getTime());
                }

                datePrevisionAuPickerDialog.show();
            }
        });

        // Récupération et initalisation dateLivraisonProchaine et dateLivraisonSuivante
        dateLivraisonProchaine = ((TextView) v.findViewById(R.id.dateLivraisonProchaine));
        dateLivraisonSuivante = ((TextView) v.findViewById(R.id.dateLivraisonSuivante));

        dateLivraisonProchaine.setText(dateLivraisonProchaineString);
        dateLivraisonSuivante.setText(dateLivraisonSuivanteString);

        // Récupération du dépot concerné par la demande de dotation
        Depot depot = DepotOpenHelper.getDepotParReference(((MenuActivity) context).db, ph_reassort.getDepot_Reference());

        // Récupération des objets graphiques
        commentaire = ((EditText) v.findViewById(R.id.commentaire));
        urgent = ((CheckBox) v.findViewById(R.id.isUrgent));

        // Affichage des informations de base
        ((TextView) v.findViewById(R.id.nomDemande)).setText(String.valueOf(ph_reassort.getListe()));
        ((TextView) v.findViewById(R.id.typeDemande)).setText("Réassort service");
        ((TextView) v.findViewById(R.id.referenceDepot)).setText(ph_reassort.getDepot_Reference());
        ((TextView) v.findViewById(R.id.dateDemande)).setText(sdf.format(myCalendar.getTime()));

        ((TextView) v.findViewById(R.id.identiteDepot)).setText(depot.getNom());
        ((TextView) v.findViewById(R.id.telephone)).setText("Tel : " + depot.getTel());
        ((TextView) v.findViewById(R.id.fax)).setText("Fax : " + depot.getFax());

        // Récupération de l'adresse du dépot, attention dans le cas d'un PAD vérification de l'utilisation ou non de son adresse de vacance
        String adresse = "";

        if (depot.getDepot_Reference().contains("PAD") && depot.isPAD_Utiliser_Adresse_Vacances()) {
            adresse = depot.getPAD_Vacances_Adr1() + ", ";
            if (depot.getPAD_Vacances_Adr2().length() > 1) {
                adresse += depot.getPAD_Vacances_Adr2() + ", ";
            }
            adresse += depot.getPAD_Vacances_CP() + " " + depot.getPAD_Vacances_Ville();
        } else {
            adresse = depot.getAdresse1() + ", ";
            if (depot.getAdresse2().length() > 1) {
                adresse += depot.getAdresse2() + ", ";
            }
            adresse += depot.getCP() + " " + depot.getVille();
        }

        ((TextView) v.findViewById(R.id.adresse)).setText(adresse);

        ((MenuActivity) context).invalidateOptionsMenu();
        return v;
    }

    // Transformation de la date choisi au format voulu
    private void updateLabel(TextView dateTextView) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        dateTextView.setText(sdf.format(myCalendar.getTime()));
    }*/
}
