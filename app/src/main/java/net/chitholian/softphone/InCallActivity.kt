package net.chitholian.softphone

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import net.chitholian.softphone.databinding.ActivityInCallBinding

class InCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_in_call)
        binding.dialPad.deleteButton.visibility = View.INVISIBLE
        binding.dialPad.digitsInputField.isCursorVisible = false

        // Set speaker mode
        binding.speakerButton.imageTintList =
            ContextCompat.getColorStateList(this, R.color.tint_green)

        // Set mute mode
        binding.muteButton.imageTintList = ContextCompat.getColorStateList(this, R.color.tint_red)

        // Set hold mode
        binding.holdButton.imageTintList = ContextCompat.getColorStateList(this, R.color.tint_red)

        binding.dialPadToggle.setOnClickListener {
            setDialPadShown(binding.dialPad.visibility == View.GONE)
        }
    }

    private fun setDialPadShown(shown: Boolean = true) {
        if (shown) {
            binding.dialPadToggle.animate().rotation(180f)
            binding.dialPad.visibility = View.VISIBLE
            binding.dialPad.animate().alpha(1f)
            binding.callControls.animate().alpha(0f).withEndAction {
                binding.callControls.visibility = View.GONE
            }
        } else {
            binding.dialPadToggle.animate().rotation(0f)
            binding.callControls.visibility = View.VISIBLE
            binding.callControls.animate().alpha(1f)
            binding.dialPad.animate().alpha(0f).withEndAction {
                binding.dialPad.visibility = View.GONE
            }
        }
    }
}
