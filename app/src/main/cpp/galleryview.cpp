#include <jni.h>

extern "C"{
#include "fftools/ffmpeg.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_example_galleryview_model_FFmpegUtils_run(JNIEnv *env, jclass clazz,
                                                   jobjectArray commands) {
    int argc = (*env).GetArrayLength(commands);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        auto js = (jstring) (*env).GetObjectArrayElement(commands, i);
        argv[i] = (char*) (*env).GetStringUTFChars(js, 0);
    }
    ffmpeg_exec(argc, argv);
    // TODO: implement run()
}
}