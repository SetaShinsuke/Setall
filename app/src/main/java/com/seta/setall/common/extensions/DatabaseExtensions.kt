package com.seta.setall.common.extensions

import android.database.sqlite.SQLiteDatabase
import com.seta.setall.common.logs.LogX
import org.jetbrains.anko.db.*

/**
 * Created by SETA_WORK on 2017/7/18.
 */

/**
 * 把 cursor 转换成 collection
 */
fun <T : Any> SelectQueryBuilder.parseList(parser: (Map<String, Any?>) -> T): List<T> =
        parseList(object : MapRowParser<T> {
            override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
        })

fun <T : Any> SelectQueryBuilder.parseOpt(parser: (Map<String, Any?>) -> T): T? =
        parseOpt(object : MapRowParser<T> {
            override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
        })


fun SQLiteDatabase.clear(tableName: String) {
    execSQL("delete from $tableName")
}

fun SelectQueryBuilder.byId(id: Long) = whereSimple("_id = ?", id.toString())

fun SQLiteDatabase.exists(tableName: String,
                          argName: String,
                          argValue: String): Boolean {
    var exist = false
    select(tableName)
            .whereSimple("$argName = ?", argValue)
            .parseList(object : MapRowParser<Unit> {
                override fun parseRow(columns: Map<String, Any?>): Unit {
                    exist = columns.isNotEmpty()
                }
            })
    return exist
}

fun SQLiteDatabase.insertOrUpdate(tableName: String,
                                  argName: String,
                                  argValue: String,
                                  vararg values: Pair<String, Any?>) {
    if (exists(tableName, argName, argValue)) {
        val result = update(tableName, *values)
        LogX.d("Update result : $result")
    } else {
        insert(tableName, *values)
    }
}
