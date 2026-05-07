package fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light
import fr.alcyons.phiwms_mobile.R

class LotAdapter(
    context: Context,
    private val liste: List<Stock_Lot_Emplacement_Light>
) : ArrayAdapter<Stock_Lot_Emplacement_Light>(context, R.layout.spinner_item_lot, liste) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item_lot, parent, false)

        val item = liste[position]

        // Cas spécial "Ajouter un lot"
        if (item.lot == "Ajouter un lot") {
            view.findViewById<TextView>(R.id.lotNumero_TV).text = "＋ Ajouter un lot"
            view.findViewById<TextView>(R.id.lotNumero_TV).setTextColor(
                ContextCompat.getColor(context, R.color.bleu_clair_alcyons)
            )
            view.findViewById<TextView>(R.id.peremption_TV).text = ""
            view.findViewById<TextView>(R.id.zone_TV).text = ""
            view.findViewById<TextView>(R.id.emplacement_TV).text = ""
            view.findViewById<TextView>(R.id.quantite_TV).text = ""
            view.findViewById<CardView>(R.id.cardViewQuantite).visibility = View.GONE
            view.findViewById<View>(R.id.separateurVertical_V).visibility = View.GONE
            return view
        }

        if (item.getLot() == "Sélectionner un lot") {
            view.findViewById<TextView>(R.id.lotNumero_TV).apply {
                text = "Sélectionner un lot"
                setTextColor(ContextCompat.getColor(context, R.color.bleu_fonce_alcyons))
                alpha = 0.5f
            }
            view.findViewById<TextView>(R.id.peremption_TV).text = ""
            view.findViewById<TextView>(R.id.zone_TV).text = ""
            view.findViewById<TextView>(R.id.emplacement_TV).text = ""
            view.findViewById<TextView>(R.id.quantite_TV).text = ""
            view.findViewById<CardView>(R.id.cardViewQuantite).visibility = View.GONE
            view.findViewById<View>(R.id.separateurVertical_V).visibility = View.GONE
            return view
        }

        // Cas normal
        view.findViewById<TextView>(R.id.lotNumero_TV).text = item.lot
        view.findViewById<TextView>(R.id.peremption_TV).text = formatPeremption(item.peremptionDate)
        view.findViewById<TextView>(R.id.zone_TV).text = item.zone ?: ""
        view.findViewById<TextView>(R.id.emplacement_TV).text = item.emplacement ?: ""
        view.findViewById<TextView>(R.id.quantite_TV).text = item.qte.toInt().toString()

        return view
    }
    private fun formatPeremption(date: String?): String {
        if (date.isNullOrEmpty() || date == "0000-00-00") return ""
        val parts = date.split("-")
        return if (parts.size == 3) "${parts[1]}/${parts[0].substring(2)}" else date
    }
}