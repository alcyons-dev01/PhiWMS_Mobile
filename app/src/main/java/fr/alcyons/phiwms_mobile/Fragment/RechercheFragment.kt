package fr.alcyons.phiwms_mobile.Fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp
import fr.alcyons.phiwms_mobile.Interfaces.DatabaseProvider
import fr.alcyons.phiwms_mobile.Interfaces.RechercheAdjustable
import fr.alcyons.phiwms_mobile.R

class RechercheFragment : Fragment() {

    interface OnElementRechercheListener {
        fun onElementRechercher(element: Int)
    }

    private lateinit var resultatsRV: RecyclerView
    private lateinit var adapter: ResultatRechercheAdapter
    private var listener: OnElementRechercheListener? = null
    private lateinit var db: SQLiteDatabase

    companion object {
        private const val ARG_LIGNE = "ligne"

        fun newInstance(ligne: Inventaire_Ligne_Temp?) = RechercheFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_LIGNE, ligne) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnElementRechercheListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recherche, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = (requireActivity() as DatabaseProvider).db

        adapter = ResultatRechercheAdapter(mutableListOf()) { designation ->
            val produit = ProduitOpenHelper.getUnProduitByDesignation(db, designation)
            if (produit != null) listener?.onElementRechercher(produit.iD_produit)
        }

        resultatsRV = view.findViewById(R.id.liste_reference_RV)
        resultatsRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = true
            adapter = this@RechercheFragment.adapter
        }
    }

    fun lancerRecherche(query: String, typeDoc: String = "", numDoc: String = "") {
        val resultats = when (typeDoc) {
            "reception"        -> PH_ReliquatOpenHelper.getDesignationReliquatByNumero(db, query, numDoc)
            "preparation",
            "retourFournisseur",
            "destruction",
            "retourPUI"        -> ProduitOpenHelper.getProduitByDesignation(db, query)
            else               -> ProduitOpenHelper.getProduitByDesignation(db, query)
        }

        adapter.mettreAJourListe(resultats)

        // Calcul hauteur : hauteur d'un item × nb résultats, plafonnée à 300dp
        resultatsRV.post {
            if (!isAdded || context == null) return@post

            val maxHauteur = (resources.displayMetrics.heightPixels * 0.25).toInt()
            val itemHauteur = (48 * resources.displayMetrics.density).toInt() // hauteur standard item texte
            val hauteurTotale = (itemHauteur * adapter.itemCount).coerceAtMost(maxHauteur)

            resultatsRV.layoutParams.height = hauteurTotale
            resultatsRV.requestLayout()

            (activity as? RechercheAdjustable)?.ajusterHauteurRecherche(hauteurTotale)
        }
    }

    fun viderListe() {
        adapter.mettreAJourListe(emptyList())
        resultatsRV.layoutParams.height = 0
        resultatsRV.requestLayout()
        (activity as? RechercheAdjustable)?.ajusterHauteurRecherche(0)
    }
}

class ResultatRechercheAdapter(
    private val liste: MutableList<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ResultatRechercheAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val texte: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.texte.text = liste[position]
        holder.itemView.setOnClickListener { onItemClick(liste[position]) }
    }

    override fun getItemCount(): Int = liste.size

    fun mettreAJourListe(nouvelleListe: List<String>) {
        liste.clear()
        liste.addAll(nouvelleListe)
        notifyDataSetChanged()
    }
}