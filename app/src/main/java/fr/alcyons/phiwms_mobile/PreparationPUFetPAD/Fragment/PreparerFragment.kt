package fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Adapter.DetailPreparationAdapter
import fr.alcyons.phiwms_mobile.R

class PreparerFragment  : Fragment() {
    interface OnElementSelectionneListener {
        fun onElementSelectionne(element: PH_Preparation_Ligne) // ou ton type d'objet
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? PreparerFragment.OnElementSelectionneListener
    }

    private lateinit var liste_PH_Preparation_Ligne_LV: ListView
    private var listener: OnElementSelectionneListener? = null
    private lateinit var db: SQLiteDatabase // ton type de BDD
    private lateinit var adapter: DetailPreparationAdapter

    companion object {
        private const val ARG_LISTE = "liste"

        fun newInstance(liste: ArrayList<PH_Preparation_Ligne>) = PreparerFragment().apply {
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
        return inflater.inflate(R.layout.fragment_apreparer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        liste_PH_Preparation_Ligne_LV = view.findViewById(R.id.liste_PH_Preparation_Ligne_LV)
        liste_PH_Preparation_Ligne_LV.isNestedScrollingEnabled = true

        // Récupère la liste passée en argument
        @Suppress("UNCHECKED_CAST")
        val liste = arguments?.getSerializable(PreparerFragment.Companion.ARG_LISTE) as? ArrayList<PH_Preparation_Ligne>
            ?: arrayListOf()

        adapter = DetailPreparationAdapter(requireContext(), liste)
        liste_PH_Preparation_Ligne_LV.adapter = adapter

        // Calcul de la hauteur réelle de la ListView
        liste_PH_Preparation_Ligne_LV.post {
            val maxHauteur = (400 * resources.displayMetrics.density).toInt()

            // On mesure uniquement le premier item pour estimer la hauteur de tous
            if (adapter.count == 0) return@post

            val premierItem = adapter.getView(0, null, liste_PH_Preparation_Ligne_LV)
            premierItem.measure(
                View.MeasureSpec.makeMeasureSpec(liste_PH_Preparation_Ligne_LV.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val hauteurItem = premierItem.measuredHeight

            // Multiplie par le nombre d'items
            var hauteurTotale = hauteurItem * adapter.count

            // Ajoute la hauteur des dividers
            hauteurTotale += liste_PH_Preparation_Ligne_LV.dividerHeight * (adapter.count - 1)

            liste_PH_Preparation_Ligne_LV.layoutParams.height =
                hauteurTotale.coerceAtMost(maxHauteur)
            liste_PH_Preparation_Ligne_LV.requestLayout()
        }

        liste_PH_Preparation_Ligne_LV.setOnItemClickListener { _, _, position, _ ->
            val elementSelectionne = liste[position]
            listener?.onElementSelectionne(elementSelectionne)
        }
    }
}