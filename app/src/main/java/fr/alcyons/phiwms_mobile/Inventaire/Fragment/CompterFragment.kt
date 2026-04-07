package fr.alcyons.phiwms_mobile.Inventaire.Fragment

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp
import fr.alcyons.phiwms_mobile.Inventaire.Adapter.ACompterAdapter
import fr.alcyons.phiwms_mobile.R

class CompterFragment : Fragment() {
    interface OnElementSelectionnéListener {
        fun onElementSelectionne(element: Inventaire_Ligne_Temp) // ou ton type d'objet
    }

    private lateinit var liste_inventaireLigneTemp_LV: ListView
    private var listener: OnElementSelectionnéListener? = null
    private lateinit var db: SQLiteDatabase // ton type de BDD
    private lateinit var adapter: ACompterAdapter

    companion object {
        private const val ARG_LISTE = "liste"

        fun newInstance(liste: ArrayList<Inventaire_Ligne_Temp>) = ACompterFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_LISTE, liste)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_acompter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        liste_inventaireLigneTemp_LV = view.findViewById(R.id.liste_inventaireLigneTemp_LV)
        liste_inventaireLigneTemp_LV.isNestedScrollingEnabled = true

        // Récupère la liste passée en argument
        @Suppress("UNCHECKED_CAST")
        val liste = arguments?.getSerializable(CompterFragment.Companion.ARG_LISTE) as? ArrayList<Inventaire_Ligne_Temp>
            ?: arrayListOf()

        adapter = ACompterAdapter(requireContext(), liste)
        liste_inventaireLigneTemp_LV.adapter = adapter

        // Calcul de la hauteur réelle de la ListView
        liste_inventaireLigneTemp_LV.post {
            val maxHauteur = (400 * resources.displayMetrics.density).toInt()

            // On mesure uniquement le premier item pour estimer la hauteur de tous
            if (adapter.count == 0) return@post

            val premierItem = adapter.getView(0, null, liste_inventaireLigneTemp_LV)
            premierItem.measure(
                View.MeasureSpec.makeMeasureSpec(liste_inventaireLigneTemp_LV.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val hauteurItem = premierItem.measuredHeight

            // Multiplie par le nombre d'items
            var hauteurTotale = hauteurItem * adapter.count

            // Ajoute la hauteur des dividers
            hauteurTotale += liste_inventaireLigneTemp_LV.dividerHeight * (adapter.count - 1)

            liste_inventaireLigneTemp_LV.layoutParams.height =
                hauteurTotale.coerceAtMost(maxHauteur)
            liste_inventaireLigneTemp_LV.requestLayout()
        }

        liste_inventaireLigneTemp_LV.setOnItemClickListener { _, _, position, _ ->
            val elementSelectionne = liste[position]
            listener?.onElementSelectionne(elementSelectionne)
        }
    }
}