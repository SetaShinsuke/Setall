package com.seta.setall.common.extensions

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ApplicationInfo
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

fun Context.isDebuggable(): Boolean = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

fun ProgressDialog.setMessage(resId: Int) = setMessage(context.getString(resId))