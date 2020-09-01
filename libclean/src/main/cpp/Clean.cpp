#include <sys/stat.h>
#include <dirent.h>
#include <cstring>
#include <jni.h>
#include <unistd.h>
#include <malloc.h>
#include <android/log.h>
#include "SpaceScan.h"

bool checkFlag(BaseScan *baseScanner) {
    return baseScanner->checkFlag == 861024;
}

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
Java_com_sl_clean_NativeUtils_nativeGetFileSize(JNIEnv *env, jobject thiz, jstring path) {
    const char *ch_path = env->GetStringUTFChars(path, JNI_FALSE);
    long long size = common_get_file_size(ch_path);
    env->ReleaseStringUTFChars(path, ch_path);
    return size;
}

// space scan

extern "C"
JNIEXPORT jlong JNICALL
Java_com_sl_clean_SpaceScan_nativeCreateScanSpace(JNIEnv *env, jobject thiz,
                                                              jstring path, jint deep) {
    const char *root_dir = env->GetStringUTFChars(path, JNI_FALSE);
    SpaceScan *spaceScanner = new SpaceScan(root_dir, deep);
    env->ReleaseStringUTFChars(path, root_dir);
    return (jlong) spaceScanner;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sl_clean_SpaceScan_nativeStartScanSpace(JNIEnv *env, jobject thiz,
                                                             jlong token, jobject callback) {
    if (token <= 0) {
        return;
    }

    jclass javaClass = env->GetObjectClass(callback);
    jmethodID methodId = env->GetMethodID(javaClass, "onProgress", "(ILjava/lang/String;JJ)V");

    SpaceScan *spaceScanner = (SpaceScan *) token;
    if (!checkFlag(spaceScanner)) {
        return;
    }

    spaceScanner->scan(env, callback, methodId);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sl_clean_SpaceScan_nativeCancelScanSpace(JNIEnv *env, jobject thiz,
                                                              jlong token) {
    if (token <= 0) {
        return;
    }

    SpaceScan *spaceScanner = (SpaceScan *) token;
    if (!checkFlag(spaceScanner)) {
        return;
    }

    spaceScanner->cancel();
    delete spaceScanner;
}