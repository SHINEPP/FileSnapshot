package com.sh.app.base.filetravel

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object TravelNodePool {

    private val executor: ThreadPoolExecutor = ThreadPoolExecutor(4, 8, 2,
            TimeUnit.SECONDS, LinkedBlockingQueue())

    private var isRejected = false

    init {
        executor.setRejectedExecutionHandler { _, _ ->
            isRejected = true
        }
    }

    @Synchronized
    fun execute(runnable: () -> Unit): Boolean {
        isRejected
        executor.execute(runnable)
        return isRejected
    }
}