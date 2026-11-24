#include <jni.h>
#include <cmath>

extern "C" JNIEXPORT jfloat JNICALL
Java_com_devil_aimbot_NativePid_calculate(JNIEnv *env, jobject thiz, jfloat target, jfloat current, jfloat dt) {
    static float integral = 0.0f;
    static float prev_error = 0.0f;
    float kp = 0.8f, ki = 0.1f, kd = 0.05f;
    float error = target - current;
    integral += error * dt;
    float derivative = (error - prev_error) / dt;
    float output = (kp * error) + (ki * integral) + (kd * derivative);
    prev_error = error;
    return output;
}
