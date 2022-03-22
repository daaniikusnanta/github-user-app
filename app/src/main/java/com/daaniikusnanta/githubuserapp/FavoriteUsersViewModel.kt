package com.daaniikusnanta.githubuserapp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daaniikusnanta.githubuserapp.repository.FavoriteUserRepository

class FavoriteUsersViewModel(application: Application) : ViewModel() {
    private val favoriteUserRepository: FavoriteUserRepository = FavoriteUserRepository(application)

    fun getFavoriteUsers() = favoriteUserRepository.getAllFavoriteUsers()

    class Factory (private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoriteUsersViewModel(application) as T
        }
    }
}