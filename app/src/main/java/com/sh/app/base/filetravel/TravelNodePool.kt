package com.sh.app.base.filetravel

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object TravelNodePool {

    private val executor: ThreadPoolExecutor = ThreadPoolExecutor(2, 8, 1,
            TimeUnit.SECONDS, LinkedBlockingQueue())

    init {
        executor.setRejectedExecutionHandler { _, _ ->
        }
    }

    @Synchronized
    fun execute(runnable: () -> Unit) {
        executor.execute(runnable)
    }
}