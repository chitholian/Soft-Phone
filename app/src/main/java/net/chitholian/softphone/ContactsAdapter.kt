package net.chitholian.softphone

import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.chitholian.softphone.databinding.ItemContactEntryBinding

class ContactsAdapter(
    cursor: Cursor,
    var clickListener: (contactEntry: ContactEntry, call: Boolean) -> Unit = { _, _ -> }
) :
    RecyclerView.Adapter<ContactEntryHolder>() {
    var cursor: Cursor = cursor
        set(value) {
            if (field != value) field.close()
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactEntryHolder {
        return ContactEntryHolder(
            ItemContactEntryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ContactEntryHolder, position: Int) {
        cursor.moveToPosition(position)
        val entry = ContactEntry(cursor)
        holder.binding.contactEntry = entry
        holder.binding.name.isSelected = true
        holder.binding.root.setOnClickListener {
            clickListener(entry, false)
        }
//        holder.binding.detailsButton.setOnClickListener {
//            clickListener(entry, true)
//        }
    }

    override fun getItemCount(): Int {
        return cursor.count
    }
}

class ContactEntryHolder(val binding: ItemContactEntryBinding) :
    RecyclerView.ViewHolder(binding.root)
