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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotionapp.ui.theme.*

@Composable
fun StateSelector(onNext: () -> Unit) {

    var selectedState by remember { mutableStateOf<String?>(null) }

    val states = listOf(
        "busy" to "바쁨",
        "relaxed" to "여유로움"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)   // #ECE4D8
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 제목
        Text(
            text = "오늘 상태는 어떠세요?",
            fontSize = FontSizes.Title,
            color = PrimaryBrown           // #3C2F2F
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 버튼 영역
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            states.forEach { (id, label) ->
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(80.dp)    // size(width, height) → 오류 나는 부분 수정
                        .background(SurfaceWhite, RoundedCornerShape(20.dp))
                        .border(
                            width = 4.dp,
                            color = if (selectedState == id)
                                AccentBlue          // 선택됨 (#CAF1FF)
                            else
                                SecondaryBeige,     // 비선택 (#D2BDA8)
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedState = id },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = FontSizes.SemiBold,
                        color = PrimaryBrown       // #3C2F2F
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 확인 버튼
        Button(
            onClick = { if (selectedState != null) onNext() },
            enabled = selectedState != null,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBrown,          // 활성
                disabledContainerColor = DisabledGray // 비활성 
            )
        ) {
            Text(
                text = "확인 →",
                color = SurfaceWhite,
                fontSize = FontSizes.Normal
            )
        }
    }
}
