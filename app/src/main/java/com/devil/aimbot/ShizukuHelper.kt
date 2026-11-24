package com.devil.aimbot

import android.app.Activity
import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import android.util.Log

object ShizukuHelper {
    private const val REQUEST_CODE = 1001
    private var listener: Shizuku.OnRequestPermissionResultListener? = null

    fun checkAndRequest(activity: Activity, onGranted: () -> Unit) {
        if (!Shizuku.pingBinder()) {
            Log.e("ShizukuHelper", "Shizuku is not running")
            return
        }
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            listener = object : Shizuku.OnRequestPermissionResultListener {
                override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                    if (requestCode == REQUEST_CODE && grantResult == PackageManager.PERMISSION_GRANTED) {
                        onGranted()
                    }
                    listener?.let { Shizuku.removeRequestPermissionResultListener(it) }
                }
            }
            listener?.let { Shizuku.addRequestPermissionResultListener(it) }
            Shizuku.requestPermission(REQUEST_CODE)
        }
    }
}
