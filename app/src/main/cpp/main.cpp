//
// Created by zhouzhenliang on 2020/8/28.
//

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
Java_com_sh_app_utils_NativeUtils_nativeGetFileSize(JNIEnv *env, jobject thiz, jstring path) {
    const char *ch_path = env->GetStringUTFChars(path, JNI_FALSE);
    long long size = common_get_file_size(ch_path);
    env->ReleaseStringUTFChars(path, ch_path);
    return size;
}

void printdir(const char *path) {
    DIR *dir = opendir(path);
    if (dir == NULL) {
        return;
    }

    struct dirent *dirent;

    chdir(path);
    while ((dirent = readdir(dir)) != NULL) {
        if (dirent->d_type == DT_REG) {
            continue;
        }

        if (dirent->d_type == DT_DIR) {
            if (strcmp(dirent->d_name, ".") == 0 ||
                strcmp(dirent->d_name, "..") == 0) {
                continue;
            }
            printdir(dirent->d_name);
        }
    }
    chdir("..");
    closedir(dir);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sh_app_utils_NativeUtils_nativePrintDir(JNIEnv *env, jobject thiz, jstring path) {
    const char *ch_path = env->GetStringUTFChars(path, JNI_FALSE);
    printdir(ch_path);
    env->ReleaseStringUTFChars(path, ch_path);
}

// space scan

extern "C"
JNIEXPORT jlong JNICALL
Java_com_sh_app_modules_space_SpaceScan_nativeCreateScanSpace(JNIEnv *env, jobject thiz,
                                                              jstring path, jint deep) {
    const char *root_dir = env->GetStringUTFChars(path, JNI_FALSE);
    SpaceScan *spaceScanner = new SpaceScan(root_dir, deep);
    env->ReleaseStringUTFChars(path, root_dir);
    return (jlong) spaceScanner;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sh_app_modules_space_SpaceScan_nativeStartScanSpace(JNIEnv *env, jobject thiz,
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
Java_com_sh_app_modules_space_SpaceScan_nativeCancelScanSpace(JNIEnv *env, jobject thiz,
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