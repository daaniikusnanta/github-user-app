package com.daaniikusnanta.githubuserapp

//import android.content.Intent
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.daaniikusnanta.githubuserapp.databinding.ActivityMainBinding
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val listUsers = ArrayList<UsersResponseItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvUsers.setHasFixedSize(true)

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvUsers.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.rvUsers.layoutManager = LinearLayoutManager(this)
        }

        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
        mainViewModel.listUsers.observe(this, { listUsers ->
            setUsersData(listUsers)
        })

        mainViewModel.isLoading.observe(this, {
            showLoading(it)
        })
    }

    private fun showUserDetail(username: String) {
        val moveIntent = Intent(this@MainActivity, DetailUserActivity::class.java)
        moveIntent.putExtra(DetailUserActivity.EXTRA_USERNAME, username)
        startActivity(moveIntent)
    }

    private fun setUsersData(users: List<UsersResponseItem>) {

        listUsers.addAll(users)

        val listUserAdapter = ListUserAdapter(listUsers)
        binding.rvUsers.adapter = listUserAdapter

        binding.tvListCount.text = StringBuilder().append("Showing ").append(listUsers.size).append(" users")

        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UsersResponseItem) {
                showUserDetail(data.login)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}