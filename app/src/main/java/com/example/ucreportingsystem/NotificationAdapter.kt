package com.example.ucreportingsystem

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ucreportingsystem.R

class NotificationAdapter(
    private val context: Context,
    private var notifications: List<Notification>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardView: com.google.android.material.card.MaterialCardView = itemView as com.google.android.material.card.MaterialCardView
        val borderView: View = itemView.findViewById(R.id.v_status_indicator_border)
        val iconBackground: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cv_icon_background)
        val icon: android.widget.ImageView = itemView.findViewById(R.id.iv_notification_icon)
        val categoryText: android.widget.TextView = itemView.findViewById(R.id.tv_notification_category)
        val descriptionText: android.widget.TextView = itemView.findViewById(R.id.tv_notification_description)
        val timestampText: android.widget.TextView = itemView.findViewById(R.id.tv_notification_timestamp)

        fun bind(notification: Notification) {
            categoryText.text = notification.category
            descriptionText.text = notification.description
            timestampText.text = notification.timestamp

            val style = getNotificationStyle(notification.status)

            cardView.setCardBackgroundColor(ContextCompat.getColor(context, style.backgroundColor))

            borderView.setBackgroundColor(ContextCompat.getColor(context, style.primaryColor))

            iconBackground.setCardBackgroundColor(ContextCompat.getColor(context, style.iconBackgroundColor))

            icon.setImageResource(style.iconResource)
            icon.setColorFilter(ContextCompat.getColor(context, style.primaryColor), PorterDuff.Mode.SRC_IN)

            itemView.setOnClickListener {
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size

    /**
     * Replaces the current list of notifications with a new list (for filtering/search)
     * and refreshes the RecyclerView display.
     */
    fun updateList(newList: List<Notification>) {
        this.notifications = newList
        notifyDataSetChanged()
    }


    private data class NotificationStyle(
        val primaryColor: Int,
        val iconBackgroundColor: Int,
        val backgroundColor: Int,
        val iconResource: Int
    )

    private fun getNotificationStyle(status: NotificationStatus): NotificationStyle {
        return when (status) {
            NotificationStatus.PUBLIC -> NotificationStyle(
                primaryColor = R.color.public_text,
                iconBackgroundColor = R.color.public_icon_bg,
                backgroundColor = R.color.public_bg,
                iconResource = R.drawable.public_icon
            )
            NotificationStatus.PENDING -> NotificationStyle(
                primaryColor = R.color.pending,
                iconBackgroundColor = R.color.pending_bg,
                backgroundColor = R.color.pending_card,
                iconResource = R.drawable.pending_icon
            )
            NotificationStatus.IN_PROGRESS -> NotificationStyle(
                primaryColor = R.color.in_progress,
                iconBackgroundColor = R.color.in_progress_bg,
                backgroundColor = R.color.in_progress_card,
                iconResource = R.drawable.in_progress_icon
            )
            NotificationStatus.RESOLVED -> NotificationStyle(
                primaryColor = R.color.resolved,
                iconBackgroundColor = R.color.resolved_bg,
                backgroundColor = R.color.resolved_card,
                iconResource = R.drawable.resolved_icon
            )
        }
    }
}