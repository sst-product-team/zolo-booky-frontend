package com.example.test.entity

import java.util.Date

data class BooksDetailsEntity (
    var id: Int =0,
    var name: String="",
    var description: String="",
    var book_genre: List<String> = listOf(),
    var ratings: Double=0.0,
    var thumbnail: String = "",
    var author: String = "",
    var owner_id: Int = 0,
    var book_next_available : Date = Date(),
):java.io.Serializable