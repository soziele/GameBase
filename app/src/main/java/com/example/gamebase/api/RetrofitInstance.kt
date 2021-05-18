package com.example.gamebase.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.gamebase.util.Constants.Companion.BASE_URL

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: GamesApi by lazy {
        retrofit.create(GamesApi::class.java)
    }
}