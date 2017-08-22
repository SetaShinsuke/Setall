package com.seta.setall.common.framework

import android.app.Application
import com.seta.setall.common.extensions.DelegateExt

/**
 * Created by SETA_WORK on 2017/7/3.
 */
class BaseApplication : Application() {

    companion object {
        var instance: BaseApplication by DelegateExt.notNullSingleValue() //非空、懒加载
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}