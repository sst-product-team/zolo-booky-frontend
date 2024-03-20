package com.example.test.entity

data class UserEntity(
    var USER_ID: Int = 0,
    var USER_NAME: String = "",
    var FCM_TOKEN: String = "",
) : java.io.Serializable
