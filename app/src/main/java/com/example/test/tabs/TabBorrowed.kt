package com.example.test.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.test.adapter.BookBorrowAdapter
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


                        val bookIdObject = appealObject.getJSONObject("book_id")
                        val bookId = bookIdObject.getInt("id")
                        val bookTitle = bookIdObject.getString("name")
                        val bookStatus = bookIdObject.getString("status")
                        val bookThumbnail = bookIdObject.getString("thumbnail")
                        val bookOwner = bookIdObject.getJSONObject("owner")
                        val ownerName = bookOwner.getString("name")
                        val bookAuthor = bookIdObject.getString("author")

                        books.add(
                            ListAppealEntity(
                                bookId,
                                bookTitle,
                                bookStatus,
                                bookThumbnail,
                                ownerName,
                                bookAuthor,
                                trans_status
                            )
                        )
                    }
                }
                Log.d("Parsed Books", "Number of books fetched: ${books.size}")


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
    }

    companion object {
    }
}