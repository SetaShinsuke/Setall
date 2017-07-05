package com.seta.setall.common.extensions

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * Created by SETA_WORK on 2017/7/3.
 */
fun Activity.logD(content: String) {
    Log.d(javaClass.simpleName, content)
}

fun Context.toast(textResource: Int, vararg extraString: String) {
    val content = getString(textResource) + extraString
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
}

fun ProgressDialog.setMessage(resId: Int) = setMessage(context.getString(resId))