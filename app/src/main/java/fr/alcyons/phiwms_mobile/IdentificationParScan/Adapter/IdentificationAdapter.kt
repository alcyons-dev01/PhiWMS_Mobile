package fr.alcyons.phiwms_mobile.IdentificationParScan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.alcyons.phiwms_mobile.Classes.Produit_Identification
import fr.alcyons.phiwms_mobile.R

class IdentificationAdapter(
    private val items: MutableList<Produit_Identification>,
    private val onSupprimerClick: (Produit_Identification, Int) -> Unit
) : RecyclerView.Adapter<IdentificationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val badgeNature: FrameLayout     = view.findViewById(R.id.badgeNature_FL)
        val iconeNature: ImageView       = view.findViewById(R.id.iconeNature_IV)
        val natureLabel: TextView        = view.findViewById(R.id.natureIdentification_TV)
        val codeIdentification: TextView = view.findViewById(R.id.codeIdentification_TV)
        val btnSupprimer: ImageButton    = view.findViewById(R.id.btnSupprimerCode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_code_identification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.codeIdentification.text = item.identification ?: "—"
        holder.natureLabel.text        = item.natureIdentification?.uppercase() ?: "INCONNU"

        when (item.natureIdentification?.uppercase()) {
            "CARTON" -> {
                holder.badgeNature.setBackgroundResource(R.drawable.badge_nature_carton)
                holder.iconeNature.setImageResource(R.drawable.ic_colis)
            }
            "UNITAIRE" -> {
                holder.badgeNature.setBackgroundResource(R.drawable.badge_nature_unitaire)
                holder.iconeNature.setImageResource(R.drawable.icon_produit)
            }
            else -> {
                holder.badgeNature.setBackgroundResource(R.drawable.badge_nature_inconnu)
                holder.iconeNature.setImageResource(R.drawable.ic_barcode)
            }
        }

        holder.btnSupprimer.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_ID.toInt()) {
                onSupprimerClick(item, pos)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun ajouterItem(item: Produit_Identification) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun retirerItem(position: Int) {
        if (position in items.indices) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}