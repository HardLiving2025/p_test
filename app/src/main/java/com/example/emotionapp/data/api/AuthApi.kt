package com.example.emotionapp.data.api

import com.example.emotionapp.data.model.GoogleAuthRequest
import com.example.emotionapp.data.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleAuthRequest): Response<TokenResponse>
}
