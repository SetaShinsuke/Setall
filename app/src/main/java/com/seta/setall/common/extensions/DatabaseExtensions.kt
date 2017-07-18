package com.seta.setall.common.extensions

import android.database.sqlite.SQLiteDatabase

/**
 * Created by SETA_WORK on 2017/7/18.
 */
fun SQLiteDatabase.clear(tableName: String) {
    execSQL("delete from $tableName")
}