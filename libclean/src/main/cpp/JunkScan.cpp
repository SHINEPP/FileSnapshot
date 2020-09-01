#include <cstring>
#include <dirent.h>
#include <unistd.h>
#include "JunkScan.h"

void JunkScan::scan(JNIEnv *env, jobject callback, jmethodID methodId) {
    this->env = env;
    this->callback = callback;
    this->methodId = methodId;

    hasCanceled = false;

    hasCanceled = true;
}

void JunkScan::isPathMatching(const char *path, int pos) {
    if (strlen(path) == pos) {
        return;
    }

    char section[512] = {0};
    int index = 0;

    char ch;
    while ((ch = path[pos++]) != '\0' && index < 511) {
        if (ch == '/') {
            if (index == 0) {
                continue;
            }
            break;
        }
        section[index++] = ch;
    }

    if (index == 511) {
        return;
    }

    // 提取出目录分割片段
    section[index] = '\0';

    if (isRegexSection((char *) &section)) {
        char name[256] = {0};
        while (isRegexMatching(section, (char *) &name)) {
            chdir(name);
            isPathMatching(path, pos);
            chdir("..");
        }
    } else {
        if (isExistPath(section)) {
            chdir(section);
            isPathMatching(path, pos);
            chdir("..");
        }
    }
}

bool JunkScan::isRegexMatching(const char *regex, const char *path) {
    return false;
}

bool JunkScan::isExistPath(const char *path) {
    DIR *dir = opendir(path);
    if (dir == NULL) {
        return false;
    }

    bool result = false;
    struct dirent *entry = NULL;
    while ((entry = readdir(dir)) != NULL) {
        if (strcmp(path, entry->d_name) == 0) {
            result = true;
            break;
        }
    }
    closedir(dir);

    return result;
}

void JunkScan::cancel() {
    hasCanceled = true;
}

void JunkScan::onProgress(int type, const char *path, long long size, long long lastModified) {
    env->CallVoidMethod(callback, methodId, type, env->NewStringUTF(path), size, lastModified);
}

bool JunkScan::isRegexSection(const char *path) {
    return strlen(path) > 2 && path[0] == '@' && path[1] == 'R';
}
