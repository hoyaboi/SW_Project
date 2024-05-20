package com.example.sw_project.models

data class Room(
    val roomName: String,
    val roomCode: String,
    val participants: Map<String, Participant>? = null
)
data class Participant(
    val uid: String,
    val profileImage: String? = null
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