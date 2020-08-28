//
// Created by zhouzhenliang on 2020/8/28.
//

#ifndef FILESNAPSHOT_SLFILE_H
#define FILESNAPSHOT_SLFILE_H

#include <sys/stat.h>

class SlFile {

public:
    SlFile(const char *path);

    bool isFile();



private:
    struct stat buf;
};


#endif //FILESNAPSHOT_SLFILE_H
