package com.seta.setall.common.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by SETA_WORK on 2017/7/24.
 */
object DateUtils {
    fun getYMD(year: Int, month: Int, day: Int): String {
        return "$year-$month-$day"
    }
}

fun Date.toYMD(): String {
    val simpleDateFormat = SimpleDateFormat.getInstance()
    return simpleDateFormat.format(this)
}

fun Calendar.toYMD(): String {
    return "${get(Calendar.YEAR)}-${get(Calendar.MONTH)}-${Calendar.DAY_OF_MONTH}"
}