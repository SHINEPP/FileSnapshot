package com.sh.app.utils

import android.os.Handler
import android.os.Looper
import okhttp3.*
import okio.*
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

internal class FileDownloader(private val url: String, private val desFile: File) {

    interface DownloaderListener {
        fun onFinished(e: Exception?)
    }

    private val isLoading = AtomicBoolean()
    private var call: Call? = null

    constructor(url: String, desPath: String?) : this(url, File(desPath))

    fun load(downloaderListener: DownloaderListener?) {
        if (isLoading.get()) {
            return
        }
        isLoading.set(true)

        val request = Request.Builder().url(url).build()
        val okHttpClient = OkHttpClient()
        call = okHttpClient.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                isLoading.set(false)
                downloaderListener?.onFinished(e)
            }

            override fun onResponse(call: Call, response: Response) {
                var bufferedSink: BufferedSink? = null
                try {
                    val sink: Sink = desFile.sink()
                    bufferedSink = sink.buffer()
                    bufferedSink.writeAll(response.body!!.source())
                    bufferedSink.close()
                    Handler(Looper.getMainLooper()).post { downloaderListener?.onFinished(null) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Handler(Looper.getMainLooper()).post { downloaderListener?.onFinished(e) }
                } finally {
                    isLoading.set(false)
                    bufferedSink?.close()
                }
            }
        })
    }

    fun cancel() {
        val call = this.call ?: return
        if (isLoading.get() && call.isExecuted()) {
            call.cancel()
        }
    }
}