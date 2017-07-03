package com.seta.setall.common.extensions

import android.app.Activity
import org.apache.log4j.Logger

/**
 * Created by SETA_WORK on 2017/7/3.
 */
fun Activity.log(content: String) {
    Logger.getLogger(javaClass.simpleName).debug(content)
}