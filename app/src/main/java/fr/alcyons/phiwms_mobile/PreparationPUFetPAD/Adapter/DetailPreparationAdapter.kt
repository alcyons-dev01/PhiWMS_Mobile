package fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne
import fr.alcyons.phiwms_mobile.R

class DetailPreparationAdapter (
    context: Context,
    private var liste: MutableList<PH_Preparation_Ligne>

) : ArrayAdapter<PH_Preparation_Ligne>(context, 0, liste) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.row_preparation, parent, false)

        val item = liste[position]

        val nomProduit         = view.findViewById<TextView>(R.id.nomProduit)
        val refProduit         = view.findViewById<TextView>(R.id.refProduit)
        val fournisseurProduit = view.findViewById<TextView>(R.id.fournisseurProduit)
        val lotProduit         = view.findViewById<TextView>(R.id.lotProduit)
        val serieProduit         = view.findViewById<TextView>(R.id.serieProduit)
        val peremptionProduit  = view.findViewById<TextView>(R.id.peremptionProduit)
        val quantitePreparer = view.findViewById<TextView>(R.id.quantitePreparer)
        val quantiteAPreparer = view.findViewById<TextView>(R.id.quantiteAPreparer)
        val informationLot_LL = view.findViewById<LinearLayout>(R.id.InformationLot_LL)
        val bandeauQteAPreparer = view.findViewById<LinearLayout>(R.id.bandeauQteAPreparer)
        val bandeauQtePreparer = view.findViewById<LinearLayout>(R.id.bandeauQtePreparer)

        nomProduit.text         = item.produitDesignation
        refProduit.text         = item.produitReference
        fournisseurProduit.text = ""
        lotProduit.text         = item.lotNumero
        peremptionProduit.text  = item.peremptionDate
        serieProduit.text = item.serieNumero

        if(item.lotNumero.uppercase().contentEquals("LOT NON TRACE") || item.lotNumero.uppercase().startsWith("PHI"))
        {
            peremptionProduit.visibility = View.GONE
        }

        val dateString = item.peremptionDate

        if (dateString.isNullOrEmpty() || dateString == "0000-00-00") {
            peremptionProduit.visibility = View.GONE
        } else {
            peremptionProduit.visibility = View.VISIBLE
            val parts = dateString.split("-")
            peremptionProduit.text = "${parts[1]}/${parts[0].substring(2)}"
        }

        if(item._UID < 0)
        {
            bandeauQtePreparer.visibility = View.VISIBLE
            informationLot_LL.visibility = View.VISIBLE
            bandeauQteAPreparer.visibility = View.GONE
            quantitePreparer.text = item.qte_preparer.toString()
        }
        else{
            bandeauQtePreparer.visibility = View.GONE
            informationLot_LL.visibility = View.GONE
            bandeauQteAPreparer.visibility = View.VISIBLE
            quantiteAPreparer.text = item.qte_APreparer.toString()
        }

        return view
    }
}