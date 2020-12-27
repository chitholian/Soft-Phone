package net.chitholian.softphone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.chitholian.softphone.databinding.FragmentDialerBinding

class DialerFragment : Fragment() {

    companion object {
        private const val CALL_LOG_REQUEST = 222
        var dialPadShown = true
        private var dialPadTransY = 0f
    }

    private lateinit var binding: FragmentDialerBinding
    private var callLogsAdapter: CallLogsAdapter? = null
    private var cursor: Cursor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialerFab.setOnClickListener {
            setDialPadShown(true)
        }
        binding.dialPadHide.setOnClickListener {
            setDialPadShown(false)
        }
        binding.dialButton.setOnClickListener {
            val number = binding.dialPad.digitsInputField.text.toString()
            if (number.isEmpty()) {
                // Set the last call number in the digits field.
                binding.dialPad.digitsInputField.setText(CallLog.Calls.getLastOutgoingCall(it.context))
            } else {
                // TODO: Dial Current number.
            }
        }
        binding.callHistory.layoutManager = LinearLayoutManager(view.context)
        binding.callHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) setDialPadShown(false)
            }
        })

        if (ContextCompat.checkSelfPermission(
                view.context,
                Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCallHistory()
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CALL_LOG), CALL_LOG_REQUEST)
        }
    }

    private fun showCallHistory() {
        cursor?.close()
        cursor = context?.contentResolver?.query(
            CallLog.Calls.CONTENT_URI, arrayOf(
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls._ID
            ), null, null, CallLog.Calls._ID + " DESC"
        )
        cursor?.let {
            if (callLogsAdapter == null) {
                callLogsAdapter = CallLogsAdapter(it) { entry, details ->
                    if (details) {
                        //TODO: Open call logs.
                    } else {
                        //TODO: Set and dial this number.
                        binding.dialPad.digitsInputField.setText(entry.number)
                    }
                }
            } else {
                callLogsAdapter?.cursor = it
            }
            binding.callHistory.adapter = callLogsAdapter
        }
    }

    private fun setDialPadShown(shown: Boolean = true) {
        dialPadShown = shown
        if (shown) {
            binding.dialPadContainer.visibility = View.VISIBLE
            binding.dialPadContainer.animate().translationY(0f)
            binding.dialerFab.animate()
                .translationX(binding.dialerFab.width + binding.dialerFab.marginEnd.toFloat())
                .withEndAction {
                    binding.dialerFab.visibility = View.GONE
                }
        } else {
            binding.dialerFab.visibility = View.VISIBLE
            binding.dialerFab.animate().translationX(0f)
            dialPadTransY = binding.dialPadContainer.width.toFloat()
            binding.dialPadContainer.animate().translationY(dialPadTransY).withEndAction {
                binding.dialPadContainer.visibility = View.GONE
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CALL_LOG_REQUEST) {
            grantResults.forEachIndexed { index, result ->
                if (permissions[index] == Manifest.permission.READ_CALL_LOG && result == PackageManager.PERMISSION_GRANTED) {
                    showCallHistory()
                    return
                }
            }
            // Permission Denied.
            context?.let {
                Toast.makeText(it, "Call Log permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (!dialPadShown) {
            binding.dialPadContainer.visibility = View.GONE
            binding.dialerFab.visibility = View.VISIBLE
            binding.dialerFab.translationX = 0f
            binding.dialPadContainer.translationY = dialPadTransY
        }
    }

    override fun onDestroyView() {
        cursor?.close()
        super.onDestroyView()
    }
}
