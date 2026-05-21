package fr.alcyons.phiwms_mobile.ViewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Commande;

/**
 * Détient l'état de la liste de réceptions PUI.
 * Avant refactoring : commandeList, commandeListBase, listeFournisseurReception
 * étaient des champs de l'Activity et mutés depuis onResume(), le listener réseau
 * et le listener de l'AutoComplete → impossibles à tester.
 */
public class ReceptionPuiViewModel extends ViewModel {

    private final MutableLiveData<List<Commande>> commandesVisibles = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> fournisseurs = new MutableLiveData<>(new ArrayList<>());

    private List<Commande> toutesLesCommandes = new ArrayList<>();

    public LiveData<List<Commande>> getCommandesVisibles() { return commandesVisibles; }
    public LiveData<List<String>> getFournisseurs() { return fournisseurs; }

    /** Initialise la liste complète (appelé après chargement API ou BDD). */
    public void setCommandes(List<Commande> commandes) {
        toutesLesCommandes = new ArrayList<>(commandes);
        filtrerParFournisseur("Tous les fournisseurs");
        construireFournisseurs();
    }

    public void filtrerParFournisseur(String fournisseur) {
        List<Commande> filtre = new ArrayList<>();
        if ("Tous les fournisseurs".equals(fournisseur)) {
            filtre.addAll(toutesLesCommandes);
        } else {
            for (Commande c : toutesLesCommandes) {
                if (c.getFournisseur().equals(fournisseur)) {
                    filtre.add(c);
                }
            }
        }
        filtre.sort(Comparator.comparing(Commande::getNumero));
        commandesVisibles.setValue(filtre);
    }

    private void construireFournisseurs() {
        List<String> liste = new ArrayList<>();
        liste.add("Tous les fournisseurs");
        for (Commande c : toutesLesCommandes) {
            if (!liste.contains(c.getFournisseur())) {
                liste.add(c.getFournisseur());
            }
        }
        // Trie tout sauf le premier élément
        List<String> sansEntete = liste.subList(1, liste.size());
        Collections.sort(sansEntete);

        List<String> result = new ArrayList<>();
        result.add("Tous les fournisseurs");
        result.addAll(sansEntete);
        fournisseurs.setValue(result);
    }

    public boolean estVide() {
        List<Commande> liste = commandesVisibles.getValue();
        return liste == null || liste.isEmpty();
    }
}