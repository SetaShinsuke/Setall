package com.seta.setall.common.logs

import android.content.Context
import android.util.Log
import com.seta.setall.common.extensions.isDebuggable
import com.seta.setall.common.utils.Constants
import org.apache.log4j.Logger

/**
 * Created by SETA_WORK on 2017/7/4.
 */
object LogX {

    val logger: Logger by lazy { Logger.getLogger("Log") }
    var logLevel = Log.VERBOSE
    var logFileLevel = Log.INFO
    var logTag = Constants.LOG_TAG_S

    fun init(context: Context, logFileName: String) {
        LogConfig.configLogger(context, logFileName)
        logger.trace("Log Init")
        if (context.isDebuggable()) {
            logLevel = Log.VERBOSE
            logFileLevel = Log.VERBOSE
        } else {
            logLevel = Log.DEBUG
            logFileLevel = Log.DEBUG
        }
    }

    private fun trace2File(tag: String, msg: String) {
        logger.trace(tag + " " + msg)
    }

    fun fastLog(msg: String) {
        Log.v(logTag, msg)
    }

    fun mark() {
        e("有调试代码未删除!!!")
    }

    fun v(s: String) {
        v(logTag, s)
    }

    fun v(tag: String = logTag, s: String) {
        if (logLevel <= Log.VERBOSE) {
            Log.v(tag, s)
            if (logFileLevel <= Log.VERBOSE) {
                trace2File(tag, s)
            }
        }
    }

    fun d(s: String) {
        d(logTag, s)
    }

    fun d(tag: String = logTag, s: String) {
        if (logLevel <= Log.DEBUG) {
            Log.d(tag, s)
            if (logFileLevel <= Log.DEBUG) {
                trace2File(tag, s)
            }
        }
    }

    fun i(s: String) {
        i(logTag, s)
    }

    fun i(tag: String = logTag, s: String) {
        if (logLevel <= Log.INFO) {
            Log.i(tag, s)
            if (logFileLevel <= Log.INFO) {
                trace2File(tag, s)
            }
        }
    }

    fun w(s: String) {
        w(logTag, s)
    }

    fun w(tag: String = logTag, s: String) {
        if (logLevel <= Log.WARN) {
            Log.w(tag, s)
            if (logFileLevel <= Log.WARN) {
                trace2File(tag, s)
            }
        }
    }

    fun e(s: String) {
        e(logTag, s)
    }

    fun e(tag: String = logTag, s: String, throwable: Throwable? = null) {
        if (logLevel <= Log.ERROR) {
            if (throwable != null) {
                Log.e(tag, s, throwable)
            } else {
                Log.e(tag, s)
            }
            if (logFileLevel <= Log.ERROR) {
                trace2File(tag, s)
            }
        }
    }
}