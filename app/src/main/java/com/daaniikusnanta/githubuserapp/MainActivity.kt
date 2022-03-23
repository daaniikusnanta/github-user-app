package com.daaniikusnanta.githubuserapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.daaniikusnanta.githubuserapp.database.UserItem
import com.daaniikusnanta.githubuserapp.databinding.ActivityMainBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listUserAdapter: ListUserAdapter
    private var isDarkModeActive: Boolean = false
    private val mainViewModel by viewModels<MainViewModel> {
        MainViewModel.Factory(
            SettingPreferences.getInstance(dataStore)
        )
    }

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

        mainViewModel.listUsers.observe(this@MainActivity, { listUsers ->
            setUsersData(listUsers)
        })

        mainViewModel.isLoading.observe(this@MainActivity, {
            showLoading(it)
        })

        mainViewModel.getThemeSettings().observe(this, {isDarkModeActive: Boolean ->
            this.isDarkModeActive = isDarkModeActive
            if(isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            invalidateOptionsMenu()
        })
    }

    private fun showUserDetail(user: UserItem) {
        val moveIntent = Intent(this@MainActivity, DetailUserActivity::class.java)
        moveIntent.putExtra(DetailUserActivity.EXTRA_USER, user)
        startActivity(moveIntent)
    }

    private fun setUsersData(users: List<UsersResponseItem>) {
        val listUsers = createUserItemsFromUsersResponse(users)

        listUserAdapter = ListUserAdapter(listUsers)
        binding.rvUsers.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserItem) {
                showUserDetail(data)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_top_app_bar, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val searchItem = menu.findItem(R.id.search)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                mainViewModel.searchUsers(query)
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                mainViewModel.searchUsers(MainViewModel.emptyQuery)
                return true
            }
        })

        val darkModeSwitch = menu.findItem(R.id.dark_mode_switch)
        if(isDarkModeActive) darkModeSwitch.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_light_mode_24)
        else darkModeSwitch.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_dark_mode_24)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_favorite -> {
                val moveIntent = Intent(this@MainActivity, FavoriteUsersActivity::class.java)
                startActivity(moveIntent)
                return true
            }
            R.id.dark_mode_switch -> {
                mainViewModel.saveThemeSetting(!isDarkModeActive)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}