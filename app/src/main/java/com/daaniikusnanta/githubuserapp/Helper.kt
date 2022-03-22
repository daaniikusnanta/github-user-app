package com.daaniikusnanta.githubuserapp

import com.daaniikusnanta.githubuserapp.database.UserItem

fun createUserItemsFromUsersResponse(listUsers: List<UsersResponseItem>): ArrayList<UserItem> {
    val newListUsers = ArrayList<UserItem>()

    for (user in listUsers) {
        newListUsers.add(
            UserItem(
                user.login,
                user.avatarUrl
            )
        )
    }

    return newListUsers
}