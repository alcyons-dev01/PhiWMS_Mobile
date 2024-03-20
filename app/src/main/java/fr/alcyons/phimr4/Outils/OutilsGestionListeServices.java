package fr.alcyons.phimr4.Outils;

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

import fr.alcyons.phimr4.ActionUtilisateur.ServiceActionUtilisateurActivity;
import fr.alcyons.phimr4.CGU.CguActivity;
import fr.alcyons.phimr4.Classes.PerimetreFonctionnel;
import fr.alcyons.phimr4.Classes.Service;
import fr.alcyons.phimr4.Classes.Utilisateur;
import fr.alcyons.phimr4.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phimr4.ControleDesRetoursScannee.ServiceControleRetoursScanneeActivity;
import fr.alcyons.phimr4.DemandeDotationGlobale.ServiceDemandeDotationGlobaleActivity;
import fr.alcyons.phimr4.DemandeDotationPAD.ServiceDemandeDotationPADActivity;
import fr.alcyons.phimr4.DemandeParticuliere.ServiceDemandeParticuliereActivity;
import fr.alcyons.phimr4.DemandePleinVide.ServiceDemandePleinVideActivity;
import fr.alcyons.phimr4.DemandeProtocolePAD.ServiceDemandeProtocolePADActivity;
import fr.alcyons.phimr4.DemandeReassort.ServiceDemandeReassortActivity;
import fr.alcyons.phimr4.Destruction.ServiceDestructionActivity;
import fr.alcyons.phimr4.DispositifAuLivret.ServiceDispositifAuLivretActivity;
import fr.alcyons.phimr4.DotationService.ServiceDotationServiceActivity;
import fr.alcyons.phimr4.IdentificationParScan.ListeProduitsIdentificationParScanActivity;
import fr.alcyons.phimr4.InventaireScanner.ServiceInventaireScannerActivity;
import fr.alcyons.phimr4.ListViewAdapters.ServiceAdapter;
import fr.alcyons.phimr4.Livraison.DepotSelecteurLivraison;
import fr.alcyons.phimr4.Livraison.ListePointDeLivraison;
import fr.alcyons.phimr4.Livraison.ServiceLivraisonActivity;
import fr.alcyons.phimr4.MedicamentAuLivret.ServiceMedicamentAuLivretActivity;
import fr.alcyons.phimr4.Navigation.NavigationActivity;
import fr.alcyons.phimr4.Notifications.ServiceNotificationsActivity;
import fr.alcyons.phimr4.PAD.ServicePADActivity;
import fr.alcyons.phimr4.ParametresServeur.ServiceParametresServeurActivity;
import fr.alcyons.phimr4.ParametresUtilisateur.ServiceParametreUtilisateurActivity;
import fr.alcyons.phimr4.PlanDePlacement.ListeProduitsPlanDePlacementActivity;
import fr.alcyons.phimr4.PreparationPUFetPAD.ServicePreparationPadActivity;
import fr.alcyons.phimr4.PreparationPUFetPAD.ServicePreparationPufActivity;
import fr.alcyons.phimr4.PreparationPUFetPADScannee.ServicePreparationPadScanneeActivity;
import fr.alcyons.phimr4.PreparationPUFetPADScannee.ServicePreparationPufScanneeActivity;
import fr.alcyons.phimr4.Quarantaine.ServiceQuarantaineActivity;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ReceptionPAD.ServiceReceptionPadActivity;
import fr.alcyons.phimr4.ReceptionPUI.ServiceReceptionPuiActivity;
import fr.alcyons.phimr4.ReceptionScanne.ServiceReceptionScanneeActivity;
import fr.alcyons.phimr4.RetourDemande.ServiceRetourDemandeActivity;
import fr.alcyons.phimr4.RetourFournisseur.ServiceRetourFournisseurActivity;
import fr.alcyons.phimr4.RetourPUI.ServiceRetourPUIActivity;
import fr.alcyons.phimr4.Serialisation.ServiceSerialisationActivity;
import fr.alcyons.phimr4.ServiceEnCreationActivity;
import fr.alcyons.phimr4.Stock.ServiceStockActivity;
import fr.alcyons.phimr4.TestMail.TestMailActivity;
import fr.alcyons.phimr4.Utiliser.ServiceUtiliserActivity;
import fr.alcyons.phimr4.VerrouPharmacie.ServiceVerrouPharmacieActivity;
import fr.alcyons.phimr4.VerrouPharmacieInterne.ServiceVerrouPharmacieInterneActivity;
import fr.alcyons.phimr4.ZonesEtEmplacements.ServiceZonesEtEmplacementsActivity;

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
