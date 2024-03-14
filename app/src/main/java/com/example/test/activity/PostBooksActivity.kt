package com.example.test.activity


import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.test.R
import com.example.test.globalContexts.Constants
import org.json.JSONObject

class PostBooksActivity: AppCompatActivity(){

    private val requestQueue: RequestQueue by lazy {Volley.newRequestQueue(this)}
    private val apiUrl = "${Constants.BASE_URL}/v0/books"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_books) // we can set the layout file here



        val addButton = findViewById<Button>(R.id.postBookButton)
        addButton.setOnClickListener {

            val bookNameEditText = findViewById<EditText>(R.id.etBookName)
            val bookDescriptionEditText = findViewById<EditText>(R.id.etDescription)
            val bookDateEditText = findViewById<EditText>(R.id.etBookName)

            val bookName = bookNameEditText.text.toString()
            val bookDescription = bookDescriptionEditText.text.toString()
            val bookAvailability = bookDateEditText.text.toString()


            addBookToDatabase(bookName, bookDescription,bookAvailability)

            //clearing fields
            bookNameEditText.text.clear()
            bookDescriptionEditText.text.clear()
        }
    }






    private fun addBookToDatabase(bookName: String, bookDescription: String,bookAvailability: String) {
        val url = apiUrl

        val jsonBody = JSONObject().apply {
            put("name", bookName)
            put("description", bookDescription)
            put("availability", bookAvailability)
            put("owner", Constants.USER_ID)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                // Handling the response from the server
                Log.d("VolleyExample", "Book added successfully. Response: $response")
            },
            { error ->
                // Handle errors
                Log.e("VolleyExample", "Error adding book: $error")
            }
        )

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest)
    }
}