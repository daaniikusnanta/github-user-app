package com.daaniikusnanta.githubuserapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("users")
    fun getUsers(
    ): Call<List<UsersResponseItem>>

    @GET("users/{login}")
    fun getUser(
        @Path("login") id: String
    ): Call<UserDetailResponse>

    @GET("users/{login}/followers")
    fun getFollowers(
        @Path("login") id: String
    ): Call<List<UsersResponseItem>>

    @GET("users/{login}/following")
    fun getFollowing(
        @Path("login") id: String
    ): Call<List<UsersResponseItem>>

    @GET("search/users")
    fun getSearchedUsers(
        @Query("q") query: String
    ): Call<SearchedUsersResponse>
}