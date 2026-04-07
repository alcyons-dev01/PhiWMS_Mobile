package fr.alcyons.phiwms_mobile.Fragment

import android.content.res.ColorStateList
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Inventaire.DetailInventaire_V3
import fr.alcyons.phiwms_mobile.R
import java.util.Calendar

class DetailLigneFragment : Fragment() {

    var onFermer: (() -> Unit)? = null
    var onValider: ((qte: Int, lot: String, datePeremption: String) -> Unit)? = null
    private lateinit var db: SQLiteDatabase // ton type de BDD

    companion object {
        private const val ARG_LIGNE = "ligne"

        fun newInstance(ligne: Inventaire_Ligne_Temp?) = DetailLigneFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_LIGNE, ligne)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_detail_ligne, container, false)

    fun mettreAJourLigne(ligne: Inventaire_Ligne_Temp) {
        val view = view ?: return
        view.findViewById<TextView>(R.id.emplacementLot_TV).text = ligne.emplacement
        view.findViewById<TextView>(R.id.designationReference_TV).text = ligne.designation
        view.findViewById<EditText>(R.id.quantiteComptee_ET)
            .setText(ligne.stockPhysique.toInt().toString())
        view.findViewById<EditText>(R.id.numeroLot_ET)
            .setText(ligne.lot ?: "")
        // Mettez à jour les autres champs si nécessaire
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = (requireActivity() as DetailInventaire_V3).db

        val ligne = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_LIGNE, Inventaire_Ligne_Temp::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable(ARG_LIGNE) as? Inventaire_Ligne_Temp
        } ?: return

        var conditionnement: Int = ligne.cond_Achat.toInt()
        val layoutCartonFermerLL = view.findViewById<LinearLayout>(R.id.layoutCartonFermer_LL)
        val layoutCartonOuvertLL = view.findViewById<LinearLayout>(R.id.layoutCartonOuvert_LL)
        val textCartonFermerTV = view.findViewById<TextView>(R.id.textCartonFermer_TV)
        val textCartonOuvert_TV = view.findViewById<TextView>(R.id.textCartonOuvert_TV)
        val layoutMoinsLL = view.findViewById<ImageView>(R.id.layoutMoins_LL)
        val layoutPlusLL = view.findViewById<ImageView>(R.id.layoutPlus_LL)
        val spinnerMoisDatePeremptionSP = view.findViewById<Spinner>(R.id.selecteurDateMois_SP)
        val adapterMoisPeremption: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_date_item,
            getListeMoisDatePicker()
        )
        val spinnerAnneeDatePeremptionSP = view.findViewById<Spinner>(R.id.selecteurDateAnnee_SP)
        val adapterAnneePeremption: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_date_item,
            getListeAnneeDatePicker()
        )
        val quantiteCompteeET = view.findViewById<EditText>(R.id.quantiteComptee_ET)

        //gestion du conditionnment
        textCartonFermerTV.text = textCartonFermerTV.getText().toString() + " (x" + ligne.cond_Achat.toInt() + ")"
        layoutCartonFermerLL.setOnClickListener { _: View? ->
            conditionnement = ligne.cond_Achat.toInt()
            layoutCartonFermerLL.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bleu_fonce_alcyons))
            layoutCartonOuvertLL.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            textCartonFermerTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.blanc))
            textCartonOuvert_TV.setTextColor(ContextCompat.getColor(requireContext(), R.color.bleu_fonce_alcyons))
        }
        layoutCartonOuvertLL.setOnClickListener { _: View? ->
            conditionnement = 1
            layoutCartonOuvertLL.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bleu_fonce_alcyons))
            layoutCartonFermerLL.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            textCartonFermerTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.bleu_fonce_alcyons))
            textCartonOuvert_TV.setTextColor(ContextCompat.getColor(requireContext(), R.color.blanc))
        }
        layoutPlusLL.setOnClickListener { _: View? ->
            var qteActuelle = quantiteCompteeET.text.toString().toInt()
            if(qteActuelle == -1)
                qteActuelle = 0
            qteActuelle += conditionnement
            quantiteCompteeET.setText(qteActuelle.toString())
        }
        layoutMoinsLL.setOnClickListener { _: View? ->
            var qteActuelle = quantiteCompteeET.text.toString().toInt()
            qteActuelle -= conditionnement
            if (qteActuelle < 0) qteActuelle = 0
            quantiteCompteeET.setText(qteActuelle.toString())
        }

        //gestion de la date de péremption
        adapterMoisPeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMoisDatePeremptionSP.adapter = adapterMoisPeremption
        adapterAnneePeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAnneeDatePeremptionSP.adapter = adapterAnneePeremption
        spinnerAnneeDatePeremptionSP.setSelection(3)

        if (!ligne.peremptionDate.isNullOrEmpty() && ligne.peremptionDate != "0000-00-00") {
            val parts = ligne.peremptionDate.split("-")
            val annee = parts[0] // "2026"
            val mois = parts[1].toInt() - 1 // "04" → index 3 (0-based)

            // Règle le spinner mois (index 0-based donc avril = 3)
            spinnerMoisDatePeremptionSP.setSelection(mois)

            // Règle le spinner année en cherchant la position de l'année dans l'adapter
            val positionAnnee = (0 until adapterAnneePeremption.count).indexOfFirst {
                adapterAnneePeremption.getItem(it) == annee
            }
            if (positionAnnee != -1) {
                spinnerAnneeDatePeremptionSP.setSelection(positionAnnee)
            }
        }
        else
        {
            view.findViewById<CardView>(R.id.layoutDatePeremption_CV).visibility = View.INVISIBLE
        }

        //gestion des données
        view.findViewById<TextView>(R.id.emplacementLot_TV).text = ligne.emplacement
        view.findViewById<TextView>(R.id.designationReference_TV).text = ligne.designation
        view.findViewById<EditText>(R.id.numeroLot_ET).setText(ligne.lot.toString())
        quantiteCompteeET.setText(ligne.stockPhysique.toInt().toString())

        //gestion du suivi de lot et de péremption
        val produit = ProduitOpenHelper.getProduitByID(db, ligne.produitID)
        if(!produit.isSuivi_Lot && !produit.isPeremption)
        {
            view.findViewById<CardView>(R.id.layoutLotPeremption_LL).visibility = View.GONE
        }
        else
        {
            if(!produit.isSuivi_Lot)
            {
                view.findViewById<EditText>(R.id.numeroLot_ET).isFocusable = false
                if(!produit.isPeremption)
                    view.findViewById<CardView>(R.id.layoutDatePeremption_CV).visibility = View.INVISIBLE
            }
        }

        //gestion de la fermeture
        view.findViewById<LinearLayout>(R.id.layoutFermer_LL).setOnClickListener { onFermer?.invoke() }

        //gestion de la validation
        view.findViewById<LinearLayout>(R.id.layoutValider_LL).setOnClickListener {
            val qte  = quantiteCompteeET.text.toString().toIntOrNull() ?: 0
            val lot  = view.findViewById<EditText>(R.id.numeroLot_ET).text.toString().trim()
            val date = "2025-01-01" // récupérez depuis vos spinners
            onValider?.invoke(qte, lot, date)
        }
    }

    fun getListeMoisDatePicker(): Array<String?> {
        val tableauMois = arrayOfNulls<String>(12)

        for (i in 0..11) {
            tableauMois[i] = if (i < 9) ("0" + (i + 1)) else (i + 1).toString()
        }

        return tableauMois
    }

    fun getListeAnneeDatePicker(): Array<String?> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val startYear = currentYear - 2
        val endYear = currentYear + 10

        val size = (endYear - startYear) + 1
        val tableauAnnee = arrayOfNulls<String>(size)

        for (i in 0..<size) {
            tableauAnnee[i] = (startYear + i).toString()
        }

        return tableauAnnee
    }
}