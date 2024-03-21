package com.example.test.activity


import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity

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
import okhttp3.Call
import okhttp3.Callback
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.UUID


class PostBooksActivity: AppCompatActivity(){
    private lateinit var binding: PostBooksBinding
    private val requestQueue: RequestQueue by lazy {Volley.newRequestQueue(this)}
    private val apiUrl = "${Constants.BASE_URL}/v0/books"
    private var isImg = false;

    //image picker
    private lateinit var imageView: ImageView
    private lateinit var button: FloatingActionButton
    private var bookImagePart: MultipartBody.Part? = null
    private var thumbnailString: String? = null

    private var borrowedDays :Long= 0;
    private var selectedDate :Long= 0;

    //lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
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

//
        button.setOnClickListener {
            // calling intent on below line.
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            // starting activity on below line.
            startActivityForResult(intent, 1)
        }


        //
        val addButton = findViewById<Button>(R.id.postBookButton)
        addButton.setOnClickListener {
            val bookNameEditText = findViewById<EditText>(R.id.etBookName)
            val bookAuthorEditText = findViewById<EditText>(R.id.etAuthor)
            val bookDescriptionEditText = findViewById<EditText>(R.id.etDescription)

            val bookName = bookNameEditText.text.toString()
            val bookAuthor = bookAuthorEditText.text.toString()
            val bookDescription = bookDescriptionEditText.text.toString()

            if (isImg != false && bookName.isNotEmpty() && bookAuthor.isNotEmpty() && bookDescription.isNotEmpty() && borrowedDays > 0) {
                Log.d("PRADYUT", Constants.USER_FCM)
                showCustomDialog(
                    bookName,
                    bookAuthor,
                    bookDescription,
                    borrowedDays.toInt() + 1,
                    selectedDate,
                    ""
                )
            } else {
                Toast.makeText(this@PostBooksActivity, "Please select an image and fill all the fields", Toast.LENGTH_SHORT).show()
            }

        }

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
                 selectedDate = picker.selection!!

                 borrowedDays = calculateBorrowedDays(selectedDate)

                Log.d("BookInfoActivity", "Days borrowed: $borrowedDays")

                // Update the TextView with the selected date
                val date = Instant.ofEpochMilli(selectedDate).atZone(ZoneId.systemDefault()).toLocalDate()
                selectedDateText.text = "$date"


            }
            picker.addOnNegativeButtonClickListener {
                picker.dismiss()
            }
        }
    }

//img picker
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_OK) {
        when (requestCode) {
            1 -> {

                val selectedImageUri: Uri? = data?.data
                selectedImageUri?.let { uri ->
                    // Convert URI to File
                    val file = File(getRealPathFromURI(uri))

                    imageView.setImageURI(uri)
                    isImg = true

                    // Create request body for file
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

                    // Create MultipartBody.Part from file request body
                    bookImagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

                    // Now you can send 'body' to your backend API
                    // Make sure to use Retrofit or similar library for network requests
                }
            }
        }
    }
}


    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val realPath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return realPath ?: ""
    }
    //


    private fun uploadImageAndGetThumbnail(
        bookName: String,
        bookAuthor: String,
        bookDescription: String,
        daysBorrowed: Int,
        selectedDate: Long
    ) {
        val url = "${Constants.BASE_URL}/v0/images"

        // Create a RequestBody for the image file
        val requestFile = bookImagePart?.body

        //randomness or file name
        val randomString = UUID.randomUUID().toString()
//        val fileName = bookImagePart?.headers?.get("Content-Disposition")
//            ?.split(";")
//            ?.find { it.trim().startsWith("filename=") }
//            ?.substringAfter("filename=")
//            ?.trim()
//            ?.removeSurrounding("\"")
        val fileNameWithExtension = "${UUID.randomUUID()}"

        // Create the multipart request body with the image file
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("multipartFile", fileNameWithExtension, bookImagePart!!.body!!)
            .build()

        // Create the POST request to upload the image
        val request = okhttp3.Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // Execute the request asynchronously
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Image uploaded successfully, parse the response to get the thumbnail string
                    val responseBody = response.body?.string()
                    val thumbnailString = parseThumbnailString(responseBody)

                    // Add book to database with the obtained thumbnail string
                    thumbnailString?.let {
                        Log.d("yupp",thumbnailString)
                        addBookToDatabase(bookName, bookAuthor, bookDescription, daysBorrowed, selectedDate.toString(), thumbnailString)
                        Log.d("yuppo",thumbnailString)
                    } ?: run {
                        Log.e("UPLOAD IMAGE", "Failed to parse thumbnail string from response body")
                    }
                } else {
                    Log.e("UPLOAD IMAGE", "Failed to upload image: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("UPLOAD IMAGE", "Failed to upload image: ${e.message}")
            }
        })
    }

    private fun parseThumbnailString(responseBody: String?): String? {
        // Implement parsing logic here based on the response body structure
        // For example, if the response is a JSON object {"thumbnail": "thumbnail_value"}
        // You can parse it like this:
        // val jsonObject = JSONObject(responseBody)
        // return jsonObject.optString("thumbnail")

        // Replace this with your actual parsing logic
        return responseBody
    }

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
            uploadImageAndGetThumbnail(bookName,bookAuthor,bookDescription,daysBorrowed, selectedDate)

//
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
            put("thumbnail",thumbnail)
        }

        val jsonObjectRequest = JsonObjectRequest(
            com.android.volley.Request.Method.POST, url, jsonBody,
            { response ->
                // Handling the response from the server
                Log.d("POST BOOK SUCCESS", "Book added successfully. Response: $response")
                Toast.makeText(this@PostBooksActivity, "The book is successfully added", Toast.LENGTH_LONG).show()
                finish()
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