package com.example.homework2
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface GiphyApi {

    @GET("v1/gifs/trending")
    suspend fun getNTrendingGifs(@Query("api_key") apiKey: String, @Query("limit") limit: Int): Response<GiphyListRequest>

}