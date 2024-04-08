package com.example.test.entity

data class BorrowerEntity(
    val id:Int,
    val name:String,
    val completionDate: String,
    val initiationDate: String,

    val bkStatus: String,
    val bkTransStatus: String,
    val trans_id: Int
)
