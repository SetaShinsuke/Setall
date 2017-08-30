package com.seta.setall.common.extensions

import android.text.Editable
import android.util.Log
import com.google.gson.Gson
import com.seta.setall.common.logs.LogX
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.common.mvp.MvpView

/**
 * Created by SETA_WORK on 2017/7/4.
 */
fun <T : MvpView?> BasePresenter<T>.logD(message: String) {
    Log.d(javaClass.simpleName, message)
}

fun <T : MvpView?> BasePresenter<T>.logW(message: String, throwable: Throwable? = null) {
    Log.w(javaClass.simpleName, message, throwable)
}

// 在 kotlin中扩展 Java 类的 Gson.fromJson 方法
// 不在传入 T 的class，inline 的作用就是将函数插入到被调用处，配合 reified 就可以获取到 T 真正的类型
inline fun <reified T : Any> Gson.fromJson(json: String): T {
    return Gson().fromJson(json, T::class.java)
}

fun Int.toYuan(): Float = this * 0.01f
fun Int.toYuanInt(): Int = this.toYuan().toInt()

fun Int?.toYuanStr(): String {
    if (this == null) {
        return "￥:?"
    } else {
        return "￥:${toYuan()}"
    }
}

fun Int?.toFloatYuan2(): String {
    if (this == null) {
        return ""
    } else {
        return String.format("%.2f", this * 0.01f)
    }
}

fun Editable?.toCent(): Int? = if (this == null) {
    null
} else {
    try {
        (this.toString().toFloat() * 100).toInt()
    } catch (e: NumberFormatException) {
        e.printStackTrace()
        LogX.e("Format editText content to Cent error : ${e.message}")
        null
    }
}