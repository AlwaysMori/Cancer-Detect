package com.dicoding.asclepius.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val retrofitInstance = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val newsApiService: NewsApiService = retrofitInstance.create(NewsApiService::class.java)
}
