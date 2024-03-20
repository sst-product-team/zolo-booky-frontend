package com.example.test.activity


import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.test.R
import com.example.test.databinding.PostBooksBinding
import com.example.test.globalContexts.Constants
//import com.google.android.gms.cast.framework.media.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId

class PostBooksActivity: AppCompatActivity(){
    private lateinit var binding: PostBooksBinding
    private val requestQueue: RequestQueue by lazy {Volley.newRequestQueue(this)}
    private val apiUrl = "${Constants.BASE_URL}/v0/books"


    //image picker
    private lateinit var imageView: ImageView
    private lateinit var button: FloatingActionButton
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.zolo_bg_main));
        }

        setContentView(R.layout.post_books)

        //img picker
        imageView = findViewById(R.id.edi_cover_smol)
        button = findViewById(R.id.floatingActionButton)

//        button.setOnClickListener{
//            ImagePicker.with(this)
//                .crop()	    			//Crop image(Optional), Check Customization for more option
//                .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                .start()
//        }


        //

        val chooseDateButton = findViewById<Button>(R.id.chooseDateButton)
        val selectedDateText = findViewById<TextView>(R.id.selectedDateText)

        chooseDateButton.setOnClickListener {
            Log.d("PostBooksActivity", "chooseDateButton clicked")

            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            picker.show(supportFragmentManager, "TAG")
            picker.addOnPositiveButtonClickListener {
                val selectedDate = picker.selection!!

                val borrowedDays = calculateBorrowedDays(selectedDate)

                Log.d("BookInfoActivity", "Days borrowed: $borrowedDays")

                // Update the TextView with the selected date
                val date = Instant.ofEpochMilli(selectedDate).atZone(ZoneId.systemDefault()).toLocalDate()
                selectedDateText.text = "$date"

                val addButton = findViewById<Button>(R.id.postBookButton)
                addButton.setOnClickListener {
                    val bookNameEditText = findViewById<EditText>(R.id.etBookName)
                    val bookAuthorEditText = findViewById<EditText>(R.id.etAuthor)
                    val bookDescriptionEditText = findViewById<EditText>(R.id.etDescription)

                    val bookName = bookNameEditText.text.toString()
                    val bookAuthor = bookAuthorEditText.text.toString()
                    val bookDescription = bookDescriptionEditText.text.toString()

                    showCustomDialog(
                        bookName,
                        bookAuthor,
                        bookDescription,
                        borrowedDays.toInt() + 1,
                        selectedDate,
                        ""
                    )

                }
            }
            picker.addOnNegativeButtonClickListener {
                picker.dismiss()
            }
        }
    }

//img picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageView.setImageURI(data?.data)
    }
    //

    private fun showCustomDialog(
        bookName: String,
        bookAuthor: String,
        bookDescription: String,
        daysBorrowed: Int,
        selectedDate: Long,
        thumbnail: String
    ) {
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet_conformation, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        val tvBorrowDateText: TextView = dialogView.findViewById(R.id.tvBorrowDateText)
        val btnCancel: MaterialButton = dialogView.findViewById(R.id.btnCancel)
        val btnConfirm: MaterialButton = dialogView.findViewById(R.id.btnConfirm)

        tvBorrowDateText.text = "Lend the book for $daysBorrowed days?"

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            addBookToDatabase(
                bookName,
                bookAuthor,
                bookDescription,
                daysBorrowed,
                selectedDate.toString(),
                thumbnail
            )
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


    private fun addBookToDatabase(
        bookName: String,
        bookAuthor: String,
        bookDescription: String,
        daysBorrowed: Int,
        bookAvailability: String,
        thumbnail: String
    ) {
        val url = apiUrl

        Log.d("USERU", Constants.USER_ID.toString())

        val jsonBody = JSONObject().apply {
            put("name", bookName)
            put("author", bookAuthor)
            put("description", bookDescription)
            put("availability", bookAvailability)
            put("owner", Constants.USER_ID)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                // Handling the response from the server
                Log.d("POST BOOK SUCCESS", "Book added successfully. Response: $response")
                Toast.makeText(this@PostBooksActivity, "The book is successfully added", Toast.LENGTH_LONG).show()
            },
            { error ->
                // Handle errors
                Log.e("POST BOOK ERROR", "Error adding book: $error")
                val networkResponse = error.networkResponse
                if (networkResponse != null && networkResponse.statusCode == 409) {
                    Toast.makeText(this@PostBooksActivity, "The book already exists", Toast.LENGTH_LONG).show()
                }
            }
        )

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest)
    }
}