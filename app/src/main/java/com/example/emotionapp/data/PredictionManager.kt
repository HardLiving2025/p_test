package com.example.emotionapp.data

import android.content.Context
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.os.Handler
import android.os.Looper

data class RiskAnalysis(
    val level: String, // DANGER, WARNING, NORMAL, GOOD
    val score: Int,
    val vulnerableCategory: String,
    val condition: String,
    val message: String,
    val title: String
)

data class UsagePrediction(
    val hasPrediction: Boolean,
    val startTime: String,
    val endTime: String,
    val targetCategory: String,
    val probabilityPercent: Double,
    val message: String
)

data class Recommendation(
    val title: String,
    val description: String
)

data class PredictionResponse(
    val userId: Int,
    val analysisDate: String,
    val riskAnalysis: RiskAnalysis,
    val usagePrediction: UsagePrediction,
    val recommendations: List<Recommendation>
)

object PredictionManager {
    private const val BASE_URL = "http://ceprj2.gachon.ac.kr:65042/api/prediction/today"
    private val client = OkHttpClient()

    fun fetchPrediction(context: Context, onResult: (PredictionResponse?) -> Unit) {
        val token = com.example.emotionapp.data.local.TokenManager(context).getAccessToken()
        val request = Request.Builder()
            .url(BASE_URL)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("PredictionManager", "Failed to fetch prediction", e)
                postResult(onResult, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("PredictionManager", "Server Error: ${response.code}")
                        postResult(onResult, null)
                        return
                    }

                    val bodyStr = response.body?.string()
                    if (bodyStr == null) {
                        postResult(onResult, null)
                        return
                    }

                    try {
                        val json = JSONObject(bodyStr)
                        val riskObj = json.getJSONObject("risk_analysis")
                        val riskAnalysis = RiskAnalysis(
                            level = riskObj.optString("level"),
                            score = riskObj.optInt("score"),
                            vulnerableCategory = riskObj.optString("vulnerable_category"),
                            condition = riskObj.optString("condition"),
                            message = riskObj.optString("message"),
                            title = riskObj.optString("title")
                        )

                        val predObj = json.getJSONObject("usage_prediction")
                        val usagePrediction = UsagePrediction(
                            hasPrediction = predObj.optBoolean("has_prediction"),
                            startTime = predObj.optString("start_time"),
                            endTime = predObj.optString("end_time"),
                            targetCategory = predObj.optString("target_category"),
                            probabilityPercent = predObj.optDouble("probability_percent"),
                            message = predObj.optString("message")
                        )

                        val recList = mutableListOf<Recommendation>()
                        val recArr = json.optJSONArray("recommendations")
                        if (recArr != null) {
                            for (i in 0 until recArr.length()) {
                                val item = recArr.getJSONObject(i)
                                recList.add(Recommendation(
                                    title = item.optString("title"),
                                    description = item.optString("description")
                                ))
                            }
                        }

                        val result = PredictionResponse(
                            userId = json.optInt("user_id"),
                            analysisDate = json.optString("analysis_date"),
                            riskAnalysis = riskAnalysis,
                            usagePrediction = usagePrediction,
                            recommendations = recList
                        )
                        postResult(onResult, result)
                    } catch (e: Exception) {
                        Log.e("PredictionManager", "Parsing error", e)
                        postResult(onResult, null)
                    }
                }
            }
        })
    }

    private fun <T> postResult(onResult: (T?) -> Unit, data: T?) {
        Handler(Looper.getMainLooper()).post { onResult(data) }
    }
}
