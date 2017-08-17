package com.seta.setall.common.views

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import com.seta.setall.R
import com.seta.setall.common.utils.hideInputMethod

/**
 * Created by SETA_WORK on 2017/7/24.
 */
class InputDialog(context: Context) : AlertDialog.Builder(context) {

    fun show(title: String, inputDialogInterface: InputDialogInterface, contentInit: String? = null) {
        val view = LayoutInflater.from(context).inflate(R.layout.input_dialog, null, false)
        val contentView = view.findViewById(R.id.mEtContent) as EditText
        setView(view)
        setTitle(title)
        if (contentInit != null) {
            contentView.setText(contentInit)
        }
        setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
            hideInputMethod(contentView)
        }
        setPositiveButton(R.string.confirm) { _, _ ->
            inputDialogInterface.onContentConfirm(contentView.text.toString())
            hideInputMethod(contentView)
        }
        show()
    }

    fun show(titleStringId: Int, inputDialogInterface: InputDialogInterface, contentInit: String = "") {
        show(context.getString(titleStringId), inputDialogInterface, contentInit)
    }

    interface InputDialogInterface {
        fun onContentConfirm(content: String)
    }
}