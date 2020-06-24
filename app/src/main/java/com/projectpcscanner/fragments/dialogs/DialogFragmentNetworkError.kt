package com.projectpcscanner.fragments.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.projectpcscanner.R
import com.projectpcscanner.utils.openWebNavigator


class DialogFragmentNetworkError : DialogFragment() {
    private lateinit var listener: DialogFragmentNetworkErrorListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogFragmentNetworkErrorListener
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
            val view = inflater.inflate(R.layout.dialog_network_error, null)

            val helpButton: ImageButton = view.findViewById(R.id.helpButtonNetworkError)
            helpButton.setOnClickListener {
                openWebNavigator(listener.getActivity(),"https://github.com/juanDeVicente")
            }

            builder.setView(view)
                .setPositiveButton(getString(R.string.retry)) { _, _ ->
                    listener.onPositiveButton()
                }
                .setNeutralButton(R.string.introduce_ip) {_, _ ->
                    listener.onNeutralButton()
                }
                .setNegativeButton(getString(R.string.exit)) { _, _ ->
                    listener.onNegativeButton()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface DialogFragmentNetworkErrorListener {
        fun onPositiveButton()
        fun onNeutralButton()
        fun onNegativeButton()
        fun getActivity(): Activity
    }
}
