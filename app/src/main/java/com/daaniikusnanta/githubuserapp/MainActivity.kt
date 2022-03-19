package com.daaniikusnanta.githubuserapp

import android.content.ContentValues.TAG
//import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.daaniikusnanta.githubuserapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val listUsers = ArrayList<UserResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvUsers.setHasFixedSize(true)

        getUsers()
    }

//    private fun showUserDetail(user: UserResponse) {
//        val moveIntent = Intent(this@MainActivity, DetailUserActivity::class.java)
//        moveIntent.putExtra(DetailUserActivity.EXTRA_USER, user)
//        startActivity(moveIntent)
//    }

    private fun getUsers() {
        showLoading(true)
        val client = ApiConfig.getApiService().getUsers()
        client.enqueue(object : Callback<UsersResponse> {
            override fun onResponse(
                call: Call<UsersResponse>,
                response: Response<UsersResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setUsersData(responseBody.users)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getUserData(user: UsersResponseItem) : UserResponse? {
        val client = ApiConfig.getApiService().getUser(user.login)
        var userData : UserResponse? = null
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        userData = responseBody
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return userData
    }

    private fun setUsersData(users: List<UsersResponseItem>) {
        for (user in users) {
            getUserData(user)?.let { listUsers.add(it) }
        }

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvUsers.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.rvUsers.layoutManager = LinearLayoutManager(this)
        }

        val listUserAdapter = ListUserAdapter(listUsers)
        binding.rvUsers.adapter = listUserAdapter

        binding.tvListCount.text = StringBuilder().append("Showing ").append(listUsers.size).append(" users")
//
//        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
//            override fun onItemClicked(data: UserResponse) {
//                showUserDetail(data)
//            }
//        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}