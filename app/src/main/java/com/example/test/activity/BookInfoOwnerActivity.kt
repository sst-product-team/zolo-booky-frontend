package com.example.test.activity

import BorrowerListAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonArrayRequest
import com.bumptech.glide.Glide
import com.example.test.databinding.ActivityBookInfoOwnerBinding
import com.example.test.entity.BorrowerEntity
import com.example.test.globalContexts.Constants
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.test.R
import com.facebook.shimmer.ShimmerFrameLayout

class BookInfoOwnerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookInfoOwnerBinding
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var borrowerAdapter: BorrowerListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.zolo_bg_main));
        }

        binding = ActivityBookInfoOwnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
//



//        val rvBorrowers = binding.rvBorrowers
//        borrowerAdapter = BorrowerListAdapter(emptyList())
//        rvBorrowers.adapter = borrowerAdapter





        // Retrieve the book information from the intent
        val bookId = intent.getIntExtra("bookId", 0)
        val bookName = intent.getStringExtra("bookName")
        val bookStatus = intent.getStringExtra("bookStatus")
        val bookThumbnail = intent.getStringExtra("bookThumbnail")
        val bookAuthor = intent.getStringExtra("bookAuthor")
        val bookOwner = intent.getStringExtra("bookOwner")

        // Display the book information in the views
        binding.tvBookName.text = bookName
        binding.tvBookOwner.text = bookOwner
        binding.textAuthor.text = bookAuthor
        Glide.with(this)
            .load(bookThumbnail)
            .into(binding.coverSmol)
        Glide.with(this)
            .load(bookThumbnail)
            .into(binding.coverBig)









        var queue = Volley.newRequestQueue(this)
        val url = "${Constants.BASE_URL}/v0/appeals"




        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val borrowers = ArrayList<BorrowerEntity>()
                for (i in 0 until response.length()) {
                    val appealObject = response.getJSONObject(i)
                    val borrowerObject = appealObject.getJSONObject("borrowerId")
                    val borrowerId = borrowerObject.getInt("id")
                    val borrowerName = borrowerObject.getString("name")
                    val borrower = BorrowerEntity(
                        id = borrowerId,
                        name = borrowerName
                    )
                    borrowers.add(borrower)
                }
                borrowers.sortBy { it.name }
                Log.d("borrowers data", "onCreate: $borrowers")
//                borrowerAdapter.borrowers = borrowers
//                borrowerAdapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonArrayRequest)


    }
}