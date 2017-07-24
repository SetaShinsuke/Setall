package com.seta.setall.common.views

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import com.seta.setall.R

/**
 * Created by SETA_WORK on 2017/7/24.
 */
class InputDialog(context: Context) : AlertDialog.Builder(context) {

    fun show(title: String, inputDialogInterface: InputDialogInterface) {
        val view = LayoutInflater.from(context).inflate(R.layout.input_dialog, null, false)
        val contentView = view.findViewById(R.id.mEtContent) as EditText
        setView(view)
        setTitle(title)
        setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
        setPositiveButton(R.string.confirm) { _, _ -> inputDialogInterface.onContentConfirm(contentView.text.toString()) }
        show()
    }

    fun show(titleStringId: Int, inputDialogInterface: InputDialogInterface) {
        show(context.getString(titleStringId), inputDialogInterface)
    }

    interface InputDialogInterface {
        fun onContentConfirm(content: String)
    }
}