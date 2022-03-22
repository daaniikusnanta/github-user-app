package com.daaniikusnanta.githubuserapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.daaniikusnanta.githubuserapp.database.UserItem
import com.daaniikusnanta.githubuserapp.databinding.ActivityDetailUserBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.lang.StringBuilder

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var userDetail: UserDetailResponse
    private var isFavorite: Boolean = false
    private val detailUserViewModel by viewModels<DetailUserViewModel> {
        DetailUserViewModel.Factory(
            intent.getParcelableExtra<UserItem>(EXTRA_USER)?.username ?: "",
            application
        )
    }

    private lateinit var user: UserItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra<UserItem>(EXTRA_USER) as UserItem
        user.username.let { detailUserViewModel.getUserDetail(it) }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = StringBuilder().append(user.username).append("'s Detail")

        detailUserViewModel.userDetail.observe(this, {
            this.userDetail = it
            setUserDetail()
        })

        detailUserViewModel.isLoading.observe(this, {
            showLoading(it)
        })

        detailUserViewModel.isFavorite.observe(this, {
            this.isFavorite = it
            invalidateOptionsMenu()
        })

        val sectionsPagerAdapter = SectionsPagerAdapter(this@DetailUserActivity,
            user.username
        )
        binding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun setUserDetail() {
        Glide.with(this)
            .load(userDetail.avatarUrl)
            .into(binding.imgDetailPhoto)
        binding.imgDetailPhoto.clipToOutline = true
        binding.tvDetailName.text = userDetail.name ?: "-"
        binding.tvDetailUsername.text = userDetail.login
        binding.tvDetailLocation.text = userDetail.location ?: "-"
        binding.tvDetailCompany.text = userDetail.company ?: "-"
        binding.tvDetailRepository.text = userDetail.publicRepos.toString()
        binding.tvDetailFollowers.text = userDetail.followers.toString()
        binding.tvDetailFollowing.text = userDetail.following.toString()
    }

    private fun showLoading(isLoading: Boolean) {
        val loadingText = getString(R.string.loading_placeholder)
        if (isLoading) {
            binding.apply {
                tvDetailName.text = loadingText
                tvDetailUsername.text = loadingText
                tvDetailLocation.text = loadingText
                tvDetailCompany.text = loadingText
                tvDetailRepository.text = loadingText
                tvDetailFollowers.text = loadingText
                tvDetailFollowing.text = loadingText
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_user_top_app_bar, menu)

        val favoriteMenuItem = menu.findItem(R.id.favorite)
        if(isFavorite) favoriteMenuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24)
        else favoriteMenuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_border_24)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                val textMessage: String = "${userDetail.name ?: "-"} (${userDetail.login})\n" +
                        "Location: ${userDetail.location ?: "-"}\n" +
                        "Company: ${userDetail.company ?: "-"}\n" +
                        "${userDetail.publicRepos} Repositories\n" +
                        "${userDetail.followers} Followers • ${userDetail.following} Following"

                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, textMessage)
                    type = "text/plain"
                }

                val title: String = resources.getString(R.string.send_chooser_title)
                val shareIntent = Intent.createChooser(sendIntent, title)

                if (sendIntent.resolveActivity(packageManager) != null) {
                    startActivity(shareIntent)
                }
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.favorite -> {
                if(this.isFavorite) {
                    detailUserViewModel.setUserFavorite(user, false)
                    Toast.makeText(this@DetailUserActivity, "User removed from favorite", Toast.LENGTH_SHORT).show()
                } else {
                    detailUserViewModel.setUserFavorite(user, true)
                    Toast.makeText(this@DetailUserActivity, "User added to favorite", Toast.LENGTH_SHORT).show()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_USER = "extra_user"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_follower,
            R.string.tab_following
        )
    }
}