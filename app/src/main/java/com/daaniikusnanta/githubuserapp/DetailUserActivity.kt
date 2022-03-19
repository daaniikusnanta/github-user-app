package com.daaniikusnanta.githubuserapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.StringRes
import com.daaniikusnanta.githubuserapp.databinding.ActivityDetailUserBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayoutMediator

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f

        val topAppBar: MaterialToolbar = findViewById(R.id.topappbar_detail)

        val user = intent.getParcelableExtra<User>(EXTRA_USER) as User
        binding.imgDetailPhoto.setImageResource(user.photo)
        binding.imgDetailPhoto.clipToOutline = true
        binding.tvDetailName.text = user.name.toString()
        binding.tvDetailUsername.text = user.username
        binding.tvDetailLocation.text = user.location
        binding.tvDetailCompany.text = user.company
        binding.tvDetailRepository.text = user.repository
        binding.tvDetailFollowers.text = user.followers
        binding.tvDetailFollowing.text = user.following

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share -> {
                    val textMessage: String = "${user.name.toString()} (${user.username})\n" +
                            "Location: ${user.location}\n" +
                            "Company: ${user.company}\n" +
                            "${user.repository} Repositories\n" +
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
                    true
                }
                else -> false
            }
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