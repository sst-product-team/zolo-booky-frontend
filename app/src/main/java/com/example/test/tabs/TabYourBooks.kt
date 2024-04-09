package com.example.test.tabs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.test.R
import com.example.test.activity.PostBooksActivity
import com.example.test.adapter.MyBooksAdapter
import com.example.test.databinding.FragmentTabYourbooksBinding
import com.example.test.entity.MyBookEntity
import com.example.test.entity.UserEntity
import com.example.test.globalContexts.Constants


class TabYourBooks : Fragment() {
    private lateinit var binding: FragmentTabYourbooksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): android.view.View? {
        binding = FragmentTabYourbooksBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fetchmybooks()


        val openPostButton = view.findViewById<Button>(R.id.addbooksButton)
        openPostButton.setOnClickListener{
            val intent = Intent(activity, PostBooksActivity::class.java)
            startActivity(intent)
        }
    }
     fun fetchmybooks(){


        val shimmerFrame = binding.shimmerViewMybooks
        shimmerFrame.startShimmer()

        val recyclerView = view?.findViewById<RecyclerView>(R.id.MyBooksRecyclerView)

        val queue = Volley.newRequestQueue(requireContext())
        val url = "${Constants.BASE_URL}/v0/books?owner=${Constants.USER_ID}"
         Log.d("TAGid", "fetchmybooks: ${Constants.USER_ID}")
        var count = 0
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val books = mutableListOf<MyBookEntity>()
                for (i in 0 until response.length()) {
                    val bookObject = response.getJSONObject(i)
                    val bookId = bookObject.getInt("id")
                    val bookTitle = bookObject.getString("name")
                    val bookStatus = bookObject.getString("status")
                    val author = bookObject.getString("author")
                    val bookThumbnail = bookObject.getString("thumbnail")
                    val owner = bookObject.getJSONObject("owner")
                    var ownerEntity = UserEntity(
                        owner.getInt("id"),
                        owner.getString("name"),
                        owner.getString("fcmToken")
                    )
                    val numReq = bookObject.getInt("requestCount")

                    Log.d("GUCCI", Constants.USER_ID.toString())

                        count++
                        books.add(
                            MyBookEntity(
                                bookId,
                                bookTitle,
                                bookStatus,
                                bookThumbnail,
                                ownerEntity,
                                author,
                                numReq
                            )
                        )

                }
                shimmerFrame.stopShimmer()
                shimmerFrame.visibility = View.GONE
                if (count==0){
                    val msg  = view?.findViewById<CardView>(R.id.msgO)
                    if (msg != null) {
                        msg.visibility = View.VISIBLE
                    }
                }

                val adapter = MyBooksAdapter(requireContext(), books)
                if (recyclerView != null) {
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                }
                if (recyclerView != null) {
                    recyclerView.adapter = adapter
                }

            },
            { error ->
                Log.e("VolleyExample", "Error: $error")
                shimmerFrame.stopShimmer()
                shimmerFrame.visibility = View.GONE
            }
        )

        queue.add(jsonArrayRequest)



    }




    companion object {
        const val POST_BOOK_REQUEST = 123 // Any unique integer constant
    }

}

