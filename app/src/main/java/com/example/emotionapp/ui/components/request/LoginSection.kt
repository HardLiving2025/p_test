package com.example.emotionapp.ui.components.request

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.emotionapp.R
import com.example.emotionapp.data.api.NetworkClient
import com.example.emotionapp.data.local.TokenManager
import com.example.emotionapp.data.model.GoogleAuthRequest
import com.example.emotionapp.ui.theme.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginSection(onLoginSuccess: () -> Unit) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val tokenManager = TokenManager(context)

        // TODO: Replace with your actual Google Client ID
        val GOOGLE_CLIENT_ID =
                "777869363840-51iuop5q12m7d1mh1fdnnksh76ga84us.apps.googleusercontent.com"

        fun handleGoogleLogin() {
                scope.launch {
                        try {
                                val credentialManager = CredentialManager.create(context)

                                val googleIdOption =
                                        GetGoogleIdOption.Builder()
                                                .setFilterByAuthorizedAccounts(false)
                                                .setServerClientId(GOOGLE_CLIENT_ID)
                                                .setAutoSelectEnabled(false)
                                                .build()

                                val request =
                                        GetCredentialRequest.Builder()
                                                .addCredentialOption(googleIdOption)
                                                .build()

                                val result =
                                        credentialManager.getCredential(
                                                request = request,
                                                context = context as Activity
                                        )

                                val credential = result.credential

                                if (credential is CustomCredential &&
                                                credential.type ==
                                                        GoogleIdTokenCredential
                                                                .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {

                                        val googleIdTokenCredential =
                                                GoogleIdTokenCredential.createFrom(credential.data)
                                        val idToken = googleIdTokenCredential.idToken

                                        Log.d(
                                                "LoginSection",
                                                "Google ID Token received: ${idToken.take(10)}..."
                                        )

                                        try {
                                                val fcmToken =
                                                        try {
                                                                FirebaseMessaging.getInstance()
                                                                        .token
                                                                        .await()
                                                        } catch (e: Exception) {
                                                                Log.e(
                                                                        "LoginSection",
                                                                        "FCM Token fetch failed",
                                                                        e
                                                                )
                                                                null
                                                        }

                                                val response =
                                                        NetworkClient.authApi.loginWithGoogle(
                                                                GoogleAuthRequest(
                                                                        id_token = idToken,
                                                                        fcm_token = fcmToken
                                                                )
                                                        )

                                                if (response.isSuccessful && response.body() != null
                                                ) {
                                                        val tokenResponse = response.body()!!
                                                        Log.d(
                                                                "LoginSection",
                                                                "Login Success: ${tokenResponse.nickname}"
                                                        )

                                                        tokenManager.saveAccessToken(
                                                                tokenResponse.access_token
                                                        )
                                                        onLoginSuccess()
                                                } else {
                                                        Log.e(
                                                                "LoginSection",
                                                                "Server login failed: ${response.code()}"
                                                        )
                                                        Toast.makeText(
                                                                        context,
                                                                        "서버 로그인 실패: ${response.code()}",
                                                                        Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                }
                                        } catch (e: Exception) {
                                                Log.e("LoginSection", "Network error", e)
                                                Toast.makeText(
                                                                context,
                                                                "서버 연결 오류: ${e.message}",
                                                                Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                        }
                                } else {
                                        Log.e(
                                                "LoginSection",
                                                "Unexpected credential type: ${credential.type}"
                                        )
                                }
                        } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                                Log.e("LoginSection", "Credential Manager Validation Failed", e)
                                Toast.makeText(context, "Google 로그인 취소 또는 실패", Toast.LENGTH_SHORT)
                                        .show()
                        } catch (e: Exception) {
                                Log.e("LoginSection", "Login process error", e)
                                Toast.makeText(context, "로그인 중 오류 발생", Toast.LENGTH_SHORT).show()
                        }
                }
        }

        Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                Text(
                        text = "Screen Comma",
                        color = PrimaryBrown,
                        fontSize = FontSizes.Title,
                        fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                        text = "멈춤이 필요한 순간에 찍는 쉼표",
                        color = PrimaryBrown.copy(alpha = 0.7f),
                        fontSize = FontSizes.Normal
                )

                Spacer(modifier = Modifier.height(48.dp))

                Row(
                        modifier =
                                Modifier.fillMaxWidth(0.8f)
                                        .shadow(
                                                elevation = 8.dp,
                                                shape = RoundedCornerShape(16.dp),
                                                spotColor = Color.Black.copy(alpha = 0.1f)
                                        )
                                        .background(SurfaceWhite, RoundedCornerShape(16.dp))
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { handleGoogleLogin() }
                                        .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                ) {
                        Icon(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                text = "Google로 시작하기",
                                color = PrimaryBrown,
                                fontSize = FontSizes.Normal,
                                fontWeight = FontWeight.Medium
                        )
                }
        }
}
