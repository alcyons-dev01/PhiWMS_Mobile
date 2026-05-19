package fr.alcyons.phiwms_mobile.ControleDesRetours.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import fr.alcyons.phiwms_mobile.Classes.Retour
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_ControleRetoursAdapter_2025
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.ServiceActivity

class ControleRetourLignesFragment : Fragment()
{
    companion object
    {
        private const val ARG_LISTE = "liste"
        private const val ARG_RETOUR = "retour"

        fun newInstance(liste: ArrayList<Retour_Ligne>, retour: Retour): ControleRetourLignesFragment =
            ControleRetourLignesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LISTE, liste)
                    putSerializable(ARG_RETOUR, retour)
                }
            }
    }

    interface OnElementSelectionneListener { fun onElementSelectionne(element: Retour_Ligne) }

    private lateinit var listeRetourLigneLV: ListView
    private lateinit var adapter: Retour_Ligne_ControleRetoursAdapter_2025
    private var listener: OnElementSelectionneListener? = null

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        listener = context as? OnElementSelectionneListener
    }

    override fun onDetach()
    {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_controle_retour_lignes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        listeRetourLigneLV = view.findViewById(R.id.liste_RetourLigne_LV)
        listeRetourLigneLV.isNestedScrollingEnabled = true

        val (liste, retour) = readArguments()
        val db = (requireActivity() as? ServiceActivity)?.db ?: return
        val retourCourant = retour ?: return

        bindAdapter(liste, retourCourant, db)
        listeRetourLigneLV.setOnItemClickListener { _, _, position, _ -> listener?.onElementSelectionne(liste[position]) }
    }

    fun updateList(newListe: ArrayList<Retour_Ligne>, retour: Retour)
    {
        arguments = Bundle().apply {
            putSerializable(ARG_LISTE, newListe)
            putSerializable(ARG_RETOUR, retour)
        }
        if (!this::adapter.isInitialized) return

        val db = (requireActivity() as? ServiceActivity)?.db ?: return
        bindAdapter(newListe, retour, db)
    }

    private fun readArguments(): Pair<ArrayList<Retour_Ligne>, Retour?>
    {
        @Suppress("UNCHECKED_CAST")
        val liste = arguments?.getSerializable(ARG_LISTE) as? ArrayList<Retour_Ligne> ?: arrayListOf()
        val retour = arguments?.getSerializable(ARG_RETOUR) as? Retour
        return liste to retour
    }

    private fun bindAdapter(liste: ArrayList<Retour_Ligne>, retour: Retour, db: android.database.sqlite.SQLiteDatabase)
    {
        adapter = Retour_Ligne_ControleRetoursAdapter_2025(requireContext(), liste, db, retour)
        listeRetourLigneLV.adapter = adapter
        listeRetourLigneLV.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        listeRetourLigneLV.requestLayout()
    }
}
