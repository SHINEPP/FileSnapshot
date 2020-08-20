package com.sh.app.snapshot

// SHA1 转目录
fun String.sha1ToObjectPath(): String {
    return "${substring(0, 2)}/${substring(2)}"
}

fun String.sha1ToObjectLeftPath(): String {
    return substring(0, 2)
}

fun String.sha1ToObjectRightPath(): String {
    return substring(2)
}

// 有效性判断
fun String.isValidSha1(): Boolean {
    return length == 40
}

fun String.isNotValidSha1(): Boolean {
    return length != 40
}