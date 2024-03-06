package com.example.test.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.test.Constants
import com.example.test.R
import com.example.test.databinding.ActivityBookInfoBinding
import org.json.JSONObject

class BookInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookInfoBinding
    private var id: Int? = null
    private var name: String? = ""
    private var description: String? = ""
    private var ratings: Double? = 0.0
    private var thumbnail: String? = ""
    private var author: String? = ""
    private var owner_id: Int? = 0
    private var book_next_available: String? = ""
    private var count: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bookId = intent.getIntExtra("bookId", 0)
        if (bookId == 0) {
            finish()
            return
        } else {
            fetchBookDetails(bookId)
        }

        id = intent.getIntExtra("id", 3)
        name = intent.getStringExtra("name")
        description = intent.getStringExtra("description")
        thumbnail = intent.getStringExtra("thumbnail")
        author = intent.getStringExtra("author")
        owner_id = intent.getIntExtra("owner_id", 0)

        with (binding) {
            tvBookName.text = name
            tvDescription.text = description
            textAuthor.text = author
            tvGenre.text = "Ratings: $ratings"
            tvAllGenre.text = book_next_available
            tvCount.text = count.toString() // Setting the initial count

            bIncrement.setOnClickListener {
                incrementQuantity()
            }

            bDecrement.setOnClickListener {
                decrementQuantity()
            }
            binding.bBorrowBook.setOnClickListener {
                showBorrowConfirmationDialog(bookId, count)
            }
        }
    }


    private fun showBorrowConfirmationDialog(bookId :Int, count: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Borrow")
            .setMessage("Are you sure you want to borrow the book for ${count} days?")
            .setPositiveButton("Borrow") { dialog, which ->

                borrowDataToDatabase(bookId , count)

                Log.d("BookInfoActivity", "Borrowing for ${count} days confirmed!")
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
    private fun borrowDataToDatabase(bookId: Int, count: Int) {
        val url = "${Constants.BASE_URL}/v0/appeals"

        val jsonBody = JSONObject().apply {
            put("book_id", bookId)
            put("borrower_id", count)//only for test
//            put("requested_by", owner_id)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            Response.Listener { response ->
                // Handle successful response
                Log.d("BookInfoActivity", "Book borrowed successfully. Response: $response")
                // Update UI or perform further actions if needed
            },
            Response.ErrorListener { error ->
                // Handle errors
                Log.e("BookInfoActivity", "Error borrowing book: $error")
                // Display error message or perform other error handling steps
            }
        )

        // Adding the request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }


    private fun fetchBookDetails(bookId: Int) {
        val url = "${Constants.BASE_URL}/v0/books/$bookId"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Using optString instead of getString
                val name = response.optString("name")
                val description = response.optString("description")
                val author = response.optString("author")


                binding.tvBookName.text = name
                binding.tvDescription.text = description
                binding.textAuthor.text = author
            },
            { error ->
                Log.e("BookInfoActivity", "Error fetching book details: $error")
            })

        // Adding the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun incrementQuantity() {
        count++
        binding.tvCount.text = count.toString()
    }

    private fun decrementQuantity() {
        if (count > 1) {
            count--
            binding.tvCount.text = count.toString()
        }
    }
}
