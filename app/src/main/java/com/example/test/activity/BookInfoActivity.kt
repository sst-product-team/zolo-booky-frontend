package com.example.test.activity

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.test.Constants
import com.example.test.R
import com.example.test.databinding.ActivityBookInfoBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import org.json.JSONObject
import java.time.Instant
import java.util.Date

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

            binding.bBorrowBook.setOnClickListener {
                datePicker(bookId)
            }
        }
    }
    private fun datePicker(bookId: Int) {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        picker.show(supportFragmentManager, "TAG")

        picker.addOnPositiveButtonClickListener {
            val selectedDate = picker.selection!!

            val borrowedDays = calculateBorrowedDays(selectedDate)

            Log.d("BookInfoActivity", "Days borrowed: $borrowedDays")
            showCustomDialog(bookId, borrowedDays.toInt()+1)
        }

        picker.addOnNegativeButtonClickListener {
            picker.dismiss()
        }
    }
    private fun showCustomDialog(bookId: Int, daysBorrowed: Int) {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_box, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = builder.create()

        val tvBorrowDateText: TextView = dialogView.findViewById(R.id.tvBorrowDateText)
        val btnCancel: MaterialButton = dialogView.findViewById(R.id.btnCancel)
        val btnConfirm: MaterialButton = dialogView.findViewById(R.id.btnConfirm)

        tvBorrowDateText.text = "Borrow the book for $daysBorrowed days?"

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            borrowDataToDatabase(bookId, daysBorrowed)
            dialog.dismiss()
        }

        dialog.show()
    }



    private fun calculateBorrowedDays(selectedDate: Long): Long {
        val midnightToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return (selectedDate - midnightToday) / (1000 * 60 * 60 * 24)
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

}
