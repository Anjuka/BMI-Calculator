package com.angleone.bmical.utils

import android.util.Log

fun logd(msg:String?){
    if (LogUtil.isDebug) {
        if (msg==null){
            Log.d(LogUtil.getCallerInfo(), "打印了空消息")
        }else{
            Log.d(LogUtil.getCallerInfo(), msg)
        }
    }
}

/**
 * @desc: 通用的日志类，包装了Tag 格式：[类名].[方法名]:[代码行]
 */
object LogUtil {

    private const val BASE_CLASS_PATH = "com.xgpay.xguawallet"

    var isDebug = true

    fun v(msg: String) {
        if (isDebug) {
            Log.v(getCallerInfo(), msg)
        }
    }

    fun d(msg: String?) {
        if (isDebug) {
            if (msg==null){
                Log.d(getCallerInfo(), "打印了空消息")
            }else{
                Log.d(getCallerInfo(), msg)
            }
        }
    }

    fun i(msg: String) {
        if (isDebug) {
            Log.i(getCallerInfo(), msg)
        }
    }

    fun w(msg: String) {
        if (isDebug) {
            Log.w(getCallerInfo(), msg)
        }
    }

    fun e(msg: String) {
        if (isDebug) {
            Log.e(getCallerInfo(), msg)
        }
    }

     fun getCallerInfo(): String {
        val stackTrace = Thread.currentThread().stackTrace
        val caller = stackTrace[4]
        return "<${simplifyClassName(caller.className)}->${caller.methodName}:${caller.lineNumber}>"
    }

    private fun simplifyClassName(className: String): String {
        return className.substringAfter(BASE_CLASS_PATH).substringAfterLast(".")
    }


}
