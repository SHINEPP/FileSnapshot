//
// Created by zhouzhenliang on 2020/8/28.
//

#include <sys/stat.h>
#include <dirent.h>
#include <cstring>
#include <cstdio>
#include <jni.h>
#include <unistd.h>


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

void fun(const char *path) {

    DIR *dir = opendir(path);
    if (dir == NULL) {
        return;
    }


    closedir(dir);
}


void printdir(char *dir, int depth) {
    DIR *pDir;
    struct dirent *pDirent;
    struct stat stat_buf;

    if ((pDir = opendir(dir)) == NULL) {
        fprintf(stderr, "Can`t open directory %s\n", dir);
        return;
    }

    chdir(dir);
    while ((pDirent = readdir(pDir)) != NULL) {
        lstat(pDirent->d_name, &stat_buf);
        if (S_ISDIR(stat_buf.st_mode)) {
            if (strcmp(pDirent->d_name, ".") == 0 || strcmp(pDirent->d_name, "..") == 0) {
                continue;
            }

            printf("%*s%s/\n", depth, "", pDirent->d_name);
            printdir(pDirent->d_name, depth + 4);
        } else {
            printf("%*s%s\n", depth, "", pDirent->d_name);
        }
    }
    chdir("..");
    closedir(pDir);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_sh_app_utils_NativeUtils_nativeGetFileSize(JNIEnv *env, jobject thiz, jstring path) {
    const char *ch_path = env->GetStringUTFChars(path, JNI_FALSE);
    long long size = common_get_file_size(ch_path);
    env->ReleaseStringUTFChars(path, ch_path);
    return size;
}