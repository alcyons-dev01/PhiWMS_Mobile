package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.DemandeParticuliere.ListeProduitActivity;
import fr.alcyons.phiwms_mobile.R;

public class DemandeParticuliereAdapater extends RecyclerView.Adapter<DemandeParticuliereAdapater.ViewHolder> {

    public List<Produit> produits;
    public List<Produit> produitsOriginal;
    public List<Integer> listQuantite;
    public List<Integer> listQuantiteOriginal;
    public LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    int selected_position = 0; //
    // data is passed into the constructor
    public DemandeParticuliereAdapater(Context context, List<Produit> data, List<Integer> listQuantite) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.produits = data;
        this.listQuantite = listQuantite;

        this.produitsOriginal = new ArrayList<>();
        this.produitsOriginal.addAll(produits);

        this.listQuantiteOriginal = new ArrayList<>();
        this.listQuantiteOriginal.addAll(listQuantite);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_dotation_globale, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Produit produit = produits.get(position);
        holder.designation.setText(produit.getDesignation_interne());
        holder.reference.setText(produit.getRef_fourni());
        holder.qteConditionnement.setText("(x"+(int)produit.getCond_distrib()+")");
        holder.qte_demander.setText(String.valueOf(listQuantite.get(position)));

        if(listQuantite.get(position) == 0)
        {
            holder.separateur.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.background_gestion_statut_action_soumis, null));
            holder.qte_demander.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.bleu_clair_alcyons,null));
        }
        else
        {
            holder.separateur.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.background_gestion_statut_action_valider, null));
            holder.qte_demander.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.vert,null));
        }

        ViewHolder finalViewHolder = holder;
        holder.qte_demander.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if(hasfocus)
                    finalViewHolder.linearLigneProduit.setBackground(context.getResources().getDrawable(R.drawable.background_plain_vert, null));
                else
                {
                    gestionQuantite(produit, finalViewHolder);
                }
            }
        });
        holder.qte_demander.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                gestionQuantite(produit, finalViewHolder);
            }
            return false;
        });
    }

    private void gestionQuantite(Produit produit, ViewHolder holder)
    {
        int quantite = 0;
        if(!holder.qte_demander.getText().toString().isEmpty())
            quantite = Integer.parseInt(holder.qte_demander.getText().toString());

        quantite = (int)Conditionnement_Calcul(quantite, (int)produit.getCond_distrib());

        holder.qte_demander.setText(String.valueOf(quantite));

        int positionReference = -1;
        for(Produit produitCourant : produitsOriginal)
        {
            positionReference ++;

            if(produitCourant.getDesignation_interne().contentEquals(produit.getDesignation_interne()) && produitCourant.getFournisseur().contentEquals(produit.getFournisseur()) && produitCourant.getRef_fourni().contentEquals(produit.getRef_fourni()))
            {
                break;
            }
        }
        listQuantiteOriginal.set(positionReference, quantite);

        int positionProduit = -1;
        for(Produit produitCourant : produits)
        {
            positionProduit ++;

            if(produitCourant.getDesignation_interne().contentEquals(produit.getDesignation_interne()) && produitCourant.getFournisseur().contentEquals(produit.getFournisseur()) && produitCourant.getRef_fourni().contentEquals(produit.getRef_fourni()))
            {
                break;
            }
        }

        listQuantite.set(positionProduit, quantite);

        if(quantite > 0)
        {
            holder.separateur.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.background_gestion_statut_action_valider, null));
            holder.qte_demander.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.vert,null));
        }
        else
        {
            holder.separateur.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.background_gestion_statut_action_soumis, null));
            holder.qte_demander.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.bleu_clair_alcyons,null));
        }
        holder.linearLigneProduit.setBackground(context.getResources().getDrawable(R.drawable.background_element_list_droit, null));
        ((ListeProduitActivity) context).gestionCompteur();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return produits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView designation;
        TextView reference;
        TextView qteConditionnement;
        public EditText qte_demander;
        ImageView separateur;
        LinearLayout linearLigneProduit;
        TextView qte_a_preparer;

        ViewHolder(View itemView) {
            super(itemView);
            designation = itemView.findViewById(R.id.designation);
            reference = itemView.findViewById(R.id.reference);
            qteConditionnement = itemView.findViewById(R.id.qteConditionnement);
            qte_demander = itemView.findViewById(R.id.qte_demander);
            qte_a_preparer = itemView.findViewById(R.id.qte_a_preparer);
            separateur = itemView.findViewById(R.id.separateur);
            linearLigneProduit = itemView.findViewById(R.id.linearLigneProduit);
            itemView.setOnClickListener(this);

            qte_a_preparer.setVisibility(View.GONE);
            qte_demander.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View view) {
            ((ListeProduitActivity) context).clickItem(view);
        }
    }

    // convenience method for getting data at click position
    public Produit getItem(int id) {
        return produits.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public double Conditionnement_Calcul(Integer qte, Integer conditionnement) {
        double qte_conditionnee = 0;

        switch (conditionnement) {
            case 0:
                qte_conditionnee = qte;
                break;
            case 1:
                qte_conditionnee = qte;
                break;
            default:
                Integer reste = mod(qte, conditionnement);
                if (reste == 0) {
                    qte_conditionnee = qte;
                } else {
                    double nb_conditionnement = Math.ceil(qte / conditionnement);
                    qte_conditionnee = (nb_conditionnement + 1) * conditionnement;
                }
                break;
        }

        return qte_conditionnee;
    }

    private int mod(int x, int y) {
        int result = x % y;
        if (result < 0)
            result += y;
        return result;
    }
}