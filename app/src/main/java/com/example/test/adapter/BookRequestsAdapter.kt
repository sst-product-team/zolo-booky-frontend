package com.example.test.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test.R
import com.example.test.databinding.BooklistBinding
import com.example.test.entity.ListAppealEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class BookRequestsAdapter(private val context: Context, private val books: List<ListAppealEntity>) :
    RecyclerView.Adapter<BookRequestsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = BooklistBinding.bind(view)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookRequestsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booklist, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookRequestsAdapter.ViewHolder, position: Int) {
        val book = books[position]
        holder.binding.blBkTitle.text = book.name
        holder.binding.blBkStatus.text = book.trans_status
        holder.binding.tvBlAuthor.text = "requested by " + book.owner
        holder.binding.tvBlOwner.text = "requested till " + book.expected_completion_date
        holder.itemView.setOnClickListener {
            showCustomDialog()
        }
        Glide.with(context)
            .load(book.thumbnail)
            .into(holder.binding.imageView)
    }


    private fun showCustomDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.bottomsheet_conformation, null)
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        val tvTitleDialogBox: TextView = dialogView.findViewById(R.id.tvTitleDialogBox)
        val tvBorrowDateText: TextView = dialogView.findViewById(R.id.tvBorrowDateText)
        val btnCancel: MaterialButton = dialogView.findViewById(R.id.btnCancel)
        val btnConfirm: MaterialButton = dialogView.findViewById(R.id.btnConfirm)

        tvTitleDialogBox.text = "Approve the Requested Book"
        tvBorrowDateText.text = "Approve the book now"

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun getItemCount() = books.size
}
