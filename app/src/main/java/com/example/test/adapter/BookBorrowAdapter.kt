package com.example.test.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.test.R
import com.example.test.databinding.BooklistBinding
import com.example.test.entity.ListAppealEntity
import com.example.test.globalContexts.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import org.json.JSONObject

class BookBorrowAdapter(private val context: Context, private val books: List<ListAppealEntity>) : RecyclerView.Adapter<BookBorrowAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = BooklistBinding.bind(view)
    }

    var queue = Volley.newRequestQueue(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booklist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]
        holder.binding.blBkTitle.text = book.name
        holder.binding.blBkStatus.text = book.trans_status
        holder.binding.tvBlAuthor.text = book.author
        holder.binding.tvBlOwner.text = book.owner

        Glide.with(context)
            .load(book.thumbnail)
            .into(holder.binding.imageView)

        if (book.status == "AVAILABLE") {
            holder.itemView.setOnClickListener(View.OnClickListener {
                Toast.makeText(context, "Book Borrow approval by Owner Pending", Toast.LENGTH_SHORT).show()
            })

        } else {
            holder.itemView.setOnClickListener {
                showCustomDialog(book)
            }
        }
    }

    private fun showCustomDialog(appeal: ListAppealEntity) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.bottomsheet_conformation, null)
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        val tvTitleDialogBox: TextView = dialogView.findViewById(R.id.tvTitleDialogBox)
        val tvBorrowDateText: TextView = dialogView.findViewById(R.id.tvBorrowDateText)
        val btnCancel: MaterialButton = dialogView.findViewById(R.id.btnCancel)
        val btnConfirm: MaterialButton = dialogView.findViewById(R.id.btnConfirm)

        tvTitleDialogBox.text = "Return the Borrowed Book"
        tvBorrowDateText.text = "Return the book now?"

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            returnBook(appeal)
        }

        dialog.show()
    }

    public fun returnBook(appeal: ListAppealEntity) {
        val url = "${Constants.BASE_URL}/v0/appeals/${appeal.trans_id}"
        val newStatus = JSONObject().apply {
            put("trans_status", "COMPLETED")
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PATCH, url, newStatus,
            { response ->
                Log.d("APPEAL UPDATE", "APPEAL UPDATED")
                Toast.makeText(context, "Book returned successfully", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Log.d("APPEAL UPDATE", "ERROR OCCURED")
            }
        )
        queue.add(jsonObjectRequest)

    }

    override fun getItemCount() = books.size
}
