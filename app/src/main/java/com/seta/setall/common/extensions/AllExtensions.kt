package com.seta.setall.common.extensions

import android.util.Log
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

fun Int.toYuan(): Float = this * 0.01f

fun Int?.toYuanStr(): String {
    if (this == null) {
        return "￥:?"
    } else {
        return "￥:${toYuan()}"
    }
}