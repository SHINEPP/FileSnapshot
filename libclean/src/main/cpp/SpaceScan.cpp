#include <dirent.h>
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

void SpaceScan::scan(JNIEnv *env, jobject callback, jmethodID methodId, int typeFlag) {
    this->env = env;
    this->callback = callback;
    this->methodId = methodId;
    this->typeFlag = typeFlag;

    hasCanceled = false;
    if (rootPath != NULL && strlen(rootPath) != 0) {
        travel(rootPath, 0);
    }
    hasCanceled = true;
}

void SpaceScan::cancel() {
    hasCanceled = true;
}

void SpaceScan::travel(const char *path, int deep) {
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
            handleFile(entry->d_name);
            continue;
        }

        if (entry->d_type == DT_DIR) {
            if (strcmp(".", entry->d_name) == 0
                || strcmp("..", entry->d_name) == 0) {
                continue;
            }

            travel(entry->d_name, deep + 1);
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

void SpaceScan::handleFile(const char *name) {

    if ((typeFlag & TYPE_DOCUMENT) != 0x0) {
        for (int i = 0; i < document_extension_count; i++) {
            if (isNameMatching(name, document_extension[i])) {
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
    }

    if ((typeFlag & TYPE_IMAGE) != 0x0) {
        for (int i = 0; i < image_extension_count; i++) {
            if (isNameMatching(name, image_extension[i])) {
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
    }

    if ((typeFlag & TYPE_AUDIO) != 0x0) {
        for (int i = 0; i < audio_extension_count; i++) {
            if (isNameMatching(name, audio_extension[i])) {
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
    }

    if ((typeFlag & TYPE_VIDEO) != 0x0) {
        for (int i = 0; i < video_extension_count; i++) {
            if (isNameMatching(name, video_extension[i])) {
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
    }

    if ((typeFlag & TYPE_APK) != 0x0) {
        if (isNameMatching(name, ".apk") || isNameMatching(name, ".apk.1")) {
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
}

bool SpaceScan::isNameMatching(const char *name, const char *extension) {
    int extensionLength = strlen(extension);
    if (extensionLength >= 64) {
        return false;
    }

    int nameLength = strlen(name);
    if (nameLength <= extensionLength) {
        return false;
    }

    char lowCastTmp[64] = {0};
    for (int i = nameLength - extensionLength, j = 0; i < nameLength; i++, j++) {
        if (name[i] >= 65 && name[i] <= 90) {
            lowCastTmp[j] = (char) (name[i] + 32);
        } else {
            lowCastTmp[j] = name[i];
        }
    }

    return strcmp((char *) &lowCastTmp, extension) == 0;
}

void SpaceScan::onProgress(int type, const char *path, long long size, long long lastModified) {
    env->CallVoidMethod(callback, methodId, type, env->NewStringUTF(path), size, lastModified);
}
