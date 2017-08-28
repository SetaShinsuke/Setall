package com.seta.setall.common.mvp

import android.os.Environment
import com.seta.setall.common.logs.LogX
import java.io.File
import java.io.FileWriter
import java.io.IOException


/**
 * Created by SETA_WORK on 2017/8/25.
 */
fun writeFile(dir: String, path: String, content: String): Boolean {
    try {
        val sd = Environment.getExternalStorageDirectory()
        val dirFile = File(sd.path + dir)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        val file = FileWriter(sd.path + dir + path)
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