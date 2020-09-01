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


#define TAG "APP_CLEAN"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)


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

// test

extern "C"
JNIEXPORT jlong JNICALL
Java_com_sh_app_utils_NativeUtils_nativeOpenDir(JNIEnv *env, jobject thiz, jstring path) {
    const char *ch_path = env->GetStringUTFChars(path, JNI_FALSE);
    DIR *dir = opendir(ch_path);
    env->ReleaseStringUTFChars(path, ch_path);
    if (dir == NULL) {
        return -1L;
    } else {
        return (jlong) dir;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sh_app_utils_NativeUtils_nativeChDir(JNIEnv *env, jobject thiz, jstring path) {
    const char *ch_path = env->GetStringUTFChars(path, JNI_FALSE);
    chdir(ch_path);
    env->ReleaseStringUTFChars(path, ch_path);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_sh_app_utils_NativeUtils_nativeReadDir(JNIEnv *env, jobject thiz, jlong dir) {
    if (dir == -1L) {
        return NULL;
    }

    DIR *pdir = (DIR *) dir;
    struct dirent *dirent = readdir(pdir);
    if (dirent == NULL) {
        return -1L;
    }
    return (jlong) dirent;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sh_app_utils_NativeUtils_nativeGetDirentType(JNIEnv *env, jobject thiz, jlong dirent) {
    if (dirent == -1L) {
        return -1;
    }

    struct dirent *pdirent = (struct dirent *) dirent;
    return pdirent->d_type;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sh_app_utils_NativeUtils_nativeGetDirentName(JNIEnv *env, jobject thiz, jlong dirent) {
    if (dirent == -1L) {
        return NULL;
    }

    struct dirent *pdirent = (struct dirent *) dirent;
    return env->NewStringUTF(pdirent->d_name);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sh_app_utils_NativeUtils_nativeCloseDir(JNIEnv *env, jobject thiz, jlong dir) {
    if (dir == -1L) {
        return;
    }
    closedir((DIR *) dir);
}