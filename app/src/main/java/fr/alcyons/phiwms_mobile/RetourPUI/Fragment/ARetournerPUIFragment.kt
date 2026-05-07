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

class ARetournerPUIFragment : Fragment()
{
    companion object
    {
        private const val ARG_LISTE = "liste"
        private const val ARG_RETOUR = "retour"

        @JvmStatic
        fun newInstance(
            liste: ArrayList<Retour_Ligne>,
            retour: Retour
        ): ARetournerPUIFragment = ARetournerPUIFragment().apply {
            this.arguments = Bundle().apply {
                putSerializable(ARG_LISTE, liste)
                putSerializable(ARG_RETOUR, retour)
            }
        }
    }

    private lateinit var liste_RetourLigne_LV: ListView
    private var listener: OnElementSelectionneListener? = null
    private lateinit var adapter: Retour_Ligne_RetourPUIAdapter

    interface OnElementSelectionneListener { fun onElementSelectionne(element: Retour_Ligne) }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        this.listener = context as? OnElementSelectionneListener
    }

    override fun onDetach()
    {
        super.onDetach()
        this.listener = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_aretourner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        this.liste_RetourLigne_LV = view.findViewById(R.id.liste_RetourLigne_LV)
        this.liste_RetourLigne_LV.isNestedScrollingEnabled = true

        // Récupère la liste passée en argument
        @Suppress("UNCHECKED_CAST")
        val liste = arguments?.getSerializable(ARG_LISTE) as? ArrayList<Retour_Ligne> ?: arrayListOf()
        @Suppress("UNCHECKED_CAST")
        val retour = arguments?.getSerializable(ARG_RETOUR) as? Retour

        // Get the db from the parent activity
        val activity = requireActivity() as? ServiceActivity
        val db = activity?.db

        if (db != null && retour != null) {
            this.adapter = Retour_Ligne_RetourPUIAdapter(requireContext(), db, liste, retour)
            this.liste_RetourLigne_LV.adapter = this.adapter

            // Calcul de la hauteur réelle de la ListView
            this.liste_RetourLigne_LV.post {
                if (this.adapter.count == 0) return@post

                val maxHauteur = (400 * resources.displayMetrics.density).toInt()
                // On mesure uniquement le premier item pour estimer la hauteur de tous
                val premierItem = this.adapter.getView(0, null, this.liste_RetourLigne_LV)
                premierItem.measure(
                    View.MeasureSpec.makeMeasureSpec(this.liste_RetourLigne_LV.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                val hauteurItem = premierItem.measuredHeight
                // Multiplie par le nombre d'items
                var hauteurTotale = hauteurItem * this.adapter.count
                // Ajoute la hauteur des dividers
                hauteurTotale += this.liste_RetourLigne_LV.dividerHeight * (adapter.count - 1)
                // Ajoute le padding (top + bottom)
                hauteurTotale += (this.liste_RetourLigne_LV.paddingTop + this.liste_RetourLigne_LV.paddingBottom)
                this.liste_RetourLigne_LV.layoutParams.height = hauteurTotale.coerceAtMost(maxHauteur)
                this.liste_RetourLigne_LV.requestLayout()
            }

            this.liste_RetourLigne_LV.setOnItemClickListener { _, _, position, _ ->
                val elementSelectionne = liste[position]
                this.listener?.onElementSelectionne(elementSelectionne)
            }
        }
    }

    fun scrollToPosition(position: Int)
    {
        if (!this::adapter.isInitialized || position < 0 || position >= this.adapter.count) return
        this.liste_RetourLigne_LV.smoothScrollToPosition(position)
    }

    fun updateList(newListe: ArrayList<Retour_Ligne>, retour: Retour)
    {
        this.arguments = Bundle().apply {
            putSerializable(ARG_LISTE, newListe)
            putSerializable(ARG_RETOUR, retour)
        }
        if (this::adapter.isInitialized) {
            val activity = requireActivity() as? ServiceActivity
            val db = activity?.db
            if (db != null) {
                this.adapter = Retour_Ligne_RetourPUIAdapter(requireContext(), db, newListe, retour)
                this.liste_RetourLigne_LV.adapter = this.adapter
            }
        }
    }
}
