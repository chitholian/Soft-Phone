package net.chitholian.softphone

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.chitholian.softphone.databinding.ItemCallLogEntryBinding

class CallLogsAdapter(
    cursor: Cursor,
    var clickListener: (callLogEntry: CallLogEntry, details: Boolean) -> Unit = { _, _ -> }
) :
    RecyclerView.Adapter<CallLogEntryHolder>() {
    var cursor: Cursor = cursor
        set(value) {
            if (field != value) field.close()
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogEntryHolder {
        return CallLogEntryHolder(
            ItemCallLogEntryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CallLogEntryHolder, position: Int) {
        cursor.moveToPosition(position)
        val entry = CallLogEntry(cursor)
        holder.binding.callLogEntry = entry
        holder.binding.date.isSelected = true
        holder.binding.root.setOnClickListener {
            clickListener(entry, false)
        }
        holder.binding.detailsButton.setOnClickListener {
            clickListener(entry, true)
        }
    }

    override fun getItemCount(): Int {
        return cursor.count
    }
}

class CallLogEntryHolder(val binding: ItemCallLogEntryBinding) :
    RecyclerView.ViewHolder(binding.root)
