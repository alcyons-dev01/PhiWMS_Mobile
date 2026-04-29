package fr.alcyons.phiwms_mobile.Reception.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_DestructionAdapter
import fr.alcyons.phiwms_mobile.R
class ADetruireFragment : Fragment()
{
    companion object
    {
        private const val ARG_LISTE = "liste"
        fun newInstance(liste: ArrayList<Retour_Ligne>): ADetruireFragment = ADetruireFragment().apply { this.arguments = Bundle().apply { putSerializable(ARG_LISTE, liste) } }
    }

    private lateinit var liste_RetourLigne_LV: ListView
    private var listener: OnElementSelectionneListener? = null
    private lateinit var adapter: Retour_Ligne_DestructionAdapter

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { return inflater.inflate(R.layout.fragment_adetruire, container, false) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        this.liste_RetourLigne_LV = view.findViewById(R.id.liste_RetourLigne_LV)
        this.liste_RetourLigne_LV.isNestedScrollingEnabled = true

        // Récupère la liste passée en argument
        @Suppress("UNCHECKED_CAST")
        val liste = arguments?.getSerializable(ARG_LISTE) as? ArrayList<Retour_Ligne> ?: arrayListOf()
        this.adapter = Retour_Ligne_DestructionAdapter(requireContext(), liste)
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
            this.liste_RetourLigne_LV.layoutParams.height = hauteurTotale.coerceAtMost(maxHauteur)
            this.liste_RetourLigne_LV.requestLayout()
        }

        this.liste_RetourLigne_LV.setOnItemClickListener { _, _, position, _ ->
            val elementSelectionne = liste[position]
            this.listener?.onElementSelectionne(elementSelectionne)
        }
    }
}
