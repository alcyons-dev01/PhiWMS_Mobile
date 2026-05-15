package fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne
import fr.alcyons.phiwms_mobile.R

class DetailPreparationAdapter(
    private val context: Context,
    private val liste: MutableList<PH_Preparation_Ligne>,
    private val onItemClick: (PH_Preparation_Ligne) -> Unit
) : RecyclerView.Adapter<DetailPreparationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomProduit: TextView              = itemView.findViewById(R.id.nomProduit)
        val refProduit: TextView              = itemView.findViewById(R.id.refProduit)
        val fournisseurProduit: TextView      = itemView.findViewById(R.id.fournisseurProduit)
        val lotProduit: TextView              = itemView.findViewById(R.id.lotProduit)
        val serieProduit: TextView            = itemView.findViewById(R.id.serieProduit)
        val peremptionProduit: TextView       = itemView.findViewById(R.id.peremptionProduit)
        val quantitePreparer: TextView        = itemView.findViewById(R.id.quantitePreparer)
        val quantiteAPreparer: TextView       = itemView.findViewById(R.id.quantiteAPreparer)
        val informationLot_LL: LinearLayout   = itemView.findViewById(R.id.InformationLot_LL)
        val bandeauQteAPreparer: LinearLayout = itemView.findViewById(R.id.bandeauQteAPreparer)
        val bandeauQtePreparer: LinearLayout  = itemView.findViewById(R.id.bandeauQtePreparer)
        val layoutSerieNumero: LinearLayout   = itemView.findViewById(R.id.layoutSerieNumero)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.row_preparation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = liste[position]

        holder.nomProduit.text         = item.produitDesignation
        holder.refProduit.text         = item.produitReference
        holder.fournisseurProduit.text = ""
        holder.lotProduit.text         = item.lotNumero
        holder.serieProduit.text       = item.serieNumero

        // Gestion de la date de péremption
        val dateString = item.peremptionDate
        val lotUpper = item.lotNumero.uppercase()

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

        // Gestion des bandeaux selon le type de ligne
        if (item._UID < 0) {
            holder.bandeauQtePreparer.visibility  = View.VISIBLE
            holder.informationLot_LL.visibility   = View.VISIBLE
            holder.bandeauQteAPreparer.visibility = View.GONE
            holder.quantitePreparer.text          = item.qte_preparer.toString()
        } else {
            holder.bandeauQtePreparer.visibility  = View.GONE
            holder.informationLot_LL.visibility   = View.GONE
            holder.bandeauQteAPreparer.visibility = View.VISIBLE
            holder.quantiteAPreparer.text         = item.qte_APreparer.toString()
        }

        // Gestion de l'affichage du numéro de série
        holder.layoutSerieNumero.visibility =
            if (item.serieNumero.isEmpty()) View.GONE else View.VISIBLE

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = liste.size
}