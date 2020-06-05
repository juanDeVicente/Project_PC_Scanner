package com.projectpcscanner.fragments.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.projectpcscanner.R
import com.projectpcscanner.utils.openWebNavigator

/**
 * A simple [Fragment] subclass.
 */
class DialogIntroduceIP(private val listener: Listener) : DialogFragment(){

    private lateinit var v: View
    private lateinit var dialog: AlertDialog

    interface Listener {
        fun retryConnection(ip: String, port: String)
        fun onNegativeButton()
        fun getActivity(): Activity
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater;
            v = inflater.inflate(R.layout.dialog_introduce_ip, null)

            val helpButton: ImageButton = v.findViewById(R.id.helpButtonIntroduceIP)
            helpButton.setOnClickListener {
                openWebNavigator(listener.getActivity(),"https://github.com/juanDeVicente")
            }

            builder.setView(v)
                .setPositiveButton(getString(R.string.retry)) { _, _ ->
                    listener.retryConnection(v.findViewById<EditText>(R.id.ipInput).text.toString(), v.findViewById<EditText>(R.id.portInput).text.toString())
                }
                .setNegativeButton(getString(R.string.exit)) { _, _ ->
                    listener.onNegativeButton()
                }
            dialog = builder.create()
            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY)

        val ipInput = v.findViewById<EditText>(R.id.ipInput)
        ipInput.addTextChangedListener (object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (Patterns.IP_ADDRESS.matcher(s.toString()).matches()) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
                }
                else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        )
    }
}
