package com.sh.app.base.filetravel

import java.util.concurrent.Executors
import kotlin.math.max

object TravelThreadPool {

    private var THREAD_COUNT: Int = Runtime.getRuntime().availableProcessors()
    private val mulExecutorService = Executors.newFixedThreadPool(2 * max(4, THREAD_COUNT) + 1)

    fun execute(action: () -> Unit) {
        mulExecutorService.execute(action)
    }
}