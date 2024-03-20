package com.example.test.tabs

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.test.adapter.BookRequestsAdapter
import com.example.test.databinding.FragmentTabRequestsBinding
import com.example.test.entity.ListAppealEntity
import com.example.test.globalContexts.Constants
import java.text.SimpleDateFormat
import java.util.Locale


class TabRequests : Fragment() {
    private lateinit var binding: FragmentTabRequestsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): android.view.View? {
        binding = FragmentTabRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.rvRequestsBookList
        val queue = Volley.newRequestQueue(requireContext())

        val url = "${Constants.BASE_URL}/v0/appeals"

        Log.d("API Request URL", url)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API Response", response.toString())
                val books = mutableListOf<ListAppealEntity>()
                for (i in 0 until response.length()) {
                    val appealObject = response.getJSONObject(i)

                    val bookObject = appealObject.getJSONObject("book_id")
                    val transId = appealObject.getInt("trans_id")
                    val bookId = bookObject.getString("id")
                    val bookName = bookObject.getString("name")
                    val bookStatus = bookObject.getString("status")
                    val bookAuthor = bookObject.getString("author")
                    val bookOwner = bookObject.getJSONObject("owner").getInt("id")
                    val bookBorrower = appealObject.getJSONObject("borrower_id").getString("name")
                    val bookThumnail = bookObject.getString("thumbnail")
                    val trans_status = appealObject.getString("trans_status")
                    val expected_completion_date =
                        appealObject.getString("expected_completion_date")
                    val dates: String = expected_completion_date.split(" ")[0]
                    if (bookOwner == Constants.USER_ID) {
                        books.add(
                            ListAppealEntity(
                                transId,
                                bookName,
                                bookStatus,
                                bookThumnail,
                                bookBorrower,
                                bookAuthor,
                                trans_status,
                                dates
                            )
                        )
                    }
                }
                Log.d("Parsed Books", "Number of books fetched: ${books.size}")

                val adapter = BookRequestsAdapter(requireContext(), books)
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

    public fun StringToDate(s: String): String {
        val pattern = "dd/MM/yyyy"
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        val date = formatter.parse(s)
        return formatter.format(date).toString()

    }
}

