package com.daaniikusnanta.githubuserapp

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.daaniikusnanta.githubuserapp.database.UserItem
import com.daaniikusnanta.githubuserapp.databinding.ActivityFavoriteUsersBinding

class FavoriteUsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteUsersBinding
    private val favoriteUsersViewModel by viewModels<FavoriteUsersViewModel> {
        FavoriteUsersViewModel.Factory(
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.favorite_users_actionbar_title)

        binding.rvUsers.setHasFixedSize(true)

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvUsers.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.rvUsers.layoutManager = LinearLayoutManager(this)
        }

        favoriteUsersViewModel.getFavoriteUsers().observe(this) {
            if (it != null) setUsersData(it)
        }
    }

    private fun setUsersData(users: List<UserItem>) {
        val listUserAdapter = ListUserAdapter(ArrayList(users))
        binding.rvUsers.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserItem) {
                showUserDetail(data)
            }
        })
    }

    private fun showUserDetail(user: UserItem) {
        val moveIntent = Intent(this@FavoriteUsersActivity, DetailUserActivity::class.java)
        moveIntent.putExtra(DetailUserActivity.EXTRA_USER, user)
        startActivity(moveIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}