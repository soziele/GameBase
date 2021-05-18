package com.example.gamebase.model

data class Game(var key: String = "", var title: String = "", var releaseYear: String = "", var genres: List<GameData.Genre?>? = listOf(GameData.Genre()), var imageUrl: String = "", var description: String = "", var note: String = "", var played: Boolean = false){

}