package com.seta.setall.steam.views

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import com.seta.setall.R
import com.seta.setall.common.logs.LogX
import com.seta.setall.common.utils.hideInputMethod

/**
 * Created by SETA_WORK on 2017/8/17.
 */
class PriceEditDialog(context: Context) : AlertDialog.Builder(context) {

    fun show(title: String, inputDialogInterface: PriceEditInterface, priceInitOrg: String? = "", priceFinalOrg: String? = "") {
        val view = LayoutInflater.from(context).inflate(R.layout.price_edit_dialog, null, false)
        val mEtPriceInit = view.findViewById(R.id.mEtPriceInit) as EditText
        val mEtPriceFinal = view.findViewById(R.id.mEtPriceFinal) as EditText
        setView(view)
        setTitle(title)
        priceInitOrg?.let {
            mEtPriceInit.setText(it)
        }
        priceFinalOrg?.let {
            mEtPriceFinal.setText(it)
        }
        setNegativeButton(R.string.cancel) {
            dialog, _ ->
            dialog.dismiss()
            hideInputMethod(mEtPriceFinal)
        }
        setPositiveButton(R.string.confirm) { _, _ ->
            try {
                val priceInit = mEtPriceInit.text.toString()
                val priceFinal = mEtPriceFinal.text.toString()
                inputDialogInterface.onContentConfirm((priceInit.toFloat() * 100).toInt(), (priceFinal.toFloat() * 100).toInt())
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                LogX.i("Parse price int error : ${e.message}")
            }
            hideInputMethod(mEtPriceFinal)
        }
        show()
    }

    fun show(titleStringId: Int, inputDialogInterface: PriceEditInterface, priceInitOrg: String? = null, priceFinalOrg: String? = null) {
        show(context.getString(titleStringId), inputDialogInterface, priceInitOrg, priceFinalOrg)
    }

    interface PriceEditInterface {
        fun onContentConfirm(priceInit: Int, priceFinal: Int)
    }
}