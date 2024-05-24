package com.example.sw_project.models

import com.google.firebase.auth.FirebaseAuth

data class Room(
    var roomCode: String? = null,
    var roomName: String? = null,
    var participants: HashMap<String, HashMap<String, String>> = HashMap()
)
data class Users(
    val UserId: String,
    val UserName: String,
    val UserBirth: String
)

data class Post(
    val roomCode: String = "",
    val postId: String = "",
    val profileImageUrl: String = "",
    val uid: String = "",
    val likeCount: Int = 0,
    val imageUri: String = "",
    val content: String = "",
    val postTime: String = ""
)
