package com.devil.aimbot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {
    private val REQ_SCREEN = 1001
    private val REQ_SHIZUKU = 1002
    private val REQ_NOTIF = 1003
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(50, 50, 50, 50) }
        statusTextView = TextView(this).apply { text = "HimmiAimbot V6.0\nStatus: Ready" }
        layout.addView(statusTextView)
// إضافة كود الاتصال بالشوزوكي
ShizukuHelper.checkAndRequest(this) {
    // هذا الكود سيعمل فقط إذا وافق المستخدم على صلاحية الشوزوكي
    android.util.Log.d("ShizukuTest", "تم الاتصال بالشوزوكي بنجاح!")
    // هنا يمكنك وضع أمر بدء تسجيل الشاشة أو الخدمة في الخلفية إذا أردت
}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQ_NOTIF)
            }
        }

        val btnShizuku = Button(this).apply { text = "1. GRANT SHIZUKU" }
        btnShizuku.setOnClickListener {
            try {
                if (!Shizuku.pingBinder()) {
                    statusTextView.text = "Status: Shizuku is NOT running!\nPlease open Shizuku app and start it."
                    // محاولة فتح تطبيق Shizuku تلقائياً
                    val intent = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
                    if (intent != null) startActivity(intent)
                    return@setOnClickListener
                }
                if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                    Shizuku.requestPermission(REQ_SHIZUKU)
                } else {
                    statusTextView.text = "Status: Shizuku Granted!"
                }
            } catch (e: Exception) {
                statusTextView.text = "Shizuku Error: ${e.message}"
            }
        }
        layout.addView(btnShizuku)

        val btnStart = Button(this).apply { text = "2. START AIMBOT" }
        btnStart.setOnClickListener {
            if (!Shizuku.pingBinder() || Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                statusTextView.text = "Status: Please grant Shizuku first!\n(If it says NOT running, open Shizuku app)"
                return@setOnClickListener
            }
            try {
                val pm = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                startActivityForResult(pm.createScreenCaptureIntent(), REQ_SCREEN)
            } catch (e: Exception) {
                statusTextView.text = "Screen Capture Error: ${e.message}"
            }
        }
        layout.addView(btnStart)
        setContentView(layout)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_SCREEN && resultCode == RESULT_OK && data != null) {
            try {
                val intent = Intent(this, AimService::class.java).apply {
                    putExtra("code", resultCode)
                    putExtra("data", data)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
                statusTextView.text = "Status: Aimbot Running in Background!"
                Toast.makeText(this, "Aimbot Started!", Toast.LENGTH_LONG).show()
                finish()
            } catch (e: Exception) {
                statusTextView.text = "Service Error: ${e.message}"
            }
        }
    }
}
