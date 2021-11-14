#include <jni.h>
#include <customlog.h>
#include <android/log.h>

<<<<<<< HEAD
extern "C"{
=======
extern "C" {
>>>>>>> 5ae44dd (implement x264 encoder)
#include <ffmpeg.h>
JNIEXPORT void JNICALL
Java_com_example_galleryview_model_FFmpegUtils_run(JNIEnv *env, jclass clazz,
                                                   jobjectArray commands) {
    int argc = (*env).GetArrayLength(commands);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        auto js = (jstring) (*env).GetObjectArrayElement(commands, i);

<<<<<<< HEAD
        argv[i] = (char*) (*env).GetStringUTFChars(js, 0);
        __android_log_print(ANDROID_LOG_VERBOSE,"FFmpeg","%s",argv[i]);
    }

    int resultCode=ffmpeg_exec(argc,argv);
    jmethodID returnResult = (*env).GetStaticMethodID(clazz,"onProcessResult","(Z)V");
    if (nullptr == returnResult)
    {
=======
        argv[i] = (char *) (*env).GetStringUTFChars(js, 0);
    }

    int resultCode = ffmpeg_exec(argc, argv);
    jmethodID returnResult = (*env).GetStaticMethodID(clazz, "onProcessResult", "(Z)V");
    if (nullptr == returnResult) {
>>>>>>> 5ae44dd (implement x264 encoder)
        LOGE("can't find method getStringFromStatic from JniHandle ");
        return;
    }

<<<<<<< HEAD
    (*env).CallStaticVoidMethod(clazz,returnResult,resultCode);
=======
    (*env).CallStaticVoidMethod(clazz, returnResult, resultCode);
>>>>>>> 5ae44dd (implement x264 encoder)
}
}