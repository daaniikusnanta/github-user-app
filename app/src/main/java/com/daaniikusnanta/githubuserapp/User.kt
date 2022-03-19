package com.daaniikusnanta.githubuserapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var photo: Int,
    var name: String?,
    var username: String?,
    var location: String?,
    var repository: String?,
    var company: String?,
    var followers: String?,
    var following: String?
) : Parcelable
