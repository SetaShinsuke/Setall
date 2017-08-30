package com.seta.setall.common.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.seta.setall.common.logs.LogX
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.charset.Charset


/**
 * Created by SETA_WORK on 2017/8/25.
 */
fun writeFile(dir: String, path: String, content: String): String? {
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
        return dir + path
    } catch (e: IOException) {
        e.printStackTrace()
        LogX.w("Write file error ! ${e.message}")
        return null
    }
}

/**
 * Uri -> String
 */
fun Uri2Path(context: Context, uri: Uri): String? {
    val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
    try {
        val cursor = context.contentResolver.query(uri,
                filePathColumn, null, null, null)
        cursor.moveToFirst()

        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val picturePath = cursor.getString(columnIndex)
        cursor.close()
        return picturePath
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun readFile(filePath: String?): String? {
    var jsonStr: String? = null
    if (filePath == null) {
        return jsonStr
    }
    try {
        val yourFile = File(filePath)
        val stream = FileInputStream(yourFile)
        try {
            val fc = stream.channel
            val bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())

            jsonStr = Charset.defaultCharset().decode(bb).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            stream.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return jsonStr
}