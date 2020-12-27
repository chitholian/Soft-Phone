package net.chitholian.uilib

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.GridView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class DialPadView(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayoutCompat(context, attributeSet, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet? = null) : this(context,
        attributeSet, 0)

    companion object {
        private const val TAG = "DialPadView"
        val digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")
        val letters =
            listOf("➿", "ABC", "DEF", "GHI", "JKL", "MNO", "PQRS", "TUV", "WXYZ", "", "+", "")
        val keyCodes = listOf(
            KeyEvent.KEYCODE_1,
            KeyEvent.KEYCODE_2,
            KeyEvent.KEYCODE_3,
            KeyEvent.KEYCODE_4,
            KeyEvent.KEYCODE_5,
            KeyEvent.KEYCODE_6,
            KeyEvent.KEYCODE_7,
            KeyEvent.KEYCODE_8,
            KeyEvent.KEYCODE_9,
            KeyEvent.KEYCODE_STAR,
            KeyEvent.KEYCODE_0,
            KeyEvent.KEYCODE_POUND)
    }

    var clickListener: ButtonClickListener? = null
    private val buttonsGridAdapter: DialPadButtonsGridAdapter by lazy {
        DialPadButtonsGridAdapter(context, digits, letters) { index, longClick ->
            val retVal = clickListener?.onClick(index, longClick) ?: true
            if (longClick) {
                if (index == 10) {
                    numberField.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_PLUS))
                    numberField.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_PLUS))
                }
            } else {
                numberField.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN,
                    keyCodes[index]))
                numberField.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP,
                    keyCodes[index]))
            }
            return@DialPadButtonsGridAdapter retVal
        }
    }
    private val numberField: DigitsInputField
    val deleteButton: View
    val numberFieldContainer: View
    val digitsInputField: EditText
        get() = numberField

    init {
        inflate(context, R.layout.dial_pad_view, this)
        orientation = VERTICAL
        numberField = findViewById(R.id.numberField)
        deleteButton = findViewById(R.id.deleteButton)
        numberFieldContainer = findViewById(R.id.numberFieldContainer)
        findViewById<GridView>(R.id.dialPadButtonsGrid).adapter = buttonsGridAdapter
        numberField.addTextChangedListener {
            numberFieldContainer.visibility =
                if (it?.isNotEmpty() == true) View.VISIBLE else View.GONE
        }
        deleteButton.setOnLongClickListener {
            numberField.setText("")
            return@setOnLongClickListener true
        }
        deleteButton.setOnClickListener {
            numberField.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
            numberField.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
        }
    }

    private class DialPadButtonsGridAdapter(
        val context: Context,
        val digits: List<String>,
        val letters: List<String?>,
        var clickListener: (keyIndex: Int, isLongClick: Boolean) -> Boolean,
    ) : BaseAdapter() {
        override fun getCount() = digits.size

        override fun getItem(position: Int) = digits[position]

        override fun getItemId(position: Int) = digits[position].hashCode().toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.dial_pad_button, parent, false)

            view.findViewById<MaterialTextView>(R.id.dialPadButtonDigit).text = digits[position]
            view.findViewById<MaterialTextView>(R.id.dialPadButtonLetters).text = letters[position]
            view.setOnClickListener {
                clickListener(position, false)
            }
            view.setOnLongClickListener {
                clickListener(position, true)
            }
            return view
        }
    }

    interface ButtonClickListener {
        fun onClick(keyIndex: Int, isLongClick: Boolean): Boolean
    }
}

internal class DigitsInputField(context: Context, attributeSet: AttributeSet? = null) :
    TextInputEditText(context, attributeSet) {

    init {
        inputType = inputType or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        showSoftInputOnFocus = false
        setSingleLine()
        maxLines = 1
        freezesText = true
        isFocusableInTouchMode = true
        background = context.theme.getDrawable(android.R.color.transparent)
        freezesText = true
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        setSelection(length())
    }
}
