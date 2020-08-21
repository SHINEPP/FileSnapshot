package com.sh.app.utils

import android.net.Uri
import androidx.core.content.FileProvider
import com.sh.app.OptApplication
import java.io.File

class SlFileProvider : FileProvider() {

    companion object {

        fun getUriForFile(file: File): Uri {
            return getUriForFile(OptApplication.context, OptApplication.context.packageName + ".sl_file_provider", file)
        }
    }
}

fun File.getShareURI(): Uri {
    return SlFileProvider.getUriForFile(this)
}

