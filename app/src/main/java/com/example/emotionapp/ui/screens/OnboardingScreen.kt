package com.example.emotionapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotionapp.R
import com.example.emotionapp.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
        var currentPage by remember { mutableStateOf(0) }

        val pages = remember {
                listOf(
                        OnboardingPageData(
                                title = "Screen Comma.",
                                subtitle = "멈춤이 필요한 순간에 찍는 쉼표",
                                isStart = true
                        ),
                        OnboardingPageData(
                                title = "얼마나 썼는지보다,\n어떻게 썼는지",
                                description = "단순 시간 대신 행동 패턴을 분석해\n나도 몰랐던 사용 습관을 보여줍니다.",
                                features = listOf("앱 사용 흐름 시각화", "반복·집중 사용 구간 분석", "패턴 중심 인사이트 제공")
                        ),
                        OnboardingPageData(
                                title = "감정과 상태를\n함께 기록",
                                description = "하루의 기분과 상황을 간단하게 남겨\n사용 패턴의 흐름을 이해할 수 있어요.",
                                features =
                                        listOf(
                                                "자유로운 감정, 상태 기록",
                                                "하루 2회, 부담 없는 체크",
                                                "개인 맞춤형 사용 패턴 분석",
                                        )
                        ),
                        OnboardingPageData(
                                title = "아무 때나\n개입하지 않습니다",
                                description = "가장 받아들이기 쉬운 순간에만\n필요한 알림을 전달합니다.",
                                features =
                                        listOf(
                                                "피곤하거나 집중이 흐트러질 때",
                                                "사용이 길어질 때만 개입",
                                                "최소한의 알림, 낮은 거부감"
                                        )
                        ),
                        OnboardingPageData(
                                title = "차단이 아닌,\n스스로의 선택",
                                description = "억지로 막지 않고\n스스로 멈출 수 있는 여유를 제공합니다.",
                                features = listOf("강제 차단 없음", "죄책감 없는 기록", "작은 쉼표 같은 변화")
                        )
                )
        }

        val handleNext = {
                if (currentPage < pages.lastIndex) {
                        currentPage++
                } else {
                        onComplete()
                }
        }

        val density = LocalDensity.current

        Column(modifier = Modifier.fillMaxSize().background(OnboardingBackground)) {
                // 콘텐츠 영역
                Box(
                        modifier =
                                Modifier.weight(1f)
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 48.dp),
                        contentAlignment = Alignment.Center
                ) {
                        AnimatedContent(
                                targetState = currentPage,
                                transitionSpec = {
                                        val duration = 1100 // 1.1초 (기존 0.3초 + 0.8초)
                                        val offsetPx =
                                                with(density) {
                                                        20.dp.roundToPx()
                                                } // 약 20dp 정도의 미세한 이동

                                        // React: x: 20 (enter) -> 0 -> x: -20 (exit)
                                        if (targetState > initialState) {
                                                // Enter: 오른쪽(20)에서 등장
                                                (fadeIn(animationSpec = tween(duration)) +
                                                                slideInHorizontally(
                                                                        animationSpec =
                                                                                tween(duration)
                                                                ) { offsetPx })
                                                        .togetherWith(
                                                                // Exit: 왼쪽(-20)으로 퇴장
                                                                fadeOut(
                                                                        animationSpec =
                                                                                tween(duration)
                                                                ) +
                                                                        slideOutHorizontally(
                                                                                animationSpec =
                                                                                        tween(
                                                                                                duration
                                                                                        )
                                                                        ) { -offsetPx }
                                                        )
                                        } else {
                                                // (혹시 모를 뒤로가기) Enter: 왼쪽(-20)에서 등장
                                                (fadeIn(animationSpec = tween(duration)) +
                                                                slideInHorizontally(
                                                                        animationSpec =
                                                                                tween(duration)
                                                                ) { -offsetPx })
                                                        .togetherWith(
                                                                // Exit: 오른쪽(20)으로 퇴장
                                                                fadeOut(
                                                                        animationSpec =
                                                                                tween(duration)
                                                                ) +
                                                                        slideOutHorizontally(
                                                                                animationSpec =
                                                                                        tween(
                                                                                                duration
                                                                                        )
                                                                        ) { offsetPx }
                                                        )
                                        }
                                },
                                label = "OnboardingPageTransition"
                        ) { page ->
                                val data = pages[page]
                                Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                ) {
                                        // 아이콘 (첫 페이지에만 표시)
                                        if (data.isStart) {
                                                Box(
                                                        modifier =
                                                                Modifier.size(128.dp)
                                                                        .clip(
                                                                                RoundedCornerShape(
                                                                                        32.dp
                                                                                )
                                                                        )
                                                                        .background(PrimaryBrown)
                                                                        .padding(bottom = 0.dp),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Image(
                                                                painter =
                                                                        painterResource(
                                                                                id =
                                                                                        R.mipmap
                                                                                                .ic_launcher_foreground
                                                                        ),
                                                                contentDescription = "App Icon",
                                                                modifier = Modifier.fillMaxSize()
                                                        )
                                                }
                                                Spacer(modifier = Modifier.height(32.dp))
                                        }

                                        // 타이틀 (개별 애니메이션 제거, 페이지 전환에 포함)
                                        Text(
                                                text = data.title,
                                                color = PrimaryBrown,
                                                fontSize = FontSizes.Title,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                lineHeight = 36.sp,
                                                modifier = Modifier.padding(bottom = 16.dp)
                                        )

                                        // 서브타이틀 (첫 페이지)
                                        if (data.subtitle != null) {
                                                Text(
                                                        text = data.subtitle,
                                                        color = PrimaryBrown.copy(alpha = 0.7f),
                                                        fontSize = FontSizes.Normal,
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.padding(bottom = 48.dp)
                                                )
                                        }

                                        // 설명 (나머지 페이지들)
                                        if (data.description != null) {
                                                Text(
                                                        text = data.description,
                                                        color = PrimaryBrown.copy(alpha = 0.7f),
                                                        fontSize = FontSizes.Normal,
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.padding(bottom = 32.dp)
                                                )
                                        }

                                        // 특징 리스트
                                        if (data.features.isNotEmpty()) {
                                                Column(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(155.dp) // 높이 고정
                                                                        .shadow(
                                                                                elevation = 8.dp,
                                                                                shape =
                                                                                        RoundedCornerShape(
                                                                                                16.dp
                                                                                        ),
                                                                                spotColor =
                                                                                        Color.Black
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.08f
                                                                                                )
                                                                        )
                                                                        .background(
                                                                                SurfaceWhite,
                                                                                RoundedCornerShape(
                                                                                        16.dp
                                                                                )
                                                                        )
                                                                        .padding(24.dp),
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(16.dp)
                                                ) {
                                                        data.features.forEachIndexed {
                                                                index,
                                                                feature ->
                                                                FeatureItem(feature, index)
                                                        }
                                                }
                                        }
                                }
                        }
                }

                // 하단 네비게이션
                Column(
                        modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                        // 페이지 인디케이터
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                pages.forEachIndexed { index, _ ->
                                        val isSelected = index == currentPage
                                        val width by
                                                animateDpAsState(
                                                        if (isSelected) 32.dp else 8.dp,
                                                        label = "indicatorWidth"
                                                )
                                        val color = if (isSelected) PrimaryBrown else SecondaryBeige

                                        Box(
                                                modifier =
                                                        Modifier.padding(horizontal = 4.dp)
                                                                .height(8.dp)
                                                                .width(width)
                                                                .clip(CircleShape)
                                                                .background(color)
                                        )
                                }
                        }

                        // 다음/시작하기 버튼
                        Button(
                                onClick = { handleNext() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = PrimaryBrown,
                                                contentColor = SurfaceWhite
                                        )
                        ) {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                ) {
                                        AnimatedContent(
                                                targetState = currentPage,
                                                label = "ButtonText"
                                        ) { page ->
                                                Text(
                                                        text =
                                                                when {
                                                                        page == 0 -> "시작하기"
                                                                        page == pages.lastIndex ->
                                                                                "완료"
                                                                        else -> "다음"
                                                                },
                                                        fontSize = FontSizes.Normal
                                                )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                                imageVector = Icons.Filled.ChevronRight,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                        )
                                }
                        }
                }
        }
}

data class OnboardingPageData(
        val title: String,
        val subtitle: String? = null,
        val description: String? = null,
        val features: List<String> = emptyList(),
        val isStart: Boolean = false
)

@Composable
fun FeatureItem(text: String, index: Int) {
        // React: delay: index * 0.1 + 0.2 (200ms + 100ms * index)
        // React: initial y: 10

        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
                delay(200L + index * 100L)
                visible = true
        }

        AnimatedVisibility(
                visible = visible,
                enter =
                        fadeIn(tween(500)) +
                                slideInVertically(tween(500)) { 20 } // 약 10 y값 대응 (픽셀단위는 다르지만 작게)
        ) {
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                        Text(
                                text = "✨",
                                color = PrimaryBrown.copy(alpha = 0.5f),
                                fontSize = FontSizes.Normal
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = text, color = PrimaryBrown, fontSize = FontSizes.Normal)
                }
        }
}
