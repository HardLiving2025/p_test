package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emotionapp.data.UsageAnalysisManager
import com.example.emotionapp.ui.theme.*

val APP_NAME_MAP =
        mapOf(
                "com.kakao.talk" to "카카오톡",
                "com.instagram.android" to "인스타그램",
                "com.twitter.android" to "트위터",
                "com.discord" to "디스코드",
                "com.google.android.youtube" to "유튜브",
                "com.everytime.v2" to "에브리타임",
                "com.geode.launcher" to "지오메트리 대시",
                "gg.dak.bser" to "이터널 리턴",
                "com.robtopx.geometryjump" to "지오메트리 점프",
                "com.riotgames.league.teamfighttactics" to "TFT",
                "com.kurogame.wutheringwaves.global" to "명조",
                "com.spotify.music" to "스포티파이",
                "com.netflix.mediaclient" to "넷플릭스",
                "com.nhn.android.webtoon" to "네이버 웹툰",
                "com.android.chrome" to "크롬",
                "notion.id" to "노션",
                "com.samsung.android.app.notes" to "삼성 노트",
                "com.coupang.mobile" to "쿠팡",
                "com.nhn.android.nmap" to "네이버 지도",
                "com.samsung.android.dialer" to "알람",
                "com.samsung.android.incallui" to "통화",
                "com.sec.android.app.camera" to "카메라",
                "com.sec.android.gallery3d" to "갤러리",
                "com.sec.android.app.launcher" to "홈",
                "com.android.settings" to "설정",
                "com.samsung.android.easysetup" to "에이지 셋업",
                "com.kakaopay.app" to "카카오페이",
                "com.google.android.permissioncontroller" to "권한",
                "com.sec.android.app.smartcapture" to "캡처",
                "com.sec.android.app.clockpackage" to "알람",
                "com.samsung.android.calendar" to "캘린더",
                "com.dbs.kurly.m2" to "마켓컬리",
                "kr.co.simplebestapp.cheering" to "모두의 응원 - LED 전광판",
                "ch.protonvpn.android" to "ProtonVPN",
                "com.kbstar.kbbank" to "KB국민은행",
                "com.example.emotionapp" to "Emotion App",
                "com.google.android.gms" to "Google Play",
                "com.google.android.googlequicksearchbox" to "Google 검색",
                "com.kbcard.cxh.appcard" to "KB Pay",
                "com.samsung.android.lool" to "삼성 로컬",
                "com.sec.android.app.popupcalculator" to "계산기",
                "com.wrtn.app" to "뤼튼 AI",
                "com.android.htmlviewer" to "HTML 뷰어",
                "com.google.android.photopicker" to "사진 선택",
                "kr.coursemos.android2" to "코스모스",
                "com.openai.chatgpt" to "ChatGPT",
                "com.sec.android.app.voicenote" to "음성 노트",
                "host.exp.exponent" to "Expo",
                "com.burockgames.timclocker" to "StayFree",
        )

@Composable
fun KeyPatternsSection(period: com.example.emotionapp.ui.screens.Period) {
        val context = androidx.compose.ui.platform.LocalContext.current
        var insights by remember {
                mutableStateOf<List<UsageAnalysisManager.PatternInsight>>(emptyList())
        }

        LaunchedEffect(period) {
                UsageAnalysisManager.fetchMajorPatterns(context) { result ->
                        if (result != null) {
                                insights =
                                        when (period) {
                                                com.example.emotionapp.ui.screens.Period
                                                        .YESTERDAY -> result.yesterday
                                                com.example.emotionapp.ui.screens.Period.WEEK ->
                                                        result.week1
                                                com.example.emotionapp.ui.screens.Period
                                                        .TWO_WEEKS -> result.week2
                                                com.example.emotionapp.ui.screens.Period.MONTH ->
                                                        result.month1
                                        }
                        }
                }
        }

        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(SurfaceWhite, RoundedCornerShape(Spacing.L))
                                .padding(Spacing.CardInner)
        ) {
                Text(
                        text = "주요 패턴",
                        fontSize = FontSizes.SemiBold,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBrown
                )

                Spacer(modifier = Modifier.height(Spacing.M))

                if (insights.isEmpty()) {
                        Text(
                                text = "아직 분석된 패턴이 없습니다.",
                                fontSize = FontSizes.Normal,
                                color = DisabledGray,
                                modifier = Modifier.padding(vertical = Spacing.S)
                        )
                } else {
                        insights.forEach { insight ->
                                // 1. Extract name from description (before "은(는)")
                                val targetName =
                                        if (insight.description.contains("은(는)")) {
                                                insight.description.substringBefore("은(는)")
                                        } else {
                                                insight.title // Fallback
                                        }

                                // 2. Find Package Name
                                // Try to find key (package) where value matches targetName
                                // If not found, assume targetName itself is the package name
                                val packageName =
                                        APP_NAME_MAP.entries.find { it.value == targetName }?.key
                                                ?: targetName

                                // 3. Get System Label
                                val systemLabel =
                                        try {
                                                val pm = context.packageManager
                                                val appInfo = pm.getApplicationInfo(packageName, 0)
                                                pm.getApplicationLabel(appInfo).toString()
                                        } catch (
                                                e:
                                                        android.content.pm.PackageManager.NameNotFoundException) {
                                                targetName // Fallback to original if not found
                                        }

                                // 4. Replace in Description
                                val displayBody =
                                        insight.description.replace(targetName, systemLabel)

                                android.util.Log.d(
                                        "KeyPatternsSection",
                                        "Extracted: $targetName, Pkg: $packageName, Label: $systemLabel, Body: $displayBody"
                                )

                                val displayTitle = APP_NAME_MAP[insight.title] ?: insight.title
                                InsightBlock(chipText = displayTitle, body = displayBody)
                                Spacer(modifier = Modifier.height(Spacing.S))
                        }
                }
        }
}

private fun getSystemLabelForServerName(
        context: android.content.Context,
        serverName: String
): String {
        // 1. Reverse lookup: Value (App Name) -> Key (Package Name)
        val packageName = APP_NAME_MAP.entries.find { it.value == serverName }?.key
        if (packageName == null) return serverName

        // 2. PackageManager lookup: Package Name -> System Label
        return try {
                val pm = context.packageManager
                val appInfo = pm.getApplicationInfo(packageName, 0)
                pm.getApplicationLabel(appInfo).toString()
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
                serverName
        }
}

@Composable
private fun InsightBlock(chipText: String, body: String) {
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(BackgroundBeige, RoundedCornerShape(Spacing.M))
                                .padding(Spacing.M)
        ) {
                Box(
                        modifier =
                                Modifier.background(AccentBlue, RoundedCornerShape(999.dp))
                                        .padding(horizontal = Spacing.S, vertical = Spacing.XS)
                ) { Text(text = chipText, fontSize = FontSizes.Normal, color = PrimaryBrown) }
                Spacer(modifier = Modifier.height(Spacing.S))
                Text(
                        text = body,
                        fontSize = FontSizes.Normal,
                        color = PrimaryBrown.copy(alpha = 0.8f)
                )
        }
}
