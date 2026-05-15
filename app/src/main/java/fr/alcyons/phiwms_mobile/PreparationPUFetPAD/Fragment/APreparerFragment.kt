package fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Adapter.DetailPreparationAdapter
import fr.alcyons.phiwms_mobile.R

class APreparerFragment : Fragment() {

    interface OnElementSelectionneListener {
        fun onElementSelectionne(element: PH_Preparation_Ligne)
    }

    private var listener: OnElementSelectionneListener? = null
    private lateinit var adapter: DetailPreparationAdapter  // à adapter en RecyclerView.Adapter

    companion object {
        private const val ARG_LISTE = "liste"

        fun newInstance(liste: ArrayList<PH_Preparation_Ligne>) = APreparerFragment().apply {
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
            id = R.id.liste_PH_Preparation_Ligne_RV
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        val liste = arguments?.getSerializable(ARG_LISTE) as? ArrayList<PH_Preparation_Ligne> ?: arrayListOf()

        val recyclerView = view as RecyclerView
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = true   // scroll interne géré par le RV
            setHasFixedSize(false)
            adapter = DetailPreparationAdapter(requireContext(), liste) { element ->
                listener?.onElementSelectionne(element)
            }
        }
    }
}