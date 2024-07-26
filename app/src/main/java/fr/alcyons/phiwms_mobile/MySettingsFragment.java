package fr.alcyons.phiwms_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.ParametresServeur.ServiceParametresServeurActivity;

public class MySettingsFragment extends PreferenceFragmentCompat {

    private Intent intention;

    public MySettingsFragment(Intent intention){
        this.intention = intention;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference transmission = findPreference("transmission");
        if (transmission != null) {
            transmission.setOnPreferenceClickListener((preference) -> {

                SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
                String ipServ = sharedPreferences.getString("ipServeur", "");
                String numPort = sharedPreferences.getString("numPort", "");
                String versAPI = sharedPreferences.getString("versAPI", "");
                Log.d("",ipServ);
                Log.d("",numPort);
                Log.d("",versAPI);
                if (ipServ != "" && numPort != "" && versAPI != ""){
                    DBOpenHelper gestionnaireBDD = new DBOpenHelper(getContext(), DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
                    SQLiteDatabase db = gestionnaireBDD.openDB();

                    String mailPharmacie = ParametresServeurOpenHelper.getMailPharmacie(db);
                    String publishKey = "";
                    String subscribeKey = "";
                    String etablissementNom = ParametresServeurOpenHelper.getEtablissementNom(db);
                    int etablissementNumero = 0;
                    String etablissementLogoNom = "";
                    Boolean Reliquats_pour_prevision = ParametresServeurOpenHelper.getReliquats_pour_prevision(db);
                    Boolean Liv_indirecte_egal_Cond_achat = ParametresServeurOpenHelper.getLiv_indirecte_egal_Cond_achat(db);
                    Boolean plan_de_cueillette = ParametresServeurOpenHelper.getPlanDeCueilletteActif(db);
                    Boolean module_transport = ParametresServeurOpenHelper.getModuleTransport(db);
                    String Mail_Emetteur = ParametresServeurOpenHelper.getMailEmetteur(db);
                    String MDP_Emetteur = ParametresServeurOpenHelper.getMDPEmetteur(db);
                    int SMTP_Port = ParametresServeurOpenHelper.getSMTPPort(db);
                    String SMTP_Serveur = ParametresServeurOpenHelper.getSMTPServeur(db);
                    int SMTP_Session = ParametresServeurOpenHelper.getSMTPSession(db);
                    String loginEmetteur = ParametresServeurOpenHelper.getLoginEmetteur(db);

                    ParametresServeurOpenHelper.updateParametresServeurEnBDD(db, ipServ, numPort, versAPI,
                            mailPharmacie, publishKey, subscribeKey,
                            etablissementNom, etablissementNumero, etablissementLogoNom,
                            Reliquats_pour_prevision, Liv_indirecte_egal_Cond_achat, plan_de_cueillette, module_transport,
                            Mail_Emetteur, MDP_Emetteur, SMTP_Port, SMTP_Serveur, SMTP_Session, loginEmetteur);

                    Log.d("","test");

                    if (this.intention != null){
                        startActivity(intention);
                    }

                }
                return true; // Return true if the event is handled.
            });
        }

    }
}