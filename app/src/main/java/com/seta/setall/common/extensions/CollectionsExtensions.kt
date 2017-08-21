package com.seta.setall.common.extensions

/**
 * Created by SETA_WORK on 2017/7/18.
 * Collections 的扩展函数
 */


/**
 * Map -> (List) -> vararg 数组
 */
fun <K, V : Any> MutableMap<K, V?>.toVarargArray(): Array<out Pair<K, V?>> =
        map({ Pair(it.key, it.value) }).toTypedArray()