package com.example.chatapp.components

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.chatapp.R

class DeleteMessageDialogFragment : DialogFragment() {

    interface IDeleteMessageFragment {
        fun confirmClicked()
    }

    lateinit var listener: IDeleteMessageFragment

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.delete_message)
                .setPositiveButton(R.string.confirm
                ) { dialog, id ->
                    if (this::listener.isInitialized)
                        listener.confirmClicked()
                }
                .setNegativeButton(R.string.cancel
                ) { dialog, id ->
                    dialog.dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}