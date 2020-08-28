//
// Created by zhouzhenliang on 2020/8/28.
//

#include <sys/stat.h>
#include <dirent.h>
#include <cstring>
#include <jni.h>


long long common_get_file_size(const char *path) {
    if (path == NULL || strlen(path) == 0) {
        return 0;
    }

    struct stat buf;
    if (stat(path, &buf) != 0) {
        return 0;
    }
    return buf.st_size;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_sh_app_utils_NativeUtils_nativeGetFileSize(JNIEnv *env, jobject thiz, jstring path) {
    const char *ch_path = env->GetStringUTFChars(path, JNI_FALSE);
    long long size = common_get_file_size(ch_path);
    env->ReleaseStringUTFChars(path, ch_path);
    return size;
}