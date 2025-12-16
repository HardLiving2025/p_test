package com.example.emotionapp.data.model

data class GoogleAuthRequest(val id_token: String, val fcm_token: String?)

data class TokenResponse(
        val access_token: String,
        val token_type: String,
        val user_id: Int,
        val nickname: String?
)
