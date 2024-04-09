package com.example.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.test.adapter.BookBorrowAdapter
import com.example.test.adapter.ViewHistoryAdapter
import com.example.test.databinding.BottomsheetScrollerBinding
import com.example.test.databinding.FragmentTabBorrowedBinding
import com.example.test.entity.AppealEntity
import com.example.test.entity.ListAppealEntity
import com.example.test.entity.ListBookEntity
import com.example.test.globalContexts.Constants

class HistoryBottomSheet: DialogFragment() {
    private lateinit var binding: BottomsheetScrollerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetScrollerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.BookRecyclerViewy

        val queue = Volley.newRequestQueue(requireContext())

        val url = "${Constants.BASE_URL}/v0/appeals"

        Log.d("API Request URL", url)
        var count =0

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API Response", response.toString())
                val books = mutableListOf<ListAppealEntity>()

                for (i in 0 until response.length()){
                    val appealObject = response.getJSONObject(i)
                    val borrowerObject = appealObject.getJSONObject("borrower_id")
                    val borrowerId = borrowerObject.getInt("id")
                    val trans_status = appealObject.getString("trans_status")
                    if (borrowerId == Constants.USER_ID){
                        count++
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

                Log.d("Count", count.toString())

                val adapter = ViewHistoryAdapter(requireContext(), books)
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
}