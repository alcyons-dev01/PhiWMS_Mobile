package fr.alcyons.phiwms_mobile.RetourFournisseur.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.Destruction.Fragment.ADetruireFragment
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_DestructionAdapter
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_RetourFournisseurAdapter
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.Reception.Adapter.DetailReceptionAdapter

class ARetournerFournisseurFragment : Fragment() {

    interface OnElementSelectionneListener {
        fun onElementSelectionne(element: Retour_Ligne)
    }

    private var listener: OnElementSelectionneListener? = null

    companion object {
        private const val ARG_LISTE = "liste"

        fun newInstance(liste: ArrayList<Retour_Ligne>) = ARetournerFournisseurFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_LISTE, liste) }
        }
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
        return RecyclerView(requireContext()).apply {
            id = R.id.liste_RetourLigne_LV
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        val liste = arguments?.getSerializable(ARG_LISTE) as? ArrayList<Retour_Ligne> ?: arrayListOf()

        val recyclerView = view as RecyclerView
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = true   // scroll interne géré par le RV
            setHasFixedSize(false)
            adapter = Retour_Ligne_RetourFournisseurAdapter(requireContext(), liste)
        }
    }
}