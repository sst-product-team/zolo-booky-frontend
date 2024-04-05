package com.example.test.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.test.HistoryBottomSheet
import com.example.test.R
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

        holder.bind(book.name, book.owner.USER_NAME, book.status, book.thumbnail,book.author)

        holder.itemView.setOnClickListener {
            Log.d("clickey", "onBindViewHolder: clicked on: ${books[position]}")
           showBottomSheetDialog(book)



        }
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

        fun bind(title: String,owner: String, status: String, thumbnail: String , author: String ) {
            binding.blBkTitle.text = title
            binding.tvBlAuthor.text = author
            binding.tvBlOwner.text = owner
            binding.blBkStatus.text = status

            Glide.with(itemView.context)
                .load(thumbnail)
                .into(binding.imageView)

            Log.d("TAGTUG", "$title $owner $status $author")
        }
    }
}
