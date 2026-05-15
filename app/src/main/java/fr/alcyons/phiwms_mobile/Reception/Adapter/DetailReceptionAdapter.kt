package fr.alcyons.phiwms_mobile.Reception.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat
import fr.alcyons.phiwms_mobile.R

class DetailReceptionAdapter(
    private val context: Context,
    private val liste: MutableList<PH_Reliquat>,
    private val onItemClick: (PH_Reliquat) -> Unit
) : RecyclerView.Adapter<DetailReceptionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomProduit: TextView            = itemView.findViewById(R.id.nomProduit)
        val refProduit: TextView            = itemView.findViewById(R.id.refProduit)
        val fournisseurProduit: TextView    = itemView.findViewById(R.id.fournisseurProduit)
        val lotProduit: TextView            = itemView.findViewById(R.id.lotProduit)
        val peremptionProduit: TextView     = itemView.findViewById(R.id.peremptionProduit)
        val quantiteReceptionne: TextView   = itemView.findViewById(R.id.quantiteReceptionne)
        val quantiteAReceptionner: TextView = itemView.findViewById(R.id.quantiteAReceptionner)
        val informationLot_LL: LinearLayout = itemView.findViewById(R.id.InformationLot_LL)
        val bandeauQteAReceptionner: LinearLayout = itemView.findViewById(R.id.bandeauQteAReceptionner)
        val bandeauQteReceptionner: LinearLayout  = itemView.findViewById(R.id.bandeauQteReceptionner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.row_a_receptionner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = liste[position]

        holder.nomProduit.text         = item.designationCourte
        holder.refProduit.text         = item.produit_Reference
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

        // Gestion des bandeau selon le type de reliquat
        if (item.reliquat_UID < 0) {
            holder.bandeauQteReceptionner.visibility  = View.VISIBLE
            holder.informationLot_LL.visibility       = View.VISIBLE
            holder.bandeauQteAReceptionner.visibility = View.GONE
            holder.quantiteReceptionne.text           = item.qteLivraison.toInt().toString()
        } else {
            holder.bandeauQteReceptionner.visibility  = View.GONE
            holder.informationLot_LL.visibility       = View.GONE
            holder.bandeauQteAReceptionner.visibility = View.VISIBLE
            holder.quantiteAReceptionner.text         = item.qteReliquat_X.toString()
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = liste.size
}