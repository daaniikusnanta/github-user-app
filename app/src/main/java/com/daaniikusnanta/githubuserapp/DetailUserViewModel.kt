package com.daaniikusnanta.githubuserapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daaniikusnanta.githubuserapp.database.UserItem
import com.daaniikusnanta.githubuserapp.repository.FavoriteUserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserViewModel(username: String, application: Application): ViewModel() {
    private val _userDetail = MutableLiveData<UserDetailResponse>()
    val userDetail: LiveData<UserDetailResponse> = _userDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val favoriteUserRepository: FavoriteUserRepository = FavoriteUserRepository(application)

    val isFavorite: LiveData<Boolean> = favoriteUserRepository.isFavorite(username)

    fun setUserFavorite(user: UserItem, isFavorite: Boolean) {
        if (isFavorite) favoriteUserRepository.insert(user)
        else favoriteUserRepository.delete(user)
    }

    companion object{
        private const val TAG = "UserDetailViewModel"
    }

    fun getUserDetail(username : String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUser(username)
        client.enqueue(object : Callback<UserDetailResponse> {
            override fun onResponse(
                call: Call<UserDetailResponse>,
                response: Response<UserDetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _userDetail.value = response.body()
                } else {
                    Log.e(TAG, "onResponse onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "getUsers() onFailure: ${t.message}")
            }
        })
    }

    class Factory (private val username: String, private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailUserViewModel(username, application) as T
        }
    }
}