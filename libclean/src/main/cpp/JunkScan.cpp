#include <cstring>
#include "JunkScan.h"

void JunkScan::scan(JNIEnv *env, jobject callback, jmethodID methodId) {
    this->env = env;
    this->callback = callback;
    this->methodId = methodId;

    hasCanceled = false;

    hasCanceled = true;
}

bool JunkScan::isPathMatching(const char *path, int pos) {
    if (strlen(path) == pos) {
        return true;
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
        return false;
    }

    section[index] = '\0';

    if (isRegex((char *) &section)) {

    } else {

    }
}

void JunkScan::cancel() {
    hasCanceled = true;
}

void JunkScan::onProgress(int type, const char *path, long long size, long long lastModified) {
    env->CallVoidMethod(callback, methodId, type, env->NewStringUTF(path), size, lastModified);
}

bool JunkScan::isRegex(const char *path) {
    return strlen(path) > 2 && path[0] == '@' && path[1] == 'R';
}
