package com.daaniikusnanta.githubuserapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: UserItem)

    @Delete
    fun delete(user: UserItem)

    @Query("SELECT * from favorite_user ORDER BY username ASC")
    fun getAllFavoriteUsers(): LiveData<List<UserItem>>

    @Query("SELECT EXISTS (SELECT username from favorite_user WHERE username = :username)" )
    fun isFavorite(username: String): LiveData<Boolean>
}