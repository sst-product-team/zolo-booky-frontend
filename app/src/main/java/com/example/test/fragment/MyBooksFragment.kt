package com.example.test.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import com.example.test.PostBooksActivity
import com.example.test.R
import com.example.test.R.layout.fragment_my_books
import com.example.test.adapter.MyBooksAdapter

import com.example.test.databinding.FragmentMyBooksBinding
import com.example.test.entity.MyBookEntity
import com.example.test.Constants

class MyBooksFragment : Fragment() {

    private lateinit var binding: FragmentMyBooksBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyBooksBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.MyBooksRecyclerView)

        val queue = Volley.newRequestQueue(requireContext())
        val url = "${Constants.BASE_URL}/v0/books"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val books = mutableListOf<MyBookEntity>()
                for (i in 0 until response.length()) {
                    val bookObject = response.getJSONObject(i)

                    val bookId = bookObject.getInt("id")
                    val bookTitle = bookObject.getString("name")

                    books.add(MyBookEntity(bookId, bookTitle))
                }

                val adapter = MyBooksAdapter(books)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter
            },
            { error ->
                Log.e("VolleyExample", "Error: $error")
            }
        )

        queue.add(jsonArrayRequest)



        val openPostButton = view.findViewById<Button>(R.id.addbooksButton)
        openPostButton.setOnClickListener{
            val intent= Intent(activity,PostBooksActivity::class.java)
            startActivity(intent)
        }
    }



    companion object {

    }
}