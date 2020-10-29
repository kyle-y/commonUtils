package com.example.administrator.locallog

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.*
import com.example.administrator.common.commonUtils.PermissionUtils.PermissionActivity.start
import okio.BufferedSink
import okio.Okio
import java.io.File
import java.lang.reflect.Method
import java.util.*


object LogUtils {
    private const val LOG_DIR_NAME = "recordLog"
    private const val MAX_LOG_SIZE = 1 * 1024 * 1024L  //1M
    private const val MAX_FILE_COUNT = 50 //50个文件
    private const val NORMAL_FILE_COUNT = 40 //40个文件
    private var application: Application? = null
    private var isLogClosed = false
    private val logSp: SharedPreferences by lazy {
        getContext().getSharedPreferences(LOG_DIR_NAME, Context.MODE_PRIVATE)
    }
    private val handlerThread: HandlerThread = HandlerThread(LOG_DIR_NAME).apply {
        start()
    }
    private val handler: Handler = object : Handler(handlerThread.looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val obj = msg.obj
            if (msg.what == 0 && obj is String) {
                handleLog(obj)
            } else if (msg.what == 1) {
                clearLog()
            }
        }
    }

    fun log(msg: String) {
        if (isLogClosed) return
        val logItem = "${Date().toFormat()}-log: $msg \n"
        handler.sendMessage(handler.obtainMessage(0, logItem))
    }

    fun clearExtroLog() {
        handler.sendMessage(handler.obtainMessage(1))
    }

    fun reStartLog() {
        isLogClosed = false
    }

    fun isPaused(): Boolean {
        return isLogClosed
    }

    fun pauseLog() {
        isLogClosed = true
        fileSink?.flush()
        fileSink?.close()
        fileSink = null
        file = null
    }

    private var fileSink: BufferedSink? = null
    private var file: File? = null
    private var lastCheckTime = 0L

    private fun handleLog(msg: String) {
        try {
            createFileSinkIfNotExits()
            saveLog(msg)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                fileSink?.flush()
                fileSink?.close()
            } catch (ignored: Exception) {

            } finally {
                fileSink = null
            }
        }
    }

    private fun createFileSinkIfNotExits() {
        //每个文件最大1M，每天文件不同
        //每10秒钟写入一次，并检查一次文件名是否符合当前日期，以及文件大小是否超过限制
        val dateStr = Date().toFormat("yyyy-MM-dd")
        if (fileSink != null && SystemClock.elapsedRealtime() - lastCheckTime > 10000) {
            lastCheckTime = SystemClock.elapsedRealtime()

            if (file == null || file!!.exists().not()) {
                fileSink?.flush()
                fileSink?.close()
                fileSink = null
            }

            file?.let {
                fileSink?.flush()
                if (it.length() >= MAX_LOG_SIZE || !(it.name.contains(dateStr))) {
                    fileSink?.close()
                    fileSink = null
                    file = null
                }
            }

        }

        if (fileSink == null) {
            // 创建新fileSink
            val dir = getLogDir()
            val spKey = "$LOG_DIR_NAME&$dateStr"
            val fileIndex = logSp.getInt(spKey, -1)
            if (fileIndex < 0) {
                // 本日还未创建过日志文件，从0开始
                file = File(dir, "$LOG_DIR_NAME&$dateStr&0")
                logSp.edit().putInt(spKey, 0).apply()
            } else {
                val targetFile = File(dir, "$LOG_DIR_NAME&$dateStr&$fileIndex")
                if (targetFile.exists() && targetFile.length() < MAX_LOG_SIZE) {
                    // 小于目标大小可以继续使用
                    file = targetFile
                } else {
                    // 文件过大
                    file = File(dir, "$LOG_DIR_NAME&$dateStr&${fileIndex + 1}")
                    logSp.edit().putInt(spKey, fileIndex + 1).apply()
                }
            }
            fileSink = Okio.buffer(Okio.appendingSink(file!!))
        }
    }

    private fun saveLog(msg: String) {
        fileSink?.writeUtf8(msg)
    }

    private fun clearLog() {
        try {
            val logFile = getLogDir()
            if (logFile.exists()) {
                val logFileList = logFile.listFiles()
                if (logFileList.size > MAX_FILE_COUNT) {
                    logFileList.sortBy {
                        val titles = it.name.split("&")
                        titles[1].formatDateMil("yyyy-MM-dd") + titles[2].toLong()
                    }
                    val extroCount = logFileList.size - NORMAL_FILE_COUNT
                    logFileList.forEachIndexed { index, file ->
                        if (index < extroCount) {
                            file.delete()
                        }
                    }

                    //拿到今日0时的时间戳
                    val currentTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    //过滤出今日之前的sp的key
                    val oldKeys = logSp.all.keys.filter {
                        it.split("&")[1].formatDateMil("yyyy-MM-dd") < currentTime
                    }
                    //删除对应的key
                    oldKeys.forEach {
                        logSp.edit().remove(it).apply()
                    }
                }
            }
        }catch (e : Exception){

        }
    }

    private fun getLogDir(): File {
        val dirFile = getExternalStorageDirectory() ?: getContext().externalCacheDir
        val destFile = File(dirFile, LOG_DIR_NAME)
        destFile.mkdirs()
        return destFile
    }

    private fun getExternalStorageDirectory(): File? {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED != state) {
            return null
        }
        return Environment.getExternalStorageDirectory()
    }

    private fun getContext(): Context {
        application = application ?: getApplication()
        return application!!
    }

    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    private fun getApplication(): Application? {
        var application: Application? = null
        var method: Method
        try {
            method =
                Class.forName("android.app.AppGlobals").getDeclaredMethod("getInitialApplication")
            method.isAccessible = true
            application = method.invoke(null) as Application
        } catch (e: Exception) {
            try {
                method = Class.forName("android.app.ActivityThread")
                    .getDeclaredMethod("currentApplication")
                method.isAccessible = true
                application = method.invoke(null) as Application
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
        return application
    }
}