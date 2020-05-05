package com.projectpcscanner.fragments.dialogs

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.projectpcscanner.R
import com.projectpcscanner.models.StaticsModel


class DialogFragmentNetworkError() : DialogFragment() {
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

            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.dialog_network_error, null)

            val helpButton: ImageButton = view.findViewById(R.id.helpButtonNetworkError)
            helpButton.setOnClickListener {
                val urlString = "https://github.com/juanDeVicente"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage("com.android.chrome")
                try {
                    context!!.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    intent.setPackage(null)
                    context!!.startActivity(intent)
                }
            }

            builder.setView(view)
                .setPositiveButton("Reintentar",
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onPositiveButton()
                    })
                .setNegativeButton("Cerrar app",
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onNegativeButton()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface DialogFragmentNetworkErrorListener {
        fun onPositiveButton()
        fun onNegativeButton()
    }
}
