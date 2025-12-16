package com.example.emotionapp.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.IOException
import okhttp3.*
import org.json.JSONObject

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

data class Recommendation(val title: String, val description: String)

data class PredictionDescriptionResponse(val title: String, val description: String)

data class PredictionResponse(
        val userId: Int,
        val analysisDate: String,
        val riskAnalysis: RiskAnalysis,
        val usagePrediction: UsagePrediction,
        val recommendations: List<Recommendation>
)

object PredictionManager {
        private const val BASE_URL = "http://ceprj2.gachon.ac.kr:65042/api/prediction/today"
        private const val DESCRIPTION_URL =
                "http://ceprj2.gachon.ac.kr:65042/api/prediction/description"
        private val client = OkHttpClient()

        private var cachedResponse: PredictionResponse? = null
        private var cachedDescription: PredictionDescriptionResponse? = null

        fun fetchPrediction(context: Context, onResult: (PredictionResponse?) -> Unit) {
                if (cachedResponse != null) {
                        onResult(cachedResponse)
                        return
                }
                fetchData(context, BASE_URL) { bodyStr ->
                        if (bodyStr == null) {
                                onResult(null)
                                return@fetchData
                        }
                        try {
                                val json = JSONObject(bodyStr)
                                val riskObj = json.getJSONObject("risk_analysis")
                                val riskAnalysis =
                                        RiskAnalysis(
                                                level = riskObj.optString("level"),
                                                score = riskObj.optInt("score"),
                                                vulnerableCategory =
                                                        riskObj.optString("vulnerable_category"),
                                                condition = riskObj.optString("condition"),
                                                message = riskObj.optString("message"),
                                                title = riskObj.optString("title")
                                        )

                                val predObj = json.getJSONObject("usage_prediction")
                                val usagePrediction =
                                        UsagePrediction(
                                                hasPrediction =
                                                        predObj.optBoolean("has_prediction"),
                                                startTime = predObj.optString("start_time"),
                                                endTime = predObj.optString("end_time"),
                                                targetCategory =
                                                        predObj.optString("target_category"),
                                                probabilityPercent =
                                                        predObj.optDouble("probability_percent"),
                                                message = predObj.optString("message")
                                        )

                                val recList = mutableListOf<Recommendation>()
                                val recArr = json.optJSONArray("recommendations")
                                if (recArr != null) {
                                        for (i in 0 until recArr.length()) {
                                                val item = recArr.getJSONObject(i)
                                                recList.add(
                                                        Recommendation(
                                                                title = item.optString("title"),
                                                                description =
                                                                        item.optString(
                                                                                "description"
                                                                        )
                                                        )
                                                )
                                        }
                                }

                                val result =
                                        PredictionResponse(
                                                userId = json.optInt("user_id"),
                                                analysisDate = json.optString("analysis_date"),
                                                riskAnalysis = riskAnalysis,
                                                usagePrediction = usagePrediction,
                                                recommendations = recList
                                        )
                                cachedResponse = result
                                onResult(result)
                        } catch (e: Exception) {
                                Log.e("PredictionManager", "Parsing error", e)
                                onResult(null)
                        }
                }
        }

        fun fetchDescription(context: Context, onResult: (PredictionDescriptionResponse?) -> Unit) {
                if (cachedDescription != null) {
                        onResult(cachedDescription)
                        return
                }
                fetchData(context, DESCRIPTION_URL) { bodyStr ->
                        if (bodyStr == null) {
                                onResult(null)
                                return@fetchData
                        }
                        try {
                                val json = JSONObject(bodyStr)
                                val result =
                                        PredictionDescriptionResponse(
                                                title = json.optString("title"),
                                                description = json.optString("description")
                                        )
                                cachedDescription = result
                                onResult(result)
                        } catch (e: Exception) {
                                Log.e("PredictionManager", "Desc Parsing error", e)
                                onResult(null)
                                postResult(onResult, null)
                        }
                }
        }

        private fun fetchData(context: Context, url: String, onBody: (String?) -> Unit) {
                val token = com.example.emotionapp.data.local.TokenManager(context).getAccessToken()
                val request =
                        Request.Builder()
                                .url(url)
                                .addHeader("Authorization", "Bearer $token")
                                .get()
                                .build()

                client.newCall(request)
                        .enqueue(
                                object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                                Log.e(
                                                        "PredictionManager",
                                                        "Failed to fetch $url",
                                                        e
                                                )
                                                postResult(onBody, null)
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                                response.use {
                                                        if (!response.isSuccessful) {
                                                                Log.e(
                                                                        "PredictionManager",
                                                                        "Server Error: ${response.code}"
                                                                )
                                                                postResult(onBody, null)
                                                                return
                                                        }
                                                        val body = response.body?.string()
                                                        postResult(onBody, body)
                                                }
                                        }
                                }
                        )
        }

        private fun <T> postResult(onResult: (T?) -> Unit, data: T?) {
                Handler(Looper.getMainLooper()).post { onResult(data) }
        }
}
