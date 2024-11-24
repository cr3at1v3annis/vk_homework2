package com.example.homework2

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class GiphyRepository(api: String) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://$api")
        .addConverterFactory(Json{ignoreUnknownKeys = true}.asConverterFactory("application/json; charset=UTF8".toMediaType()))
        .build()



    private val giphyApi = retrofit.create(GiphyApi::class.java)


    suspend fun requestNTrendingGifs(apiKey: String, count: Int): GiphyListRequest? {
        val response = giphyApi.getNTrendingGifs(apiKey, count)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return body
            }
        }
        return null
    }

}