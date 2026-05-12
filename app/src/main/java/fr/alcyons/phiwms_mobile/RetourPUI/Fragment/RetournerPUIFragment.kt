package fr.alcyons.phiwms_mobile.RetourPUI.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import fr.alcyons.phiwms_mobile.Classes.Retour
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_RetourPUIAdapter
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.ServiceActivity

class RetournerPUIFragment : Fragment() {
    companion object {
        private const val ARG_LISTE = "liste"
        private const val ARG_RETOUR = "retour"
        private const val SHOULD_SHOW_QTE_ARETOURNER = true
        private const val SHOULD_AGGREGATE_BY_PRODUIT = false

        fun newInstance(
            liste: ArrayList<Retour_Ligne>,
            retour: Retour
        ): RetournerPUIFragment = RetournerPUIFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_LISTE, liste)
                putSerializable(ARG_RETOUR, retour)
            }
        }
    }

    private lateinit var listeRetourLigneLV: ListView
    private var listener: OnElementSelectionneListener? = null
    private lateinit var adapter: Retour_Ligne_RetourPUIAdapter

    interface OnElementSelectionneListener {
        fun onElementSelectionne(element: Retour_Ligne)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnElementSelectionneListener
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
        return inflater.inflate(R.layout.fragment_aretourner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeRetourLigneLV = view.findViewById(R.id.liste_RetourLigne_LV)
        listeRetourLigneLV.isNestedScrollingEnabled = true

        @Suppress("UNCHECKED_CAST")
        val liste = arguments?.getSerializable(ARG_LISTE) as? ArrayList<Retour_Ligne> ?: arrayListOf()
        val retour = arguments?.getSerializable(ARG_RETOUR) as? Retour
        val db = (requireActivity() as? ServiceActivity)?.db

        if (db != null && retour != null) {
            adapter = Retour_Ligne_RetourPUIAdapter(requireContext(), db, liste, retour, RetournerPUIFragment.SHOULD_SHOW_QTE_ARETOURNER, RetournerPUIFragment.SHOULD_AGGREGATE_BY_PRODUIT)
            listeRetourLigneLV.adapter = adapter

            listeRetourLigneLV.post {
                if (adapter.count == 0) return@post

                val maxHauteur = (400 * resources.displayMetrics.density).toInt()
                val premierItem = adapter.getView(0, null, listeRetourLigneLV)
                premierItem.measure(
                    View.MeasureSpec.makeMeasureSpec(listeRetourLigneLV.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                var hauteurTotale = premierItem.measuredHeight * adapter.count
                hauteurTotale += listeRetourLigneLV.dividerHeight * (adapter.count - 1)
                hauteurTotale += listeRetourLigneLV.paddingTop + listeRetourLigneLV.paddingBottom
                listeRetourLigneLV.layoutParams.height = hauteurTotale.coerceAtMost(maxHauteur)
                listeRetourLigneLV.requestLayout()
            }

            listeRetourLigneLV.setOnItemClickListener { _, _, position, _ ->
                listener?.onElementSelectionne(liste[position])
            }
        }
    }

    fun updateList(newListe: ArrayList<Retour_Ligne>, retour: Retour) {
        arguments = Bundle().apply {
            putSerializable(ARG_LISTE, newListe)
            putSerializable(ARG_RETOUR, retour)
        }
        if (this::adapter.isInitialized) {
            val db = (requireActivity() as? ServiceActivity)?.db
            if (db != null) {
                adapter = Retour_Ligne_RetourPUIAdapter(requireContext(), db, newListe, retour, RetournerPUIFragment.SHOULD_SHOW_QTE_ARETOURNER, RetournerPUIFragment.SHOULD_AGGREGATE_BY_PRODUIT)
                listeRetourLigneLV.adapter = adapter
            }
        }
    }
}
