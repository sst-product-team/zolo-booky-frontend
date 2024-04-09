package com.example.test.tabs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
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
import com.example.test.adapter.MyRequestsAdapter
import com.example.test.adapter.ViewHistoryAdapter
import com.example.test.databinding.BottomsheetScrollerBinding
import com.example.test.databinding.FragmentTabBorrowedBinding
import com.example.test.entity.AppealEntity
import com.example.test.entity.ListAppealEntity
import com.example.test.entity.ListBookEntity
import com.example.test.globalContexts.Constants
import com.example.test.globalContexts.Constants.USER_ID
import com.example.test.globalContexts.Constants.isAccepted
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.log

class TabBorrowed : Fragment() {

    private lateinit var binding: FragmentTabBorrowedBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingSpinner: CardView // Add this line
    private lateinit var searchView: SearchView
    private val books = mutableListOf<ListBookEntity>()
    private lateinit var queue: RequestQueue
    private val SEARCH_QUERY_DELAY = 500 // Milliseconds
    private var page = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTabBorrowedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        queue = Volley.newRequestQueue(requireContext())

        // shimmer

        val shimmerFrameLayout2 = binding.shimmerViewContainerBookList2
        // Starting the shimmer effect before making the API request

        shimmerFrameLayout2.startShimmer()



        recyclerView = binding.BookRecyclerView
        loadingSpinner = binding.cvProgressBar // Initialize loadingSpinner here
        setupPagination()
        fetchInitialBooks()


        val layoutParams = binding.constraintLayout4.layoutParams as ConstraintLayout.LayoutParams

        val viewButton: TextView = binding.viewHistoryBtn
        viewButton.setOnClickListener {

            Log.d("onbtnVHTAG", "onViewCreated: clicked")
        }


        // bottom sheets
        val viewHistoryB: TextView = binding.viewHistoryBtn
        viewHistoryB.setOnClickListener {
            showCustomDialog1()
        }


        val myreqbtn: TextView = binding.myRequestsBtn
        myreqbtn.setOnClickListener {
            showCustomDialog2()
            Log.d("yupppp", "hjrfh")
        }




        reloadBorrowed()


    }
    private val reloadBorrowedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            reloadBorrowed()
        }
    }

    private val reloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            reloadBorrowed()
        }
    }

    override fun onResume() {
        super.onResume()
        // This method will be called every time the fragment becomes visible
//        if (isAccepted == true) {
//           reloadBorrowed()
//        }
//        isAccepted = false

        context?.registerReceiver(reloadReceiver, IntentFilter("com.example.test.RELOAD_ACTION"))

    }

    override fun onPause() {
        super.onPause()
//        if (isAccepted == true) {
//            reloadBorrowed()
//            Log.d("pausy","called")
//        }
//        isAccepted = false

        context?.unregisterReceiver(reloadReceiver)

    }

    public fun reloadBorrowed(){
        ///// for borrowed books by borrower.

        val shimmerFrameLayout = binding.shimmerViewContainerBookList
        shimmerFrameLayout.startShimmer()
        binding.rvBorrowBookList.visibility = View.GONE
        binding.msg.visibility = View.GONE
        shimmerFrameLayout.visibility = View.VISIBLE


        val recyclerView = binding.rvBorrowBookList
        val queue = Volley.newRequestQueue(requireContext())
        val userId = USER_ID
        Log.d("meraki","${userId}")

        val url = "${Constants.BASE_URL}/v0/appeals?borrower=${USER_ID}"

        Log.d("merahi", "${url}")

        var token: String

        Log.d("API Request URLL", url)

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
                    if (trans_status=="ONGOING" && borrowerId== USER_ID){
                        count++
                        val transId = appealObject.getInt("trans_id")
                        val bookIdObject = appealObject.getJSONObject("bookId")
                        val bookId = bookIdObject.getInt("id")
                        val bookTitle = bookIdObject.getString("name")
                        val bookStatus = bookIdObject.getString("status")
                        val bookThumbnail = bookIdObject.getString("thumbnail")
                        val maxBorrow = bookIdObject.getInt("maxBorrow")
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
                                expected_completion_date,
                                bookId,
                                status_change_date
                            )
                        )
                    }
                }

                Log.d("Parsed Books borroweding", "Number of books fetched: ${books.size}")
                Log.d("Count", count.toString())
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE




                if(count==0){

                    val layoutParams = binding.constraintLayout4.layoutParams as ConstraintLayout.LayoutParams
                    val heightInDp = 200 // desired height in dp
                    val density = resources.displayMetrics.density
                    val heightInPixels = (heightInDp * density).toInt()
                    layoutParams.height = heightInPixels
                    binding.constraintLayout4.layoutParams = layoutParams

                    binding.msg.visibility = View.VISIBLE

                }
                else{
                    binding.msg.visibility = View.GONE
                    val layoutParams = binding.constraintLayout4.layoutParams as ConstraintLayout.LayoutParams
                    val heightInDp = 300 // desired height in dp
                    val density = resources.displayMetrics.density
                    val heightInPixels = (heightInDp * density).toInt()
                    layoutParams.height = heightInPixels
                    binding.constraintLayout4.layoutParams = layoutParams
                }


                if (count==1){
                    val layoutParams = binding.constraintLayout4.layoutParams as ConstraintLayout.LayoutParams
                    val heightInDp = 200 // desired height in dp
                    val density = resources.displayMetrics.density
                    val heightInPixels = (heightInDp * density).toInt()
                    layoutParams.height = heightInPixels
                    binding.constraintLayout4.layoutParams = layoutParams
                }
                if(count>1){
                    val layoutParams = binding.constraintLayout4.layoutParams as ConstraintLayout.LayoutParams
                    val heightInDp = 300 // desired height in dp
                    val density = resources.displayMetrics.density
                    val heightInPixels = (heightInDp * density).toInt()
                    layoutParams.height = heightInPixels
                    binding.constraintLayout4.layoutParams = layoutParams
                }

                binding.rvBorrowBookList.visibility= View.VISIBLE
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

        val recyclerView2 = view?.findViewById<RecyclerView>(R.id.BookRecyclerView)
        val loadingSpinner = view?.findViewById<CardView>(R.id.cv_progress_bar)

        val shimmerFrameLayout2 = binding.shimmerViewContainerBookList2


        val url2 = "${Constants.BASE_URL}/v0/books"
        shimmerFrameLayout2.startShimmer()
        shimmerFrameLayout2.visibility = View.VISIBLE
        Log.d("API Request URL", url2)
        shimmerFrameLayout2.stopShimmer()
        shimmerFrameLayout2.visibility = View.GONE
        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {


            private var lastSearchTime = 0L
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadingSpinner?.visibility = View.GONE

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                loadingSpinner?.visibility = View.GONE

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
                                recyclerView2?.layoutManager = LinearLayoutManager(requireContext())
                                recyclerView2?.adapter = adapter
                                adapter.notifyDataSetChanged()
                            },
                            { error ->
                                Log.e("API Error", error.toString())
                            }
                        )
                        queue.add(jsonArrayRequest2)
                    }
                } else {

                    clearAndSetupPagination(recyclerView2)
                }
                return true
            }
        })
    }
    fun clearAndSetupPagination(recyclerView2:RecyclerView?) {
        books.clear()
        recyclerView2?.clearOnScrollListeners()
        page = 0
        fetchInitialBooks()
        setupPagination() // Re-attach pagination logic
    }


    private fun fetchInitialBooks() {
        recyclerView = binding.BookRecyclerView
        queue = Volley.newRequestQueue(requireContext())
        val url = "${Constants.BASE_URL}/v0/books"
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











        // logics for opening various bottomsheets based on btns








    }

    fun loadMoreData(page: Int, loadingSpinner: CardView) {
        var c = 0
        loadingSpinner.visibility = View.VISIBLE
        val recyclerView = binding.BookRecyclerView
        val url = "${Constants.BASE_URL}/v0/books?page=$page"
        val queue = Volley.newRequestQueue(requireContext())
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                if (response.length() <= 4) {
                    recyclerView.clearOnScrollListeners() // Stop loading more data
                }
                for (i in 0 until response.length()) {
                    val bookObject = response.getJSONObject(i)
                    val ownerObject = bookObject.getJSONObject("owner")

                    val bookId = bookObject.getInt("id")
                    val bookTitle = bookObject.getString("name")
                    val bookStatus = bookObject.getString("status")
                    val bookThumbnail = bookObject.getString("thumbnail")
                    val ownerName = ownerObject.getString("name")
                    val bookAuthor = bookObject.getString("author")
                    c++
                    if (bookStatus == "AVAILABLE") {
                        books.add(
                            ListBookEntity(bookId, bookTitle, bookStatus, bookThumbnail, ownerName, bookAuthor)
                        )
                    }
                }
                Log.d("no of books fetched", "loadMoreData: ${response.length()}")
                Log.d("TAGBooka", "loadMoreData: no of books added $c")
                binding.BookRecyclerView.adapter?.notifyDataSetChanged()
                loadingSpinner.visibility = View.GONE
            },
            { error ->
                Log.e("API Error", error.toString())
                loadingSpinner.visibility = View.GONE
            }
        )

        queue.add(jsonArrayRequest)
    }
    companion object {
    }
    private fun showCustomDialog1() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.bottomsheet_scroller, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)

        var binding9 = BottomsheetScrollerBinding.inflate(layoutInflater)

        val shimmerFrameLayout = dialogView.findViewById<ShimmerFrameLayout>(R.id.shimmer_scroller_view)
        shimmerFrameLayout.startShimmer()





        val recyclerViewy = dialogView.findViewById<RecyclerView>(R.id.BookRecyclerViewy)

        val queue = Volley.newRequestQueue(requireContext())
        val userId = USER_ID

        val url = "${Constants.BASE_URL}/v0/appeals?borrower=${userId}"

        var token: String

        Log.d("API Request URLL", url)
        var count =0

        val jsonArrayRequest9 = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API Response", response.toString())
                val books = mutableListOf<ListAppealEntity>()
                val borrowerData = mutableListOf<AppealEntity>()

                for (i in 0 until response.length()){
                    val appealObject = response.getJSONObject(i)
                    val borrowerObject = appealObject.getJSONObject("borrowerId")
                    val borrowerId = borrowerObject.getInt("id")
                    val trans_status = appealObject.getString("trans_status")
                    if (trans_status=="COMPLETED" || trans_status=="REJECTED"){
                        count++
                        val transId = appealObject.getInt("trans_id")
                        val bookIdObject = appealObject.getJSONObject("bookId")
                        val bookId = bookIdObject.getInt("id")
                        val bookTitle = bookIdObject.getString("name")
                        val bookStatus = bookIdObject.getString("status")
                        val bookThumbnail = bookIdObject.getString("thumbnail")
                        val maxBorrow = bookIdObject.getInt("maxBorrow")
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
                Log.d("Parsyy", "Number of books fetched: ${books.size}")

                Log.d("County", count.toString())
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE



                if(count==0){

                   val textyy = dialogView.findViewById<CardView>(R.id.msg1)
                    textyy.visibility = View.VISIBLE

                    val innertxt = dialogView.findViewById<TextView>(R.id.cardtxt)
                    innertxt.text = "You have never borrowed any books"
                }


                val adaptery = ViewHistoryAdapter(requireContext(), books)
                recyclerViewy.layoutManager = LinearLayoutManager(requireContext())
                recyclerViewy.adapter = adaptery
                adaptery.notifyDataSetChanged()
            },
            { error ->
                Log.e("API Error", error.toString())
                Log.e("VolleyExample", "Error: $error")
            }
        )

        queue.add(jsonArrayRequest9)





        dialog.show()
    }
    private fun setupPagination() {
        val recyclerView2 = binding.BookRecyclerView
        val loadingSpinner = binding.cvProgressBar

        recyclerView2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val endHasBeenReached = lastVisibleItem + 5 >= totalItemCount
                if (totalItemCount > 0 && endHasBeenReached) {
                    page++
                    loadMoreData(page, loadingSpinner)
                }
            }
        })
    }

    private fun showCustomDialog2() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.bottomsheet_scroller, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)


        var binding9 = BottomsheetScrollerBinding.inflate(layoutInflater)

        val shimmerFrameLayout = dialogView.findViewById<ShimmerFrameLayout>(R.id.shimmer_scroller_view)
        shimmerFrameLayout.startShimmer()

        val text = dialogView.findViewById<TextView>(R.id.texty)

        text.text = "Pending Requests"


        val recyclerViewy = dialogView.findViewById<RecyclerView>(R.id.BookRecyclerViewy)

        val queue = Volley.newRequestQueue(requireContext())
        val userId = USER_ID

        val url = "${Constants.BASE_URL}/v0/appeals?borrower=${userId}"

        var token: String

        Log.d("API Request URL", url)
        var count =0

        val jsonArrayRequest9 = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API Response", response.toString())
                val books = mutableListOf<ListAppealEntity>()
                val borrowerData = mutableListOf<AppealEntity>()

                for (i in 0 until response.length()){
                    val appealObject = response.getJSONObject(i)
                    val borrowerObject = appealObject.getJSONObject("borrowerId")
                    val borrowerId = borrowerObject.getInt("id")
                    val trans_status = appealObject.getString("trans_status")
                    if (trans_status =="PENDING"){
                        count++
                        val transId = appealObject.getInt("trans_id")
                        val bookIdObject = appealObject.getJSONObject("bookId")
                        val bookId = bookIdObject.getInt("id")
                        val bookTitle = bookIdObject.getString("name")
                        val bookStatus = bookIdObject.getString("status")
                        val bookThumbnail = bookIdObject.getString("thumbnail")
                        val maxBorrow = bookIdObject.getInt("maxBorrow")
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
                Log.d("Parsyy", "Number of books fetched: ${books.size}")

                Log.d("County", count.toString())
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE






                if(count==0){

                    val textyy = dialogView.findViewById<CardView>(R.id.msg1)
                    textyy.visibility = View.VISIBLE

                    val innertxt = dialogView.findViewById<TextView>(R.id.cardtxt)
                    innertxt.text = "You have no Pending Requests"
                }

                val adaptery = MyRequestsAdapter(requireContext(), books)
                recyclerViewy.layoutManager = LinearLayoutManager(requireContext())
                recyclerViewy.adapter = adaptery
                adaptery.notifyDataSetChanged()
            },
            { error ->
                Log.e("API Error", error.toString())
                Log.e("VolleyExample", "Error: $error")
            }
        )

        queue.add(jsonArrayRequest9)





        dialog.show()
    }
}