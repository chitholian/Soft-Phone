package net.chitholian.softphone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import net.chitholian.softphone.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    companion object {
        private const val CONTACTS_REQUEST = 888
    }

    private lateinit var binding: FragmentContactsBinding
    private var cursor: Cursor? = null
    private var contactsAdapter: ContactsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.contactList.layoutManager = LinearLayoutManager(view.context)
        binding.addContactButton.setOnClickListener {
            try {
                val i = Intent(Intent.ACTION_INSERT)
                i.type = ContactsContract.Contacts.CONTENT_TYPE
                i.putExtra("finishActivityOnSaveCompleted", true)
                startActivityForResult(i, 0);
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        if (ContextCompat.checkSelfPermission(
                view.context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showContactList()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_REQUEST
            )
        }
    }

    private fun showContactList() {
        cursor?.close()
        cursor = context?.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            ),
            null, null,
            ContactsContract.Contacts.DISPLAY_NAME,
        )
        cursor?.let {
            if (contactsAdapter == null) {
                contactsAdapter = ContactsAdapter(it) { entry, call ->
                    if (call) {
                        //TODO: Try calling it.
                    } else {
                        // Open Contact Details.
                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.data = Uri.withAppendedPath(
                                ContactsContract.Contacts.CONTENT_URI,
                                entry.contactID.toString()
                            )
                            startActivity(intent)
                        } catch (t: Throwable) {
                            t.printStackTrace()
                        }
                    }
                }
            } else {
                contactsAdapter?.cursor = it
            }
            binding.contactList.adapter = contactsAdapter
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CONTACTS_REQUEST) {
            grantResults.forEachIndexed { index, result ->
                if (permissions[index] == Manifest.permission.READ_CONTACTS && result == PackageManager.PERMISSION_GRANTED) {
                    showContactList()
                    return
                }
            }
            // Permission Denied.
            context?.let {
                Toast.makeText(it, "Contacts read permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        cursor?.close()
        super.onDestroyView()
    }
}
