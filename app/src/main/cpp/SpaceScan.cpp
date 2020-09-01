#include <cstring>
#include <dirent.h>
#include <cstdio>
#include <sys/stat.h>
#include <unistd.h>
#include <filesystem>
#include "SpaceScan.h"


SpaceScan::SpaceScan(const char *rootPath, int maxDeep) {
    if (rootPath != NULL && strlen(rootPath) < maxPathLen - 1) {
        strcpy(this->rootPath, rootPath);
    } else {
        strcpy(this->rootPath, "\0");
    }

    this->maxDeep = maxDeep;
    hasCanceled = false;
}

void SpaceScan::scan(JNIEnv *env, jobject callback, jmethodID methodId) {
    this->env = env;
    this->callback = callback;
    this->methodId = methodId;

    hasCanceled = false;
    if (rootPath != NULL && strlen(rootPath) != 0) {
        findMatchedFile(rootPath, 0);
    }
    hasCanceled = true;
}

void SpaceScan::cancel() {
    hasCanceled = true;
}

void SpaceScan::findMatchedFile(const char *path, int deep) {
    if (hasCanceled || deep >= maxDeep) {
        return;
    }

    DIR *dir = opendir(path);
    if (dir == NULL) {
        return;
    }

    struct dirent *entry = NULL;
    chdir(path);
    while ((entry = readdir(dir)) != NULL) {
        if (hasCanceled) {
            break;
        }

        if (entry->d_type == DT_REG) {
            checkFile(entry->d_name);
            continue;
        }

        if (entry->d_type == DT_DIR) {
            if (strcmp(".", entry->d_name) == 0
                || strcmp("..", entry->d_name) == 0) {
                continue;
            }

            findMatchedFile(entry->d_name, deep + 1);
        }
    }
    chdir("..");
    closedir(dir);
}

static const int video_extension_count = 21;
static const char *video_extension[] = {".mp4", ".mkv", ".mpg", ".mpeg", ".mpe",
                                        ".avi", ".rm", ".rmvb", ".mov", ".wmv",
                                        ".vob", ".divx", ".asf", ".3gp", ".webm",
                                        ".swf", ".bdmv", ".3gpp", ".f4v", ".xvid",
                                        ".mpeg4"};

static const int image_extension_count = 10;
static const char *image_extension[] = {".png", ".jpg", ".jpeg", ".gif", ".psd",
                                        ".svg", ".ai", ".ps", ".tif", ".tiff"};

static const int audio_extension_count = 16;
static const char *audio_extension[] = {".mp3", ".cda", ".wav", ".ape", ".flac",
                                        ".aac", ".ogg", ".wma", ".m4a", ".mid",
                                        ".wave", ".caf", ".m4r", ".m3u", ".ac3",
                                        ".mka"};

static const int document_extension_count = 18;
static const char *document_extension[] = {".txt", ".doc", ".hlp", ".wps", ".ftf",
                                           ".html", ".pdf", ".docx", ".xls", ".ppt",
                                           ".pptx", ".csv", ".epub", ".mobi", ".rtf",
                                           ".pages", ".number", ".key"};

void SpaceScan::checkFile(const char *name) {
    for (int i = 0; i < video_extension_count; i++) {
        if (matchExtension(name, video_extension[i])) {
            struct stat buf;
            if (lstat(name, &buf) == 0) {
                char abs_path[maxPathLen];
                if (realpath(name, abs_path) != NULL) {
                    onProgress(TYPE_VIDEO, abs_path, buf.st_size, buf.st_mtim.tv_sec);
                }
            }
            return;
        }
    }

    for (int i = 0; i < image_extension_count; i++) {
        if (matchExtension(name, image_extension[i])) {
            struct stat buf;
            if (lstat(name, &buf) == 0) {
                char abs_path[maxPathLen];
                if (realpath(name, abs_path) != NULL) {
                    onProgress(TYPE_IMAGE, abs_path, buf.st_size, buf.st_mtim.tv_sec);
                }
            }
            return;
        }
    }

    for (int i = 0; i < audio_extension_count; i++) {
        if (matchExtension(name, audio_extension[i])) {
            struct stat buf;
            if (lstat(name, &buf) == 0) {
                char abs_path[maxPathLen];
                if (realpath(name, abs_path) != NULL) {
                    onProgress(TYPE_AUDIO, abs_path, buf.st_size, buf.st_mtim.tv_sec);
                }
            }
            return;
        }
    }

    for (int i = 0; i < document_extension_count; i++) {
        if (matchExtension(name, document_extension[i])) {
            struct stat buf;
            if (lstat(name, &buf) == 0) {
                char abs_path[maxPathLen];
                if (realpath(name, abs_path) != NULL) {
                    onProgress(TYPE_DOCUMENT, abs_path, buf.st_size, buf.st_mtim.tv_sec);
                }
            }
            return;
        }
    }

    if (matchExtension(name, ".apk") || matchExtension(name, ".apk.1")) {
        struct stat buf;
        if (lstat(name, &buf) == 0) {
            char abs_path[maxPathLen];
            if (realpath(name, abs_path) != NULL) {
                onProgress(TYPE_APK, abs_path, buf.st_size, buf.st_mtim.tv_sec);
            }
        }
        return;
    }
}

bool SpaceScan::matchExtension(const char *path, const char *extension) {
    int ext_len = strlen(extension);
    if (ext_len >= 64) {
        return false;
    }

    int path_len = strlen(path);
    if (path_len <= ext_len) {
        return false;
    }

    char tmp[64] = {0};
    for (int i = path_len - ext_len, j = 0; i < path_len; i++, j++) {
        if (path[i] >= 65 && path[i] <= 90) {
            tmp[j] = (char) (path[i] + 32);
        } else {
            tmp[j] = path[i];
        }
    }

    int ret = strcmp((char *) &tmp, extension);
    return ret == 0;
}

void SpaceScan::onProgress(int type, const char *path, long long size, long long lastModified) {
    env->CallVoidMethod(callback, methodId, type, env->NewStringUTF(path), size, lastModified);
}
