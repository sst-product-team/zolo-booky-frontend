package com.example.test.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.test.HistoryBottomSheet
import com.example.test.R
import com.example.test.activity.BookInfoOwnerActivity
import com.example.test.databinding.BooklistBinding
import com.example.test.entity.ListAppealEntity
import com.example.test.entity.MyBookEntity
import com.example.test.globalContexts.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import org.json.JSONObject


class MyBooksAdapter(private val context: Context,private val books: MutableList<MyBookEntity>) : RecyclerView.Adapter<MyBooksAdapter.RowViewHolder>() {

    var queue = Volley.newRequestQueue(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        return RowViewHolder(
            BooklistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val book = books[position]
        if(book.status == "AVAILABLE"||book.status == "DELISTED"){
            holder.itemView.setOnLongClickListener {
                showBottomSheetDialog(book)
                true
            }
        }


        holder.itemView.setOnClickListener {
            Log.d("clickey", "onBindViewHolder: clicked on: ${books[position]}")

            val intent = Intent(context, BookInfoOwnerActivity::class.java)
            intent.putExtra("bookId", book.id)
            intent.putExtra("bookName", book.name)
            intent.putExtra("bookStatus", book.status)
            intent.putExtra("bookThumbnail", book.thumbnail)
            intent.putExtra("bookAuthor", book.author)
            intent.putExtra("bookOwner", book.owner.USER_NAME)
            context.startActivity(intent)

        }
    getNumberOfRequests(book.id) { numberOfRequests ->
        holder.bind(book.name, book.owner.USER_NAME, book.status, book.thumbnail, book.author, numberOfRequests)
    }
}

    private fun getNumberOfRequests(bookId: Int, callback: (Int) -> Unit) {
        val url = "${Constants.BASE_URL}/v0/appeals"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                var numberOfRequests = 0
                for (i in 0 until response.length()) {
                    val appealObject = response.getJSONObject(i)
                    val book = appealObject.getJSONObject("bookId")
                    val bookIdFromAppeal = book.getInt("id")
                    if (bookIdFromAppeal == bookId) {
                        numberOfRequests = book.getInt("requestCount")
                    }
                }
                callback(numberOfRequests)
            },
            { error ->
                Log.d("NUMBER OF REQUESTS", "ERROR OCCURED")
            }
        )
        queue.add(jsonArrayRequest)
    }
    private fun showBottomSheetDialog(appeal: MyBookEntity) {
        Log.d("inside sheet", "showBottomSheetDialog: ")
        val dialogView = LayoutInflater.from(context).inflate(R.layout.bottomsheet_conformation, null)
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        Log.d("status", "showBottomSheetDialog: ${appeal.status}")

        if(appeal.status == "AVAILABLE"){
            dialogView.findViewById<TextView>(R.id.tvTitleDialogBox).text = "Do you want to delist this book?"

            val btnCancel: MaterialButton = dialogView.findViewById(R.id.btnCancel)
            val btnConfirm: MaterialButton = dialogView.findViewById(R.id.btnConfirm)
            val btnTxt = dialogView.findViewById<TextView>(R.id.tvBorrowDateText)
            btnTxt.text = "Delist the book ?"
            btnConfirm.text = "Delist"
            btnCancel.text = "Cancel"

            dialog.show()
            btnConfirm.setOnClickListener {
                delistBookAppeal(appeal)
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        else if(appeal.status == "DELISTED"){
            dialogView.findViewById<TextView>(R.id.tvTitleDialogBox).text = "Do you want to list this book Again?"
            val btnCancel: MaterialButton = dialogView.findViewById(R.id.btnCancel)
            val btnConfirm: MaterialButton = dialogView.findViewById(R.id.btnConfirm)
            val btnTxt = dialogView.findViewById<TextView>(R.id.tvBorrowDateText)
            btnTxt.text = "List the book again?"
            btnConfirm.text = "List"
            btnCancel.text = "Cancel"

            dialog.show()

            btnConfirm.setOnClickListener {
                listBookAppeal(appeal)
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
    private fun delistBookAppeal(appeal: MyBookEntity) {
        val url = "${Constants.BASE_URL}/v0/books/${appeal.id}"
        val newStatus = JSONObject().apply {
            put("trans_status", "DELISTED")
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.DELETE, url, newStatus,
            { response ->
                Log.d("DELIST APPEAL UPDATE", "APPEAL UPDATED")
                Toast.makeText(context, "Book Delisted", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Log.d("DELIST APPEAL UPDATE", "ERROR OCCURED")
            }
        )
        queue.add(jsonObjectRequest)
    }
    private fun listBookAppeal(appeal: MyBookEntity){
        val url = "${Constants.BASE_URL}/v0/books/${appeal.id}"
        val newStatus = JSONObject().apply {
            put("trans_status", "AVAILABLE")
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PATCH, url, newStatus,
            { response ->
                Log.d("LIST APPEAL UPDATE", "APPEAL UPDATED")
                Toast.makeText(context, "Book Listed", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Log.d("LIST APPEAL UPDATE", "ERROR OCCURED")
            }
        )
        queue.add(jsonObjectRequest)
    }


    override fun getItemCount(): Int {
        return books.size
    }

    fun updateData(newBooks: MutableList<MyBookEntity>) {
        books.clear()
        books.addAll(newBooks)
        notifyDataSetChanged()
    }

    class RowViewHolder(private val binding: BooklistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String,owner: String, status: String, thumbnail: String , author: String ,numberOfRequests:Int) {
            binding.blBkTitle.text = title
            binding.tvBlAuthor.text = author
            binding.tvBlOwner.text = numberOfRequests.toString()
            binding.blBkStatus.text = status

            Glide.with(itemView.context)
                .load(thumbnail)
                .into(binding.imageView)

            Log.d("TAGTUG", "$title $owner $status $author")
        }
    }

}
