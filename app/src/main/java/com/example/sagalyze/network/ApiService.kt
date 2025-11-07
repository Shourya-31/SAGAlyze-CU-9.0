package com.example.sagalyze.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class PredictionResponse(
    val status: String,
    val fine_label: String?,
    val unified_category: String?,
//    val results: List<ResultItem>?,
    val results: List<List<Any>>?,
    val images: Images?
)

data class ResultItem(
    val rank: Int,
    val disease: String,
    val confidence: Double,
    val description: String
)

data class Images(
    val marked_output: String?,
    val heatmap: String?,
    val overlay: String?
)

interface ApiService {
    @Multipart
    @POST("/predict")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<PredictionResponse>
}
