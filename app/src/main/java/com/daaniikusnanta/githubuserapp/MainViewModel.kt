package com.daaniikusnanta.githubuserapp

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _listUsers = MutableLiveData<List<UsersResponseItem>>()
    val listUsers: LiveData<List<UsersResponseItem>> = _listUsers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object{
        private const val TAG = "MainViewModel"
    }

    init {
        getUsers()
    }

    private fun getUsers() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUsers()
        client.enqueue(object : Callback<List<UsersResponseItem>> {
            override fun onResponse(
                call: Call<List<UsersResponseItem>>,
                response: Response<List<UsersResponseItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listUsers.value = responseBody
                    }
                } else {
                    Log.e(TAG, "onResponse onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<UsersResponseItem>>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "getUsers() onFailure: ${t.message}")
            }
        })
    }
}