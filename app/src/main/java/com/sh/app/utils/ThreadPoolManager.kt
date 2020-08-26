package com.sh.app.utils

import java.util.concurrent.Executors

object ThreadPoolManager {

    private var THREAD_COUNT: Int = Runtime.getRuntime().availableProcessors()
    private val mulExecutorService = Executors.newFixedThreadPool(1)

    fun requestExecute(action: () -> Unit) {
        mulExecutorService.execute(action)
    }
}