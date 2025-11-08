package com.example.ucreportingsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class RoomAdapter(
    private val fullList: List<String>,
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    private var filteredList: MutableList<String> = fullList.toMutableList()

    class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomName: TextView = view.findViewById(R.id.tv_room_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = filteredList[position]
        holder.roomName.text = room
        holder.itemView.setOnClickListener {
            clickListener(room)
        }
    }

    override fun getItemCount() = filteredList.size

    /**
     * Filters the list based on the search text and updates the RecyclerView.
     * This function is called by the TextWatcher in RoomSelectionDialogFragment.
     */

    fun filter(text: String) {
        filteredList.clear()
        if (text.isEmpty()) {
            filteredList.addAll(fullList)
        } else {
            val lowerCaseText = text.lowercase(Locale.getDefault())
            for (item in fullList) {
                if (item.lowercase(Locale.getDefault()).contains(lowerCaseText)) {
                    filteredList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }
}