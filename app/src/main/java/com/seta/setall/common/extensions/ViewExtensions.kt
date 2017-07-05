package com.seta.setall.common.extensions

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import org.apache.log4j.Logger

/**
 * Created by SETA_WORK on 2017/7/3.
 */
fun Activity.log(content: String) {
    Logger.getLogger(javaClass.simpleName).debug(content)
}

fun Context.toast(textResource: Int, vararg extraString: String) {
    val content = getString(textResource) + extraString
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
}

fun ProgressDialog.setMessage(resId: Int) = setMessage(context.getString(resId))