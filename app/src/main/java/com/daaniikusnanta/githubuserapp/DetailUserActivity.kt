package com.daaniikusnanta.githubuserapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.daaniikusnanta.githubuserapp.databinding.ActivityDetailUserBinding
import com.google.android.material.tabs.TabLayoutMediator

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var user: UserDetailResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val detailUserViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[DetailUserViewModel::class.java]

        val username = intent.getStringExtra(EXTRA_USERNAME)
        if (username != null) {
            detailUserViewModel.getUserDetail(username)
        }

        detailUserViewModel.userDetail.observe(this, { userDetail ->
            user = userDetail
            setUserDetail(userDetail)
        })

        detailUserViewModel.isLoading.observe(this, {
            showLoading(it)
        })

        val sectionsPagerAdapter = username?.let {
            SectionsPagerAdapter(this@DetailUserActivity,
                it
            )
        }
        binding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun setUserDetail(user: UserDetailResponse) {
        Glide.with(this)
            .load(user.avatarUrl)
            .into(binding.imgDetailPhoto)
        binding.imgDetailPhoto.clipToOutline = true
        binding.tvDetailName.text = user.name ?: "-"
        binding.tvDetailUsername.text = user.login
        binding.tvDetailLocation.text = user.location ?: "-"
        binding.tvDetailCompany.text = user.company ?: "-"
        binding.tvDetailRepository.text = user.publicRepos.toString()
        binding.tvDetailFollowers.text = user.followers.toString()
        binding.tvDetailFollowing.text = user.following.toString()
    }

    private fun showLoading(isLoading: Boolean) {
        val loadingText = "Loading..."
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                val textMessage: String = "${user.name ?: "-"} (${user.login})\n" +
                        "Location: ${user.location ?: "-"}\n" +
                        "Company: ${user.company ?: "-"}\n" +
                        "${user.publicRepos} Repositories\n" +
                        "${user.followers} Followers • ${user.following} Following"

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
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_follower,
            R.string.tab_following
        )
    }
}