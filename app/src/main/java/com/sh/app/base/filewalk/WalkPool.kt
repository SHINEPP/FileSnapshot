package com.sh.app.base.filewalk

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object WalkPool {

    private var THREAD_COUNT: Int = Runtime.getRuntime().availableProcessors()
    private val executor: ThreadPoolExecutor = ThreadPoolExecutor(2, THREAD_COUNT, 1,
            TimeUnit.SECONDS, LinkedBlockingQueue())

    init {
        executor.setRejectedExecutionHandler { _, _ -> }
    }

    fun execute(runnable: () -> Unit) {
        executor.execute(runnable)
    }
}