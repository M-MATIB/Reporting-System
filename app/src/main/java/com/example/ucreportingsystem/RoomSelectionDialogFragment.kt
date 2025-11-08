package com.example.ucreportingsystem

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DividerItemDecoration
import android.text.Editable
import android.text.TextWatcher
import com.example.ucreportingsystem.R

class RoomSelectionDialogFragment(
    private val items: List<String>,
    private val selectionListener: (String) -> Unit
) : DialogFragment() {

    private lateinit var roomAdapter: RoomAdapter

    override fun getTheme(): Int {
        return androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return try {
            inflater.inflate(R.layout.dialog_room_selector, container, false)
        } catch (e: Exception) {
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_room_list)
        val searchEditText = view.findViewById<EditText>(R.id.et_search_room)

        if (recyclerView == null || searchEditText == null) {
            return
        }

        recyclerView.layoutManager = LinearLayoutManager(context)

        roomAdapter = RoomAdapter(items) { selectedItem ->
            selectionListener(selectedItem)
            dismiss()
        }

        recyclerView.adapter = roomAdapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                roomAdapter.filter(s.toString())
            }
        })
    }

    override fun onResume() {
        super.onResume()

        val window = dialog?.window

        val width = resources.displayMetrics.widthPixels * 0.9

        window?.setLayout(width.toInt(), LayoutParams.WRAP_CONTENT)
    }
}