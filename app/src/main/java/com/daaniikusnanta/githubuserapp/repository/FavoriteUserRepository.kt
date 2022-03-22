package com.daaniikusnanta.githubuserapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.daaniikusnanta.githubuserapp.database.UserItem
import com.daaniikusnanta.githubuserapp.database.FavoriteUserDao
import com.daaniikusnanta.githubuserapp.database.FavoriteUserRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteUserRepository(application: Application) {
    private val favoriteUserDao: FavoriteUserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteUserRoomDatabase.getDatabase(application)
        favoriteUserDao = db.favoriteUserDao()
    }

    fun getAllFavoriteUsers(): LiveData<List<UserItem>> = favoriteUserDao.getAllFavoriteUsers()

    fun insert(user: UserItem) {
        executorService.execute { favoriteUserDao.insert(user) }
    }

    fun delete(user: UserItem) {
        executorService.execute { favoriteUserDao.delete(user) }
    }

    fun isFavorite(username: String) = favoriteUserDao.isFavorite(username)

}