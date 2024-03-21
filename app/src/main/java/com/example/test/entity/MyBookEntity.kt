package com.example.test.entity

data class MyBookEntity (
    var id: Int = 0,
    var name: String = "",
    var status: String = "",
    var thumbnail: String = "",
    var owner: UserEntity = UserEntity(),
    var author: String = ""
):java.io.Serializable

