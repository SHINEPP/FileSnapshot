#ifndef SL_JUNK_SCAN_H
#define SL_JUNK_SCAN_H


#include <jni.h>
#include "BaseScan.h"

class JunkScan : public BaseScan {

public:
    void scan(JNIEnv *env, jobject callback, jmethodID methodId);

    void cancel();

private:

    void isPathMatching(const char *path, int pos);

    void onProgress(int type, const char *path, long long size, long long lastModified);

    static bool isRegexMatching(const char *regex, const char *path);

    static bool isExistPath(const char *path);

    static bool isRegexSection(const char *path);

private:
    JNIEnv *env = NULL;
    jobject callback = NULL;
    jmethodID methodId = NULL;

    bool hasCanceled;
};


#endif //SL_JUNK_SCAN_H
