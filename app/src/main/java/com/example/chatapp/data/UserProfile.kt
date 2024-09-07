package com.example.chatapp.data

import android.net.Uri

data class UserProfile(
    val name: String,
    val about: String,
    val profileImageUri: Uri?)
