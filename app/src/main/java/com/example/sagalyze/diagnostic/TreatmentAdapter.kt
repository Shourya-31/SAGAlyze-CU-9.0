package com.example.sagalyze.diagnostic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sagalyze.R
import com.google.android.material.card.MaterialCardView

class TreatmentAdapter(
    private val items: MutableList<TreatmentItem>,
    private val onEdit: (TreatmentItem) -> Unit,
    private val onDelete: (TreatmentItem) -> Unit
) : RecyclerView.Adapter<TreatmentAdapter.TreatmentViewHolder>() {

    inner class TreatmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconContainer: MaterialCardView = view.findViewById(R.id.iconContainer)
        val iconImageView: ImageView = view.findViewById(R.id.iconImageView)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val descriptionText: TextView = view.findViewById(R.id.descriptionText)
        val editButton: ImageButton = view.findViewById(R.id.editButton)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreatmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_treatment, parent, false)
        return TreatmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreatmentViewHolder, position: Int) {
        val item = items[position]

        holder.titleText.text = item.title
        holder.descriptionText.text = item.description

        // Set icon + color by type
        when (item.type) {
            TreatmentType.MEDICATION -> {
                holder.iconImageView.setImageResource(R.drawable.ic_pill)
                holder.iconContainer.setCardBackgroundColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.severity_low_bg)
                )
            }
            TreatmentType.LIFESTYLE -> {
                holder.iconImageView.setImageResource(R.drawable.ic_heart)
                holder.iconContainer.setCardBackgroundColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.severity_medium_bg)
                )
            }
            TreatmentType.FOLLOW_UP -> {
                holder.iconImageView.setImageResource(R.drawable.ic_calendar)
                holder.iconContainer.setCardBackgroundColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.severity_high_bg)
                )
            }
        }

        // Actions
        holder.editButton.setOnClickListener { onEdit(item) }
        holder.deleteButton.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size
}
