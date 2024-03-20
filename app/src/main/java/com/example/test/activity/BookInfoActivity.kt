package com.example.test.activity

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.test.R
import com.example.test.databinding.ActivityBookInfoBinding
import com.example.test.entity.BooksDetailsEntity
import com.example.test.globalContexts.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.json.JSONObject
import java.time.LocalDate
import java.util.Date

class BookInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookInfoBinding
    private var book: BooksDetailsEntity = BooksDetailsEntity(
        id = -1,
        name = "",
        description = "",
        owner = "",
        thumbnail = ""
    )
    private var id: Int? = null
    private var name: String? = ""
    private var description: String? = ""
    private var thumbnail: String? = ""
    private var author: String? = ""
    private var owner_id: Int? = 0
    private var owner: String? = ""
    private var book_available_till: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.background_dark));
        }

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
        owner = intent.getStringExtra("owner")

        with(binding) {
            tvBookName.text = name
            tvDescription.text = description
            textAuthor.text = author
            tvBookOwner.text = owner
            binding.bBorrowBook.setOnClickListener {
                datePicker(bookId)
            }
        }



    }
    private fun datePicker(bookId: Int) {
        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setStart(MaterialDatePicker.todayInUtcMilliseconds())

//        val bookAvailableTillMillis = book_available_till?.time ?: MaterialDatePicker.todayInUtcMilliseconds()
//        constraintsBuilder.setEnd(bookAvailableTillMillis)

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//            .setCalendarConstraints(constraintsBuilder.build())
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
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet_conformation, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

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
        val url = "https://api-zolo.onrender.com/v0/appeals"
        val localDate = LocalDate.now()
        val addedDate = localDate.plusDays(count.toLong())
        val completionDate = addedDate.toString()
        val jsonBody = JSONObject().apply {
            put("book_id", bookId)
            put("borrower_id", Constants.USER_ID)
            put("initiation_date", localDate.toString())
            put("expected_completion_date", completionDate)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                // Handle successful response
                Toast.makeText(this, "Book requested from owner", Toast.LENGTH_SHORT).show()
                Log.d("BookInfoActivity", "Book borrowed successfully. Response: $response")
                // Update UI or perform further actions if needed
            },
            { error ->
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
                val name = response.getString("name")
                val description = response.getString("description")
                val author = response.optString("author")
                val owner = response.getJSONObject("owner")
                val thumbnail = response.getString("thumbnail")

                val coverBig = findViewById<ImageView>(R.id.cover_big)
                val coverSmall = findViewById<ImageView>(R.id.cover_smol)

                Glide.with(this)
                    .load(thumbnail)
                    .into(coverBig)

                Glide.with(this)
                    .load(thumbnail)
                    .into(coverSmall)


                binding.tvBookName.text = name
                binding.tvDescription.text = description
                binding.textAuthor.text = author
                binding.tvBookOwner.text = owner.getString("name")
            },
            { error ->
                Log.e("BookInfoActivity", "Error fetching book details: $error")
            })

        // Adding the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

}
