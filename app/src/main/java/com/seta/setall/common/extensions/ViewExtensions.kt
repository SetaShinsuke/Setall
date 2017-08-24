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
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

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

fun View.setVisible(boolean: Boolean?) {
    if(boolean == null){
        visibility = View.GONE
        return
    }
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
    get() {
        if (text == "￥?") {
            return null
        }
        try {
            return (text.subSequence(1, text.lastIndex).toString().toFloat() * 100).toInt()
        } catch (e: Exception) {
            return null
        }
    }

fun EditText.isTextEmpty(): Boolean {
    return text == null || text.toString() == ""
}

fun EditText.onTextChange(context: CoroutineContext = UI,
                          handler: suspend CoroutineScope.(s: Editable?) -> Unit) {
    setTextChangeHandler(
            object : TextChangeHandler {
                override fun onTextChange(s: Editable?) {
                    launch(context) {
                        handler(s)
                    }
                }
            })
}

fun EditText.setTextChangeHandler(textChangeHandler: TextChangeHandler) {
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
    fun onTextChange(s: Editable?): Unit
}

