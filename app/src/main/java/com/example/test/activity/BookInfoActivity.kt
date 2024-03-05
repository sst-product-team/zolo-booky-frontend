package com.example.test.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.test.Constants
import com.example.test.R
import com.example.test.databinding.ActivityBookInfoBinding

class BookInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookInfoBinding
    private var id: Int? = null
    private var name: String? = ""
    private var description: String? = ""
//    private var book_genre: List<String> = listOf()
    private var ratings: Double? = 0.0
    private var thumbnail: String? = ""
    private var author: String? = ""
    private var owner_id: Int? = 0
    private var book_next_available: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bookId = intent.getIntExtra("bookId", 0)
        if (bookId == 0) {
            finish()
            return
        }else{
            fetchBookDetails(bookId)
        }

        id = intent.getIntExtra("id", 3)
        name = intent.getStringExtra("name")
        description = intent.getStringExtra("description")
//        book_genre = intent.getStringArrayListExtra("book_genre") as List<String>
//        ratings = intent.getDoubleExtra("ratings", 0.0)
        thumbnail = intent.getStringExtra("thumbnail")
        author = intent.getStringExtra("author")
        owner_id = intent.getIntExtra("owner_id", 0)
//        book_next_available = intent.getStringExtra("book_next_available")

        with(binding){
            tvBookName.text = name
            tvDescription.text = description
            textAuthor.text = author
            tvGenre.text = ratings.toString()
            tvAllGenre.text = book_next_available
        }
    }
    private fun fetchBookDetails(bookId: Int) {
        val url = "${Constants.BASE_URL}/v0/books/$bookId"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Using optString instead of getString
                val name = response.optString("name")
                val description = response.optString("description")
                val author = response.optString("thumbnail")
                // Updating views here
                binding.tvBookName.text = name
                binding.tvDescription.text = description
                binding.textAuthor.text = thumbnail
            },
            { error ->
                // Logging error
                Log.e("BookInfoActivity", "Error fetching book details: $error")
            })

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}