package com.sh.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import java.io.File

@Throws(Exception::class)
fun File.openViaOtherApp(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    val fileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        getShareURI()
    } else {
        Uri.fromFile(this)
    }

    val lastName = path.substringAfterLast(".")
    intent.setDataAndType(fileUri, MimeUtils.getMime(lastName))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
            or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(intent)
}
