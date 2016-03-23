//
// Created by JimmyZhang on 2016/3/22.
//
#include<stdio.h>
#include<stdlib.h>
#include<jni.h>
#include"com_zkjinshi_jni_aes_AESManager.h"

JNIEXPORT jstring JNICALL Java_com_zkjinshi_jni_aes_AESManager_getEncryptKey
  (JNIEnv * env, jobject clazz){
    return (*env)->NewStringUTF(env,"X2VOV0+W7szslb+@kd7d44Im&JUAWO0y");
}