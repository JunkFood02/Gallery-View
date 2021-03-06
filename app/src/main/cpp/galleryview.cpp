#include <jni.h>
#include <customlog.h>
#include <android/log.h>


extern "C" {

#include <ffmpeg.h>
JNIEXPORT void JNICALL
Java_com_example_galleryview_model_FFmpegUtils_run(JNIEnv *env, jclass clazz,
                                                   jobjectArray commands) {
    int argc = (*env).GetArrayLength(commands);
    char *argv[argc + 2];
    int i;
    for (i = 0; i < argc; i++) {
        argv[i] = (char *) malloc(sizeof(char) * 100);
        auto js = (jstring) (*env).GetObjectArrayElement(commands, i);
        strcpy(argv[i], (char *) (*env).GetStringUTFChars(js, 0));
        __android_log_print(ANDROID_LOG_VERBOSE, "FFmpeg", "%s", argv[i]);
    }
    __android_log_print(ANDROID_LOG_VERBOSE, "FFmpeg", "%s", argv[argc]);
    int resultCode = ffmpeg_exec(argc, argv);
    jmethodID returnResult = (*env).GetStaticMethodID(clazz, "onProcessResult", "(Z)V");
    if (nullptr == returnResult) {
        LOGE("can't find method");
        return;
    }
    (*env).CallStaticVoidMethod(clazz, returnResult, resultCode);
    //调用Java代码内名为onProcessResult的函数，并将resultCode作为参数传入
}
}