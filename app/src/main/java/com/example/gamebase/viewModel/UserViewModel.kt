package com.example.gamebase.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebase.model.Game
import com.example.gamebase.model.GameData
import com.example.gamebase.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel(private val repository: Repository): ViewModel() {

    val gameResponse: MutableLiveData<Response<GameData>> = MutableLiveData()
    var darkMode = false

    fun getGame(gameName: String){
        viewModelScope.launch {
            val response = repository.getGame(gameName,"80a6eec344894496b1958c8ba3373a71")
            gameResponse.value = response
        }
    }
}