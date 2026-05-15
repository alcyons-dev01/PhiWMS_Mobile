package fr.alcyons.phiwms_mobile.Inventaire.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp
import fr.alcyons.phiwms_mobile.R

class ACompterAdapter(
    private val context: Context,
    private val liste: MutableList<Inventaire_Ligne_Temp>,
    private val onItemClick: (Inventaire_Ligne_Temp) -> Unit
) : RecyclerView.Adapter<ACompterAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomProduit: TextView          = itemView.findViewById(R.id.nomProduit)
        val refProduit: TextView          = itemView.findViewById(R.id.refProduit)
        val fournisseurProduit: TextView  = itemView.findViewById(R.id.fournisseurProduit)
        val lotProduit: TextView          = itemView.findViewById(R.id.lotProduit)
        val peremptionProduit: TextView   = itemView.findViewById(R.id.peremptionProduit)
        val quantiteProduit: TextView     = itemView.findViewById(R.id.quantiteProduit)
        val bandeauStockSaisie_LL: LinearLayout = itemView.findViewById(R.id.bandeauStockSaisie_LL)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.row_detail_inventaire, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = liste[position]

        holder.nomProduit.text         = item.designation
        holder.refProduit.text         = item.produitReference
        holder.fournisseurProduit.text = item.fournisseurNom
        holder.lotProduit.text         = item.lot

        // Gestion de la date de péremption
        val dateString = item.peremptionDate
        val lotUpper = item.lot.uppercase()

        if (dateString.isNullOrEmpty() || dateString == "0000-00-00"
            || lotUpper.contentEquals("LOT NON TRACE")
            || lotUpper.startsWith("PHI")
        ) {
            holder.peremptionProduit.visibility = View.GONE
        } else {
            holder.peremptionProduit.visibility = View.VISIBLE
            val parts = dateString.split("-")
            holder.peremptionProduit.text = "${parts[1]}/${parts[0].substring(2)}"
        }

        // Gestion du bandeau stock
        if (item.stockPhysique.toInt() > -1) {
            holder.bandeauStockSaisie_LL.visibility = View.VISIBLE
            holder.quantiteProduit.text = item.stockPhysique.toInt().toString()
        } else {
            holder.bandeauStockSaisie_LL.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = liste.size
}