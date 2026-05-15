package fr.alcyons.phiwms_mobile.RetourPUI.Fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.alcyons.phiwms_mobile.Classes.Retour
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_RetourPUIAdapter
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.ServiceActivity

class RetournerPUIFragment : Fragment() {

    companion object {
        private const val ARG_LISTE = "liste"
        private const val ARG_RETOUR = "retour"
        private const val SHOULD_SHOW_QTE_ARETOURNER = false
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

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Retour_Ligne_RetourPUIAdapter
    private var listener: OnElementSelectionneListener? = null

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

        recyclerView = view as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.setHasFixedSize(false)

        val (liste, retour) = readArguments()
        val db = (requireActivity() as? ServiceActivity)?.db ?: return
        val retourCourant = retour ?: return

        bindAdapter(liste, retourCourant, db)
    }

    fun updateList(newListe: ArrayList<Retour_Ligne>, retour: Retour) {
        arguments = Bundle().apply {
            putSerializable(ARG_LISTE, newListe)
            putSerializable(ARG_RETOUR, retour)
        }
        if (!this::adapter.isInitialized) return
        val db = (requireActivity() as? ServiceActivity)?.db ?: return
        bindAdapter(newListe, retour, db)
    }

    fun scrollToPosition(position: Int) {
        if (!this::adapter.isInitialized || position < 0 || position >= adapter.itemCount) return
        recyclerView.smoothScrollToPosition(position)
    }

    private fun readArguments(): Pair<ArrayList<Retour_Ligne>, Retour?> {
        @Suppress("UNCHECKED_CAST")
        val liste = arguments?.getSerializable(ARG_LISTE) as? ArrayList<Retour_Ligne> ?: arrayListOf()
        val retour = arguments?.getSerializable(ARG_RETOUR) as? Retour
        return liste to retour
    }

    private fun bindAdapter(liste: ArrayList<Retour_Ligne>, retour: Retour, db: SQLiteDatabase) {
        adapter = Retour_Ligne_RetourPUIAdapter(
            requireContext(),
            db,
            liste,
            retour,
            SHOULD_SHOW_QTE_ARETOURNER,
            SHOULD_AGGREGATE_BY_PRODUIT
        ) { element -> listener?.onElementSelectionne(element) }

        recyclerView.adapter = adapter
    }
}