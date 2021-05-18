package com.example.gamebase.api

import com.example.gamebase.model.Game
import com.example.gamebase.model.GameData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GamesApi {
    @GET("api/games/{gameSlug}")
    suspend fun getGame(
        @Path("gameSlug") gameName: String,
        @Query("key") apiKey: String
    ): Response<GameData>
}