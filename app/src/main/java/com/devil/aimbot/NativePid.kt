package com.devil.aimbot
class NativePid {
    companion object {
        init { System.loadLibrary("himmi_pid") }
    }
    external fun calculate(target: Float, current: Float, dt: Float): Float
}
