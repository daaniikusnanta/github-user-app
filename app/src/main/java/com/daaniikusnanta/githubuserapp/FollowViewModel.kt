package com.daaniikusnanta.githubuserapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowViewModel(private val username: String, private val type: Int): ViewModel() {
    private val _followList = MutableLiveData<List<UsersResponseItem>>()
    val followList: LiveData<List<UsersResponseItem>> = _followList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val responseCallback: Callback<List<UsersResponseItem>> get() {
        return object : Callback<List<UsersResponseItem>> {
            override fun onResponse(
                call: Call<List<UsersResponseItem>>,
                response: Response<List<UsersResponseItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _followList.value = response.body()
                } else {
                    Log.e(TAG, "onResponse onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<UsersResponseItem>>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "getUsers() onFailure: ${t.message}")
            }
        }
    }

    init {
        getFollows()
    }

    private fun getFollows(){
        _isLoading.value = true

        val client = if (type == 0) ApiConfig.getApiService().getFollowers(username)
                    else ApiConfig.getApiService().getFollowing(username)
        client.enqueue(responseCallback)
    }

    companion object{
        private const val TAG = "UserDetailViewModel"
    }

    class Factory (private val username: String, private val type: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FollowViewModel(username, type) as T
        }
    }
}