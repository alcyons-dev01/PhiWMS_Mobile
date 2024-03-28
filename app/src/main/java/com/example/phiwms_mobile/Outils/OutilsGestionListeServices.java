package com.example.phiwms_mobile.Outils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.Collections;
import java.util.List;

import com.example.phiwms_mobile.ActionUtilisateur.ServiceActionUtilisateurActivity;
import com.example.phiwms_mobile.CGU.CguActivity;
import com.example.phiwms_mobile.Classes.PerimetreFonctionnel;
import com.example.phiwms_mobile.Classes.Service;
import com.example.phiwms_mobile.Classes.Utilisateur;
import com.example.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import com.example.phiwms_mobile.ControleDesRetoursScannee.ServiceControleRetoursScanneeActivity;
import com.example.phiwms_mobile.DemandeDotationGlobale.ServiceDemandeDotationGlobaleActivity;
import com.example.phiwms_mobile.DemandeDotationPAD.ServiceDemandeDotationPADActivity;
import com.example.phiwms_mobile.DemandeParticuliere.ServiceDemandeParticuliereActivity;
import com.example.phiwms_mobile.DemandePleinVide.ServiceDemandePleinVideActivity;
import com.example.phiwms_mobile.DemandeProtocolePAD.ServiceDemandeProtocolePADActivity;
import com.example.phiwms_mobile.DemandeReassort.ServiceDemandeReassortActivity;
import com.example.phiwms_mobile.Destruction.ServiceDestructionActivity;
import com.example.phiwms_mobile.DispositifAuLivret.ServiceDispositifAuLivretActivity;
import com.example.phiwms_mobile.DotationService.ServiceDotationServiceActivity;
import com.example.phiwms_mobile.IdentificationParScan.ListeProduitsIdentificationParScanActivity;
import com.example.phiwms_mobile.InventaireScanner.ServiceInventaireScannerActivity;
import com.example.phiwms_mobile.ListViewAdapters.ServiceAdapter;
import com.example.phiwms_mobile.Livraison.DepotSelecteurLivraison;
import com.example.phiwms_mobile.Livraison.ListePointDeLivraison;
import com.example.phiwms_mobile.Livraison.ServiceLivraisonActivity;
import com.example.phiwms_mobile.MedicamentAuLivret.ServiceMedicamentAuLivretActivity;
import com.example.phiwms_mobile.Navigation.NavigationActivity;
import com.example.phiwms_mobile.Notifications.ServiceNotificationsActivity;
import com.example.phiwms_mobile.PAD.ServicePADActivity;
import com.example.phiwms_mobile.ParametresServeur.ServiceParametresServeurActivity;
import com.example.phiwms_mobile.ParametresUtilisateur.ServiceParametreUtilisateurActivity;
import com.example.phiwms_mobile.PlanDePlacement.ListeProduitsPlanDePlacementActivity;
import com.example.phiwms_mobile.PreparationPUFetPAD.ServicePreparationPadActivity;
import com.example.phiwms_mobile.PreparationPUFetPAD.ServicePreparationPufActivity;
import com.example.phiwms_mobile.PreparationPUFetPADScannee.ServicePreparationPadScanneeActivity;
import com.example.phiwms_mobile.PreparationPUFetPADScannee.ServicePreparationPufScanneeActivity;
import com.example.phiwms_mobile.Quarantaine.ServiceQuarantaineActivity;
import com.example.phiwms_mobile.R;
import com.example.phiwms_mobile.ReceptionPAD.ServiceReceptionPadActivity;
import com.example.phiwms_mobile.ReceptionPUI.ServiceReceptionPuiActivity;
import com.example.phiwms_mobile.ReceptionScanne.ServiceReceptionScanneeActivity;
import com.example.phiwms_mobile.RetourDemande.ServiceRetourDemandeActivity;
import com.example.phiwms_mobile.RetourFournisseur.ServiceRetourFournisseurActivity;
import com.example.phiwms_mobile.RetourPUI.ServiceRetourPUIActivity;
import com.example.phiwms_mobile.Serialisation.ServiceSerialisationActivity;
import com.example.phiwms_mobile.ServiceEnCreationActivity;
import com.example.phiwms_mobile.Stock.ServiceStockActivity;
import com.example.phiwms_mobile.TestMail.TestMailActivity;
import com.example.phiwms_mobile.Utiliser.ServiceUtiliserActivity;
import com.example.phiwms_mobile.VerrouPharmacie.ServiceVerrouPharmacieActivity;
import com.example.phiwms_mobile.VerrouPharmacieInterne.ServiceVerrouPharmacieInterneActivity;
import com.example.phiwms_mobile.ZonesEtEmplacements.ServiceZonesEtEmplacementsActivity;

/**
 * Created by quentinlanusse on 19/04/2017.
 */

@TargetApi(Build.VERSION_CODES.M)
public class OutilsGestionListeServices {

    public static void genererListViewServicesParPerimetreFonctionnel(final Context context, final Utilisateur utilisateurConnecte, PerimetreFonctionnel perimetreFonctionnelConcerne, final boolean finirActivite, final List<String> serviceIndicateurNom, final List<Integer> serviceIndicateurValeur) {

        // Récupération de la liste_view à remplir
        ListView listViewServices = (ListView) ((Activity) context).findViewById(R.id.listViewNavigation);

        final List<Service> listeAAfficher = utilisateurConnecte.getServicesUtilisateurParPerimetreFonctionnel(perimetreFonctionnelConcerne);

        Collections.sort(listeAAfficher);

        final ServiceAdapter adapter = new ServiceAdapter(context, listeAAfficher, serviceIndicateurNom, serviceIndicateurValeur);

        listViewServices.setAdapter(adapter);
        SearchView searchView = (SearchView) ((Activity) context).findViewById(R.id.barreDeRecherche);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
        });

        // Gestion des clics sur les services
        listViewServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Récupération du service sélectionné
                Service serviceSelectionne = adapter.getItem(position);

                // Appeler l'activité correspondante au service sélectionné
                Intent intentVersService = new Intent(context, recupererActiviteCorrespondanteAUnService(serviceSelectionne));

                if (finirActivite) {
                    // Ici on supprimer toutes les activités courantes et on relance l'activité de liste des périmètres fonctionnels
                    ((Activity) context).finishAffinity();
                    Intent intentRedemmarrageListePerimetres = new Intent(context, NavigationActivity.class);
                    intentRedemmarrageListePerimetres.putExtra("utilisateurConnecteID", utilisateurConnecte.getId());
                    context.startActivity(intentRedemmarrageListePerimetres);
                }
                // Récupération des éléments à transmettre à la prochaine activité
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                extras.putInt("serviceSelectionneID", serviceSelectionne.getId()); //!\ Il est nécessaire de transmettre cet élément pour gérer les services non disponible avec une seule activité
                intentVersService.putExtras(extras);
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                };

                // Appel de la prochaine activité
                context.startActivity(intentVersService);
            }
        });
    }


    public static Class recupererActiviteCorrespondanteAUnService(Service serviceConcerne) {
        Class activiteDemandee = ServiceEnCreationActivity.class;
        switch (serviceConcerne.getNom()) {
            /*
            * Pharmacie
            * */
            case "Quarantaine":
                return ServiceQuarantaineActivity.class;
            case "Verrou Pharmacie Préparation externe":
                return ServiceVerrouPharmacieActivity.class;
            case "Verrou Pharmacie Préparation Externe":
                return ServiceVerrouPharmacieActivity.class;
            case "Verrou Pharmacie Préparation interne":
                return ServiceVerrouPharmacieInterneActivity.class;
            /*
            * Magasinier
            * */
            case "Inventaire scanner":
                return ServiceInventaireScannerActivity.class;
            case "Réception PUI":
                return ServiceReceptionPuiActivity.class;
            case "Réception PAD":
                return ServiceReceptionPadActivity.class;
            case "Contrôle des retours":
                //return ServiceControleRetoursActivity.class;
                return ServiceControleRetoursScanneeActivity.class;
            case "Contrôle des retours Scan":
                return ServiceControleRetoursScanneeActivity.class;
            case "Retour PUI":
                return ServiceRetourPUIActivity.class;
            case "Retour Frs":
                return ServiceRetourFournisseurActivity.class;
            case "Destruction":
                return ServiceDestructionActivity.class;
            case "Plan de Placements":
                //return ServicePlanDePlacementActivity.class;
                return ListeProduitsPlanDePlacementActivity.class;
            case "Identification Par Scan":
                //return ServiceIdentificationParScanActivity.class;
                return ListeProduitsIdentificationParScanActivity.class;
            case "Zones et Emplacements":
                return ServiceZonesEtEmplacementsActivity.class;
            case "Réception Scannée":
                return ServiceReceptionScanneeActivity.class;
            /*
            * Chauffeur
            * */
            case "Livraison":
                //return DepotSelecteurLivraison.class;
                //return ServiceLivraisonActivity.class;
                return ListePointDeLivraison.class;
            /*
            * Infirmiers
            * */
            case "Utiliser":
                return ServiceUtiliserActivity.class;
            case "Demande Particuliere":
                return ServiceDemandeParticuliereActivity.class;
            case "Dotation Service":
                return ServiceDotationServiceActivity.class;
            case "Demande Réassort":
                return ServiceDemandeReassortActivity.class;
            case "Demande Dotation PAD":
                return ServiceDemandeDotationPADActivity.class;
            case "Demande Protocole PAD":
                return ServiceDemandeProtocolePADActivity.class;
            case "Retour Demandé":
                return ServiceRetourDemandeActivity.class;
            /*
            * Commun
            * */
            case "Médicament au Livret":
                return ServiceMedicamentAuLivretActivity.class;
            case "Dispositif au Livret":
                return ServiceDispositifAuLivretActivity.class;
            case "Stock":
                return ServiceStockActivity.class;
            case "Notifications":
                return ServiceNotificationsActivity.class;
            case "Réseaux":
                return ServiceParametresServeurActivity.class;
            case "Notification":
                return ServiceNotificationsActivity.class;
            case "ConnexionDirecte":
                return ServiceConnexionDirecteActivity.class;
            case "Serialisation":
                return ServiceSerialisationActivity.class;
            case "Utilisation":
                return ServiceParametreUtilisateurActivity.class;
            case "Mail":
                return TestMailActivity.class;
            case "CGU":
                return CguActivity.class;
            /*
            *Patient
             */
            case "Commander":
                return ServicePADActivity.class;
            /*
            * Multi-Perimetre
            * */
            case "Préparation PAD":
                return ServicePreparationPadActivity.class;
            case "Préparation PAD Scan":
                return ServicePreparationPadScanneeActivity.class;
            case "Préparation UF":
                return ServicePreparationPufActivity.class;
            case "Préparation UF Scan":
                return ServicePreparationPufScanneeActivity.class;
            case "Demande PleinVide":
                return ServiceDemandePleinVideActivity.class;
            case "Demande Dotation Globale":
                return ServiceDemandeDotationGlobaleActivity.class;
            /*
             *Commun
             */
            case "Actions utilisateurs":
                return ServiceActionUtilisateurActivity.class;

        }

        return activiteDemandee;
    }
}
