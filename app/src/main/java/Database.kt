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
    val postId: String,
    val uid: String,
    val roomID: String,
    val content: String,
    val postTime: String,
    val imageUri: String,
    val likeCount: Int
)