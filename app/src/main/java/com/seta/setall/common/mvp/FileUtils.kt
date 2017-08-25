package com.seta.setall.common.mvp

import android.os.Environment
import com.seta.setall.common.logs.LogX
import java.io.File
import java.io.FileWriter
import java.io.IOException


/**
 * Created by SETA_WORK on 2017/8/25.
 */
fun writeFile(path: String, content: String): Boolean {
    try {
        val sd = Environment.getExternalStorageDirectory()
        val file = FileWriter(sd.path + path)
        val dir = File(sd.path + path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        file.write(content)
        file.flush()
        file.close()
        return true
    } catch (e: IOException) {
        e.printStackTrace()
        LogX.w("Write file error ! ${e.message}")
        return false
    }

}