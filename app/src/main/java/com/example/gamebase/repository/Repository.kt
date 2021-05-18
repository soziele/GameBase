package com.example.gamebase.repository

import com.example.gamebase.api.RetrofitInstance
import com.example.gamebase.model.Game
import com.example.gamebase.model.GameData
import retrofit2.Response

class Repository {
    suspend fun getGame(gameName: String, apiKey: String): Response<GameData> {
        return RetrofitInstance.api.getGame(gameName, apiKey)
    }
}