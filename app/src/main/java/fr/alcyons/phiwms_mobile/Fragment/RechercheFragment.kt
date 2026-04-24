package fr.alcyons.phiwms_mobile.Fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.Fragment
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp
import fr.alcyons.phiwms_mobile.Interfaces.DatabaseProvider
import fr.alcyons.phiwms_mobile.Inventaire.DetailInventaire_V3
import fr.alcyons.phiwms_mobile.Inventaire.Fragment.ACompterFragment
import fr.alcyons.phiwms_mobile.R

class RechercheFragment : Fragment() {
    interface OnElementRechercheListener {
        fun onElementRechercher(element: Int) // ou ton type d'objet
    }

    private lateinit var resultatsLV: ListView
    private lateinit var adapter: ArrayAdapter<String> // ou ton adapter custom
    private var listener: OnElementRechercheListener? = null
    private lateinit var db: SQLiteDatabase // ton type de BDD

    companion object {
        private const val ARG_LIGNE = "ligne"

        fun newInstance(ligne: Inventaire_Ligne_Temp?) = RechercheFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_LIGNE, ligne)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? RechercheFragment.OnElementRechercheListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recherche, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultatsLV = view.findViewById(R.id.liste_reference_LV)
        resultatsLV.isNestedScrollingEnabled = true

        adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        resultatsLV.adapter = adapter
        db = (requireActivity() as DatabaseProvider).db

        resultatsLV.setOnItemClickListener { _, _, position, _ ->
            val elementSelectionne = adapter.getItem(position)
            val produitIdentifier =
                ProduitOpenHelper.getUnProduitByDesignation(db, elementSelectionne)
            if (produitIdentifier != null)
                elementSelectionne?.let { listener?.onElementRechercher(produitIdentifier.iD_produit) }
        }
    }

    fun lancerRecherche(query: String, typeDoc : String = "",numDoc : String = "") {
        var resultats = listOf<String>()
        when(typeDoc)
        {
            "reception"->  resultats = PH_ReliquatOpenHelper.getDesignationReliquatByNumero(db, query, numDoc)
            "preparation"-> resultats = ProduitOpenHelper.getProduitByDesignation(db, query)
            else -> resultats = ProduitOpenHelper.getProduitByDesignation(db, query)
        }

        adapter.clear()
        adapter.addAll(resultats)
        adapter.notifyDataSetChanged()

        resultatsLV.post {
            // Vérifie que le fragment est bien attaché avant de continuer
            if (!isAdded || context == null) return@post

            val maxHauteur = (300 * resources.displayMetrics.density).toInt()

            if (adapter.count == 0) return@post

            val premierItem = adapter.getView(0, null, resultatsLV)
            premierItem.measure(
                View.MeasureSpec.makeMeasureSpec(resultatsLV.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val hauteurItem = premierItem.measuredHeight
            var hauteurTotale = hauteurItem * adapter.count
            hauteurTotale += resultatsLV.dividerHeight * (adapter.count - 1)

            resultatsLV.layoutParams.height = hauteurTotale.coerceAtMost(maxHauteur)
            resultatsLV.requestLayout()

            (activity as? DetailInventaire_V3)?.ajusterHauteurRecherche(
                hauteurTotale.coerceAtMost(maxHauteur)
            )
        }
    }

    fun viderListe() {
        adapter.clear()
        resultatsLV.layoutParams.height = 0
        resultatsLV.requestLayout()
        (activity as? DetailInventaire_V3)?.ajusterHauteurRecherche(0)
    }
}