package com.projectpcscanner.fragments.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.projectpcscanner.R
import com.projectpcscanner.utils.openWebNavigator

class DialogAuthenticate(): DialogFragment() {

    private lateinit var listener: Listener
    private lateinit var dialog: AlertDialog
    private lateinit var v: View

    interface Listener {
        fun authenticate(password: String)
        fun getActivity(): Activity
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        }
        catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            v = inflater.inflate(R.layout.dialog_authenticate, null)

            builder.setView(v)
                .setPositiveButton(getString(R.string.retry)) { _, _ ->
                    listener.authenticate(v.findViewById<EditText>(R.id.editTextPassword).text.toString())
                }
                .setNegativeButton(getString(R.string.exit)) { _, _ ->
                    dialog.dismiss()
                }
            dialog = builder.create()
            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    override fun onStart() {
        super.onStart()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY)

        val ipInput = v.findViewById<EditText>(R.id.editTextPassword)
        ipInput.addTextChangedListener (object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
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