package com.example.emotionapp.ui.components.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.emotionapp.ui.theme.AccentBlue
import com.example.emotionapp.ui.theme.BackgroundBeige
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing
import com.example.emotionapp.ui.theme.SurfaceWhite

@Composable
fun NotificationPermissionToggle(isPermissionGranted: Boolean, onToggle: () -> Unit) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(SurfaceWhite, RoundedCornerShape(Spacing.M))
                            .padding(Spacing.M),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
                text = "알림 설정",
                fontSize = FontSizes.SemiBold,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBrown
        )

        Switch(
                checked = isPermissionGranted,
                onCheckedChange = { onToggle() },
                colors =
                        SwitchDefaults.colors(
                                checkedThumbColor = SurfaceWhite,
                                checkedTrackColor = AccentBlue,
                                uncheckedThumbColor = PrimaryBrown,
                                uncheckedTrackColor = BackgroundBeige,
                                uncheckedBorderColor = PrimaryBrown
                        )
        )
    }
}
