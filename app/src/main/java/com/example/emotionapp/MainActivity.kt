package com.example.emotionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.emotionapp.navigation.AppNav
import com.example.emotionapp.ui.components.common.UsagePermissionGate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    // ✅ 여기서 권한 체크 + 설정 화면 이동 처리
                    UsagePermissionGate {
                        AppNav()   // 원래 앱 네비게이션
                    }
                }
            }
        }
    }
}
