#ifndef SL_SPACE_SCANNER_H
#define SL_SPACE_SCANNER_H


#include <jni.h>

#import "BaseScan.h"


class SpaceScan : public BaseScan {

public:
    SpaceScan(const char *rootPath, int maxDeep);

    void scan(JNIEnv *env, jobject callback, jmethodID methodId);

    void cancel();

private:
    void travel(const char *path, int deep);

    void handleFile(const char *name);

    bool isNameMatching(const char *name, const char *extension);

    void onProgress(int type, const char *path, long long size, long long lastModified);

private:
    static const int TYPE_VIDEO = 1;
    static const int TYPE_IMAGE = 2;
    static const int TYPE_AUDIO = 3;
    static const int TYPE_DOCUMENT = 4;
    static const int TYPE_APK = 5;

    JNIEnv *env = NULL;
    jobject callback = NULL;
    jmethodID methodId = NULL;

    int maxDeep;
    char rootPath[maxPathLen] = {'\0'};

    bool hasCanceled;
};


#endif // SL_SPACE_SCANNER_H
