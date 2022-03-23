package com.daaniikusnanta.githubuserapp

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: SettingPreferences) : ViewModel() {
    private val _listUsers = MutableLiveData<List<UsersResponseItem>>()
    val listUsers: LiveData<List<UsersResponseItem>> = _listUsers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var lastQuery: String = emptyQuery

    companion object{
        private const val TAG = "MainViewModel"
        const val emptyQuery = "\"\""
    }

    init {
        searchUsers()
    }

    private fun searchUsers() {
        searchUsers(lastQuery)
    }

    fun searchUsers(query: String) {
        lastQuery = query
        _isLoading.value = true

        val client = ApiConfig.getApiService().getSearchedUsers(query)
        client.enqueue(object : Callback<SearchedUsersResponse> {
            override fun onResponse(
                call: Call<SearchedUsersResponse>,
                response: Response<SearchedUsersResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listUsers.value = response.body()?.items
                } else {
                    Log.e(TAG, "onResponse onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<SearchedUsersResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    class Factory (private val pref: SettingPreferences) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(pref) as T
        }
    }
}