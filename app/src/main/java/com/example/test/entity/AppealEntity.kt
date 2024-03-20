package com.example.test.entity

data class AppealEntity(
    var id: Int = 0,
    var book_id: Int = 0,
    var borrower_id: Int = 0,
    var owner_id: Int = 0,
    var status: String = "",
    var initiation_date: String = "",
    var expected_completion_date: String = "",
    var book: ListBookEntity = ListBookEntity(),
    var trans_id: Int = 0,

):java.io.Serializable