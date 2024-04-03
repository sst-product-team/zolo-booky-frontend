package com.example.test.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.test.R
import com.example.test.adapter.BookListAdapter
import com.example.test.databinding.FragmentHomeBinding
import com.example.test.entity.ListBookEntity
import com.example.test.globalContexts.Constants
import com.google.firebase.messaging.FirebaseMessaging


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
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
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shimmerFrameLayout = binding.shimmerViewContainerBookList
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmer()

        searchView = view.findViewById(R.id.search_view)

        recyclerView = view.findViewById(R.id.BookRecyclerView)
        queue = Volley.newRequestQueue(requireContext())

        val url = "${Constants.BASE_URL}/v0/books?size=10"


        Log.d("API Request URL", url)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API Response", response.toString())
                books.clear()
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

                shimmerFrameLayout.visibility = View.GONE
                shimmerFrameLayout.stopShimmer()

                val adapter = BookListAdapter(books)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged() // Ensures the adapter knows the data has changed

            },
            { error ->

                Log.e("API Error", error.toString())
                Log.e("VolleyExample", "Error: $error")
            }
        )

        queue.add(jsonArrayRequest)


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
                        val url = "${Constants.BASE_URL}/v0/search/books?name=$newText"
                        val jsonArrayRequest = JsonArrayRequest(
                            Request.Method.GET, url, null,
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
                } else {
                    // Reset to initial data if search query is empty
                    fetchInitialBooks()
                }
                return true
            }
        })
    }

    private fun fetchInitialBooks() {
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