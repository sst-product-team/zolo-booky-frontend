package com.example.test.entity

data class ListAppealEntity(
    var trans_id: Int = 0,
    var name: String = "",
    var status: String = "",
    var thumbnail: String = "",
    var owner: String = "",
    var author: String = "",
    var trans_status: String = "",
    var expected_completion_date: String = "",
    var bookId: Int = 0,
    var stats_change_date: String = ""
):java.io.Serializable