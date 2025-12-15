package com.example.emotionapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {
    private const val BASE_URL = "http://ceprj2.gachon.ac.kr:65042/"

    private val retrofit =
            Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}
