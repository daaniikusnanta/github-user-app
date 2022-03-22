package com.daaniikusnanta.githubuserapp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.daaniikusnanta.githubuserapp.database.UserItem
import com.daaniikusnanta.githubuserapp.databinding.FragmentFollowBinding

class FollowFragment : Fragment() {
    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!
    private val followViewModel by viewModels<FollowViewModel> {
        FollowViewModel.Factory(
            arguments?.getString(ARG_USERNAME) ?: "",
            arguments?.getInt(ARG_SECTION_NUMBER, 0) ?: 0
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvUsers.setHasFixedSize(true)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvUsers.layoutManager = GridLayoutManager(requireActivity(), 2)
        } else {
            binding.rvUsers.layoutManager = LinearLayoutManager(requireActivity())
        }

        followViewModel.followList.observe(requireActivity(), {
            setUsersData(it)
        })

        followViewModel.isLoading.observe(requireActivity(), {
            showLoading(it)
        })
    }

    private fun setUsersData(listUsers: List<UsersResponseItem>) {
        val users = createUserItemsFromUsersResponse(listUsers)

        if(users.count() == 0) {
            binding.tvNoData.visibility = View.VISIBLE
        } else {
            binding.tvNoData.visibility = View.GONE
            val listUserAdapter = ListUserAdapter(users)
            binding.rvUsers.adapter = listUserAdapter

            listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
                override fun onItemClicked(data: UserItem) {
                    showUserDetail(data)
                }
            })
        }
    }

    private fun showUserDetail(user: UserItem) {
        val moveIntent = Intent(requireActivity(), DetailUserActivity::class.java)
        moveIntent.putExtra(DetailUserActivity.EXTRA_USER, user)
        startActivity(moveIntent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
        const val ARG_USERNAME = "extra_username"
    }
}