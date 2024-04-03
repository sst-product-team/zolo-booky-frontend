package com.example.test.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.test.R
import com.example.test.adapter.BookBorrowAdapter
import com.example.test.adapter.BookListAdapter
import com.example.test.databinding.FragmentTabBorrowedBinding
import com.example.test.entity.AppealEntity
import com.example.test.entity.ListAppealEntity
import com.example.test.entity.ListBookEntity
import com.example.test.globalContexts.Constants
import com.example.test.globalContexts.Constants.USER_ID

class TabBorrowed : Fragment() {
    private lateinit var binding:FragmentTabBorrowedBinding
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


        ///// for borrowed books by borrower.

        val recyclerView = binding.rvBorrowBookList

        val queue = Volley.newRequestQueue(requireContext())

        val url = "${Constants.BASE_URL}/v0/appeals"

        var token: String

        Log.d("API Request URL", url)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API Response", response.toString())
                val books = mutableListOf<ListAppealEntity>()
                val borrowerData = mutableListOf<AppealEntity>()

                for (i in 0 until response.length()){
                    val appealObject = response.getJSONObject(i)
                    val borrowerObject = appealObject.getJSONObject("borrower_id")
                    val borrowerId = borrowerObject.getInt("id")
                    val trans_status = appealObject.getString("trans_status")
                    if (borrowerId == USER_ID){

                        val transId = appealObject.getInt("trans_id")
                        val bookIdObject = appealObject.getJSONObject("book_id")
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
                        books.add(
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
                Log.d("Parsed Books borrowed", "Number of books fetched: ${books.size}")


                val adapter = BookBorrowAdapter(requireContext(), books)
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

        //////
    }

    companion object {
    }
}