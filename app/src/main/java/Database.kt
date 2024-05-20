package com.example.sw_project.models

data class Room(
    var roomID: String? = null,
    var roomName: String? = null,
    var participants: HashMap<String, HashMap<String, String>> = HashMap()
)

data class Users(
    val UserId: String,
    val UserName: String,
    val UserBirth: String
)