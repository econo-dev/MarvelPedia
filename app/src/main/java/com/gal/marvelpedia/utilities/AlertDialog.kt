package com.gal.marvelpedia.utilities

import android.content.Context
import android.view.View
import android.support.v7.app.AlertDialog


class Alerter(
    context: Context,
    title: String,
    message: String,
    positiveButton: String,
    negativeButton: String
) {
    val context: Context = context
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(negativeButton) { dialog, _ -> dialog.dismiss() }




//
//    builder.setNegativeButton("Cancel") { dialog, _ ->
//        dialog.dismiss()
//    }
//
//    val dialog: android.support.v7.app.AlertDialog = builder.create()
//    dialog.show()
}