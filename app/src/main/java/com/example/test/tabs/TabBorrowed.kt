package com.example.test.tabs


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView

import androidx.constraintlayout.widget.ConstraintLayout

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.test.R
import com.example.test.adapter.BookBorrowAdapter
import com.example.test.adapter.BookListAdapter
import com.example.test.databinding.FragmentHomeBinding

import com.example.test.databinding.FragmentTabBorrowedBinding
import com.example.test.entity.AppealEntity
import com.example.test.entity.ListAppealEntity
import com.example.test.entity.ListBookEntity
import com.example.test.globalContexts.Constants
import com.example.test.globalContexts.Constants.USER_ID
import kotlin.math.log


class TabBorrowed : Fragment() {

    private lateinit var binding:FragmentTabBorrowedBinding


    private lateinit var searchView: SearchView
    private val books = mutableListOf<ListBookEntity>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var queue: RequestQueue
    private val SEARCH_QUERY_DELAY = 500 // Milliseconds
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTabBorrowedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // shimmer
        val shimmerFrameLayout = binding.shimmerViewContainerBookList
        val shimmerFrameLayout2 = binding.shimmerViewContainerBookList2
        // Starting the shimmer effect before making the API request
        shimmerFrameLayout.startShimmer()
        shimmerFrameLayout2.startShimmer()

        val layoutParams = binding.constraintLayout4.layoutParams as ConstraintLayout.LayoutParams

        val viewButton : TextView = binding.viewHistoryBtn
        viewButton.setOnClickListener {

            Log.d("onbtnVHTAG", "onViewCreated: clicked")
        }

        ///// for borrowed books by borrower.

        val recyclerView = binding.rvBorrowBookList

        val queue = Volley.newRequestQueue(requireContext())

        val url = "${Constants.BASE_URL}/v0/appeals"

        var token: String

        Log.d("API Request URL", url)

        var count =0

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API Response", response.toString())
                val booksB = mutableListOf<ListAppealEntity>()
                val borrowerData = mutableListOf<AppealEntity>()

                for (i in 0 until response.length()){

                    val appealObject = response.getJSONObject(i)
                    val borrowerObject = appealObject.getJSONObject("borrowerId")
                    val borrowerId = borrowerObject.getInt("id")
                    val trans_status = appealObject.getString("trans_status")
                    if (borrowerId == USER_ID){
                        count++
                        val transId = appealObject.getInt("trans_id")
                        val bookIdObject = appealObject.getJSONObject("bookId")
                        val bookId = bookIdObject.getInt("id")
                        val bookTitle = bookIdObject.getString("name")
                        val bookStatus = bookIdObject.getString("status")
                        val bookThumbnail = bookIdObject.getString("thumbnail")
                        val bookOwner = bookIdObject.getJSONObject("owner")
                        val ownerName = bookOwner.getString("name")
                        val bookAuthor = bookIdObject.getString("author")
                        val status_change_date = appealObject.getString("status_change_date")
                        val expected_completion_date =
                            appealObject.getString("expected_completion_date")
                        val dates: String = expected_completion_date.split(" ")[0]
                        booksB.add(
                            ListAppealEntity(
                                transId,
                                bookTitle,
                                bookStatus,
                                bookThumbnail,
                                ownerName,
                                bookAuthor,
                                trans_status,
                                dates,
                                bookId,
                                status_change_date
                            )
                        )
                    }
                }
                Log.d("Parsed Books borrowed", "Number of books fetched: ${booksB.size}")
                Log.d("Count", count.toString())
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE



                Log.d("Count", count.toString())
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE



//// need to implement something here...
                if(count==0){

                    val layoutParams = binding.constraintLayout4.layoutParams as ConstraintLayout.LayoutParams
                    val heightInDp = 200 // desired height in dp
                    val density = resources.displayMetrics.density
                    val heightInPixels = (heightInDp * density).toInt()
                    layoutParams.height = heightInPixels
                    binding.constraintLayout4.layoutParams = layoutParams

                    binding.msg.visibility = View.VISIBLE

                }


                val adapter = BookBorrowAdapter(requireContext(), booksB)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            },
            { error ->
                Log.e("API Error", error.toString())
                Log.e("VolleyExample", "Error: $error")
            }
        )

        queue.add(jsonArrayRequest)


        //////// for all books

        val recyclerView2 = view.findViewById<RecyclerView>(R.id.BookRecyclerView)
        val url2 = "${Constants.BASE_URL}/v0/books?size=100"



        Log.d("API Request URL", url2)

        val jsonArrayRequest2 = JsonArrayRequest(
            Request.Method.GET, url2, null,
            { response ->
                Log.d("API Response", response.toString())
                val books = mutableListOf<ListBookEntity>()
                for (i in 0 until response.length()) {
                    val bookObject = response.getJSONObject(i)
                    val ownerObject = bookObject.getJSONObject("owner")


                    val bookId = bookObject.getInt("id")
                    val bookTitle = bookObject.getString("name")
                    val bookStatus = bookObject.getString("status")
                    val bookThumbnail = bookObject.getString("thumbnail")
                    val ownerName = ownerObject.getString("name")
                    val bookAuthor = bookObject.getString("author")


                    if(bookStatus=="AVAILABLE"){
                        books.add(
                            ListBookEntity(bookId, bookTitle, bookStatus, bookThumbnail, ownerName, bookAuthor)

                        )}
                }
                Log.d("Parsed Books", "Number of books fetched: ${books.size}")

                shimmerFrameLayout2.stopShimmer()
                shimmerFrameLayout2.visibility = View.GONE


                shimmerFrameLayout2.stopShimmer()
                shimmerFrameLayout2.visibility = View.GONE


                val adapter = BookListAdapter(books)
                recyclerView2.layoutManager = LinearLayoutManager(requireContext())
                recyclerView2.adapter = adapter
                adapter.notifyDataSetChanged() // Ensures the adapter knows the data has changed

            },
            { error ->
                Log.e("API Error", error.toString())
                Log.e("VolleyExample", "Error: $error")
            }
        )

        queue.add(jsonArrayRequest2)
        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            private var lastSearchTime = 0L
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    val currentMillis = System.currentTimeMillis()
                    if (currentMillis - lastSearchTime >= SEARCH_QUERY_DELAY) {
                        lastSearchTime = currentMillis
                        val url2 = "${Constants.BASE_URL}/v0/search/books?name=$newText"
                        val jsonArrayRequest2 = JsonArrayRequest(
                            Request.Method.GET, url2, null,
                            { response ->
                                books.clear() // Ensure books list is cleared before adding new results
                                for (i in 0 until response.length()) {
                                    val bookObject = response.getJSONObject(i)
                                    val ownerObject = bookObject.getJSONObject("owner")

                                    val bookId = bookObject.getInt("id")
                                    val bookTitle = bookObject.getString("name")
                                    val bookStatus = bookObject.getString("status")
                                    val bookThumbnail = bookObject.getString("thumbnail")
                                    val ownerName = ownerObject.getString("name")
                                    val bookAuthor = bookObject.getString("author")

                                    Log.d("on search ", "onQueryTextChange: $books")

                                    if (bookStatus == "AVAILABLE") {
                                        books.add(
                                            ListBookEntity(bookId, bookTitle, bookStatus, bookThumbnail, ownerName, bookAuthor)
                                        )
                                    }
                                }

                                val adapter = BookListAdapter(books)
                                recyclerView2.layoutManager = LinearLayoutManager(requireContext())
                                recyclerView2.adapter = adapter
                                adapter.notifyDataSetChanged()
                            },
                            { error ->
                                Log.e("API Error", error.toString())
                            }
                        )
                        queue.add(jsonArrayRequest2)
                    }
                } else {
                    // Reset to initial data if search query is empty
                    fetchInitialBooks()
                }
                return true
            }
        })
    }

    private fun fetchInitialBooks() {
        recyclerView = binding.BookRecyclerView
        queue = Volley.newRequestQueue(requireContext())
        val url = "${Constants.BASE_URL}/v0/books?size=100"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                books.clear() // Clear existing books before fetching new ones
                for (i in 0 until response.length()) {
                    val bookObject = response.getJSONObject(i)
                    val ownerObject = bookObject.getJSONObject("owner")

                    val bookId = bookObject.getInt("id")
                    val bookTitle = bookObject.getString("name")
                    val bookStatus = bookObject.getString("status")
                    val bookThumbnail = bookObject.getString("thumbnail")
                    val ownerName = ownerObject.getString("name")
                    val bookAuthor = bookObject.getString("author")

                    if (bookStatus == "AVAILABLE") {
                        books.add(
                            ListBookEntity(bookId, bookTitle, bookStatus, bookThumbnail, ownerName, bookAuthor)
                        )
                    }
                }

                val adapter = BookListAdapter(books)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            },
            { error ->
                Log.e("API Error", error.toString())
            }
        )

        queue.add(jsonArrayRequest)

    }

    companion object {
    }
}