package net.chitholian.softphone

import android.database.Cursor
import android.provider.CallLog
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.text.format.DateUtils
import java.util.*
import kotlin.math.abs

class CallLogEntry(cursor: Cursor) {
    val callID = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID))
    var cachedName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
        get() {
            return if (field != null) field else "Unknown"
        }
    var number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
    var type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
    var date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))

    fun isoDate(): String {
        if (DateUtils.isToday(date)) {
            return "Today, " + DateFormat.format("hh:mm:ss a", date).toString()
        }
        if (DateUtils.isToday(date + DateUtils.DAY_IN_MILLIS)) {
            return "Yesterday, " + DateFormat.format("hh:mm:ss a", date).toString()
        }
        return DateFormat.format("EEE, yyyy-MM-dd hh:mm:ss a", Date(date)).toString()
    }
}

class ContactEntry(cursor: Cursor) {
    val contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID))
    val contactName: String? =
        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
}
