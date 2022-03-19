package com.daaniikusnanta.githubuserapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.lang.StringBuilder

class ListUserAdapter(private val listUser: ArrayList<UserResponse>) : RecyclerView.Adapter<ListUserAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_user, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = listUser[position]
        Glide.with(holder.itemView)
            .load(user.avatarUrl)
            .into(holder.imgPhoto)
        holder.imgPhoto.clipToOutline = true
        holder.tvName.text = user.name
        holder.tvUsername.text = user.login
        holder.tvRepository.text = StringBuilder().append(user.publicRepos).append(" Repos")
        holder.tvFollower.text = StringBuilder().append(user.followers).append(" Followers")

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listUser[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = listUser.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvUsername: TextView = itemView.findViewById(R.id.tv_item_username)
        var tvRepository: TextView = itemView.findViewById(R.id.tv_item_repository)
        var tvFollower: TextView = itemView.findViewById(R.id.tv_item_follower)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserResponse)
    }
}