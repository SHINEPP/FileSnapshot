package com.sh.app.modules.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import java.io.File

fun File.browsingText(context: Context) {
    val intent = Intent(context, BrowsingTextActivity::class.java)
    intent.putExtra(BrowsingTextActivity.EXTRA_KEY_FILE_PATH, path)
    if (context !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}