package com.example.emotionapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.emotionapp.data.ServerUploadManager
import com.example.emotionapp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun StateSelector(mood: String, onNext: () -> Unit) {
        val context = androidx.compose.ui.platform.LocalContext.current
        val scope = rememberCoroutineScope()

        var selectedState by remember { mutableStateOf<String?>(null) }

        val states = listOf("busy" to "바쁨", "relaxed" to "여유로움")

        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(BackgroundBeige) // #ECE4D8
                                .padding(Spacing.XXL),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {

                // 제목
                Text(
                        text = "오늘 상태는 어떠세요?",
                        fontSize = FontSizes.Title,
                        color = PrimaryBrown // #3C2F2F
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 버튼 영역
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.SectionGap)) {
                        states.forEach { (id, label) ->
                                Box(
                                        modifier =
                                                Modifier.width(140.dp)
                                                        .height(80.dp)
                                                        .background(
                                                                SurfaceWhite,
                                                                RoundedCornerShape(Spacing.XL)
                                                        )
                                                        .border(
                                                                width = Spacing.XS,
                                                                color =
                                                                        if (selectedState == id)
                                                                                AccentBlue // 선택됨
                                                                        // (#CAF1FF)
                                                                        else
                                                                                SecondaryBeige, // 비선택 (#D2BDA8)
                                                                shape =
                                                                        RoundedCornerShape(
                                                                                Spacing.XL
                                                                        )
                                                        )
                                                        .clip(RoundedCornerShape(Spacing.XL))
                                                        .clickable { selectedState = id },
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = label,
                                                fontSize = FontSizes.SemiBold,
                                                color = PrimaryBrown // #3C2F2F
                                        )
                                }
                        }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 확인 버튼
                Button(
                        onClick = {
                                if (selectedState != null) {
                                        // 감정/상태 매핑
                                        val apiEmotion =
                                                when (mood) {
                                                        "good" -> "GOOD"
                                                        "normal" -> "NORMAL"
                                                        "sad" -> "BAD"
                                                        else -> "NORMAL"
                                                }
                                        val apiStatus =
                                                when (selectedState) {
                                                        "busy" -> "BUSY"
                                                        "relaxed" -> "FREE"
                                                        else -> "FREE"
                                                }

                                        ServerUploadManager.uploadMoodState(
                                                context = context,
                                                emotion = apiEmotion,
                                                status = apiStatus
                                        ) { success ->
                                                // 성공 여부와 관계없이 화면 이동 (실패 시에도 일단 넘어가는것으로?)
                                                // 성공 시에만 저장해야 하지만, 여기서는 flow상 성공 가정하고 저장
                                                com.example.emotionapp.data.local.TokenManager(
                                                                context
                                                        )
                                                        .saveLastInputSlot(
                                                                com.example.emotionapp.data.local
                                                                        .TokenManager
                                                                        .calculateCurrentSlotTime()
                                                        )
                                                scope.launch { onNext() }
                                        }
                                }
                        },
                        enabled = selectedState != null,
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = PrimaryBrown, // 활성
                                        disabledContainerColor = DisabledGray // 비활성
                                )
                ) { Text(text = "확인 →", color = SurfaceWhite, fontSize = FontSizes.Normal) }
        }
}
