package com.seta.setall.common.extensions

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.seta.setall.common.logs.LogX

/**
 * Created by SETA_WORK on 2017/7/3.
 */
fun Activity.logD(content: String, throwable: Throwable? = null) {
    LogX.d(javaClass.simpleName, content, throwable)
}

fun Context.toast(textResource: Int, vararg extraString: String?) {
    var content = getString(textResource)
    extraString.forEach { content += it }
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
}

fun Context.getStringFormated(textResource: Int, vararg values: Any?): String? {
    return String.format(getString(textResource), values)
}

fun View.setVisible(boolean: Boolean) {
    if (boolean) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

fun Context.isDebuggable(): Boolean = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

fun ProgressDialog.setMessage(resId: Int) = setMessage(context.getString(resId))

fun TextView.underLine() {
    paint.flags = Paint.UNDERLINE_TEXT_FLAG
}

fun TextView.deleteLine(): TextView {
    paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
    return this
}

var TextView.money: Int?
    set(valueCent) {
        if (valueCent == null) {
            text = "￥?"
            return
        }
        text = "￥%.2f".format(valueCent?.let { it * 0.01f })
    }
    get() = this.money

fun EditText.onTextChange(textChangeHandler: TextChangeHandler) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            textChangeHandler.onTextChange(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    })
}


interface TextChangeHandler {
    fun onTextChange(s: Editable?)
}

