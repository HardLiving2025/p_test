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
import androidx.compose.ui.unit.sp
import com.example.emotionapp.ui.theme.*

@Composable
fun MoodSelector(onNext: () -> Unit) {

    var selectedMood by remember { mutableStateOf<String?>(null) }

    val moods =
            listOf(
                    Triple("good", "😊", "좋음"),
                    Triple("normal", "🙂", "보통"),
                    Triple("sad", "😞", "나쁨")
            )

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(BackgroundBeige) // 여기서 사용됨
                            .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Text(text = "오늘 기분은 어떠세요?", fontSize = FontSizes.Title, color = PrimaryBrown)

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            moods.forEach { (id, emoji, _) ->
                Box(
                        modifier =
                                Modifier.size(96.dp)
                                        .background(SurfaceWhite, RoundedCornerShape(24.dp))
                                        .border(
                                                width = 4.dp,
                                                color =
                                                        if (selectedMood == id)
                                                                AccentBlue // 선택된 색 (#CAF1FF)
                                                        else SecondaryBeige, // 비선택 상태 (#D2BDA8)
                                                shape = RoundedCornerShape(24.dp)
                                        )
                                        .clip(RoundedCornerShape(24.dp))
                                        .clickable { selectedMood = id },
                        contentAlignment = Alignment.Center
                ) { Text(text = emoji, fontSize = 36.sp) }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
                onClick = { if (selectedMood != null) onNext() },
                enabled = selectedMood != null,
                modifier = Modifier.fillMaxWidth(),
                colors =
                        ButtonDefaults.buttonColors(
                                containerColor = PrimaryBrown, // 버튼 배경
                                disabledContainerColor = DisabledGray // 비활성화 버튼 색
                        )
        ) { Text(text = "확인 →", color = SurfaceWhite, fontSize = FontSizes.Normal) }
    }
}
