package fr.alcyons.phiwms_mobile.Reception.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat
import fr.alcyons.phiwms_mobile.R

class DetailReceptionAdapter (
    context: Context,
    private var liste: MutableList<PH_Reliquat>

) : ArrayAdapter<PH_Reliquat>(context, 0, liste) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.row_detail_inventaire, parent, false)

        val item = liste[position]

        val nomProduit         = view.findViewById<TextView>(R.id.nomProduit)
        val refProduit         = view.findViewById<TextView>(R.id.refProduit)
        val fournisseurProduit = view.findViewById<TextView>(R.id.fournisseurProduit)
        val lotProduit         = view.findViewById<TextView>(R.id.lotProduit)
        val peremptionProduit  = view.findViewById<TextView>(R.id.peremptionProduit)
        val quantiteProduit = view.findViewById<TextView>(R.id.quantiteProduit)
        val bandeauStockSaisie_LL = view.findViewById<LinearLayout>(R.id.bandeauStockSaisie_LL)

        nomProduit.text         = item.designationCourte
        refProduit.text         = item.produit_Reference
        fournisseurProduit.text = item.fournisseurNom
        lotProduit.text         = item.lot
        peremptionProduit.text  = item.peremptionDate

        if(item.lot.uppercase().contentEquals("LOT NON TRACE") || item.lot.uppercase().startsWith("PHI"))
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

        if(item.qteLivraison.toInt() > 0)
        {
            bandeauStockSaisie_LL.visibility = View.VISIBLE
            quantiteProduit.text = item.qteLivraison.toInt().toString()
        }
        else
            bandeauStockSaisie_LL.visibility = View.GONE

        return view
    }
}