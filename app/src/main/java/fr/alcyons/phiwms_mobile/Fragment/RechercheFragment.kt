package fr.alcyons.phiwms_mobile.Fragment

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
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp
import fr.alcyons.phiwms_mobile.Inventaire.DetailInventaire_V3
import fr.alcyons.phiwms_mobile.R

class RechercheFragment : Fragment() {
    interface OnElementSelectionnéListener {
        fun onElementSelectionne(element: Inventaire_Ligne_Temp) // ou ton type d'objet
    }
    private lateinit var barreDeRecherche: EditText
    private lateinit var resultatsLV: ListView
    private lateinit var effacerRechercher_IV: ImageView
    private lateinit var adapter: ArrayAdapter<String> // ou ton adapter custom
    private var listener: OnElementSelectionnéListener? = null
    private lateinit var db: SQLiteDatabase // ton type de BDD

    companion object {
        private const val ARG_LIGNE = "ligne"

        fun newInstance(ligne: Inventaire_Ligne_Temp?) = RechercheFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_LIGNE, ligne)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recherche, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barreDeRecherche = view.findViewById(R.id.barreDeRecherche_ET)
        resultatsLV = view.findViewById(R.id.liste_reference_LV)
        effacerRechercher_IV = view.findViewById(R.id.effacerRechercher_IV)
        resultatsLV.isNestedScrollingEnabled = true
        // Liste vide au départ
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        resultatsLV.adapter = adapter
        db = (requireActivity() as DetailInventaire_V3).db

        effacerRechercher_IV.setOnClickListener { barreDeRecherche.text.clear() }

        // Écoute la frappe dans la barre de recherche
        barreDeRecherche.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    lancerRecherche(query)
                } else {
                    adapter.clear() // Vide la liste si le champ est vide
                }
            }
        })

        // Clic sur un élément de la liste
        resultatsLV.setOnItemClickListener { _, _, position, _ ->
            val elementSelectionne = adapter.getItem(position)


            //elementSelectionne?.let { listener?.onElementSelectionne(it) }
        }
    }

    private fun lancerRecherche(query: String) {
        // Lancer ta requête BDD ici (Room, SQLite...)
        // Exemple fictif :
        val resultats = ProduitOpenHelper.getProduitByDesignation(db, query) // à remplacer

        adapter.clear()
        adapter.addAll(resultats)
        adapter.notifyDataSetChanged()
    }
}