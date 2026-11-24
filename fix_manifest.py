import re

path = 'app/src/main/AndroidManifest.xml'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add permissions before <application>
permissions = '''
    <uses-permission android:name="moe.shizuku.manager.permission.API_V23" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION" />
'''
content = content.replace('<application', permissions + '<application', 1)

# 2. Add ShizukuProvider before </application>
provider = '''
        <provider android:name="rikka.shizuku.ShizukuProvider"
            android:authorities="${applicationId}.shizuku"
            android:enabled="true"
            android:exported="true"
            android:multiprocess="false"
            android:permission="android.permission.INTERACT_ACROSS_USERS_FULL" />
'''
content = content.replace('</application>', provider + '</application>', 1)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
    print("✅ AndroidManifest.xml fixed successfully.")
