package com.sh.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.sh.app.base.osscenter.OssCenter
import com.sh.app.utils.getCurrentProcessName

class OptApplication : Application() {

    companion object {
        lateinit var context: Context
            private set
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        context = base
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()

        when (getCurrentProcessName()) {
            packageName -> {
                initOnMainProcess()
            }
            "$packageName:service" -> {
                initOnServiceProcess()
            }
        }
    }

    private fun initOnMainProcess() {
    }

    private fun initOnServiceProcess() {

    }
}