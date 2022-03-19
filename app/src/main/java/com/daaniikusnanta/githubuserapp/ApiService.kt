package com.daaniikusnanta.githubuserapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users")
    fun getUsers(
    ): Call<UsersResponse>

    @GET("users/{login}")
    fun getUser(
        @Path("login") id: String
    ): Call<UserResponse>
}