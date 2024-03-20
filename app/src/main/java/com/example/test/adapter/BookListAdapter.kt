package com.example.test.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test.activity.BookInfoActivity
import com.example.test.entity.ListBookEntity
import com.example.test.databinding.BooklistBinding

class BookListAdapter(private val books: MutableList<ListBookEntity>) :
    RecyclerView.Adapter<BookListAdapter.RowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        return RowViewHolder(BooklistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book.name, book.status,book.author,book.thumbnail)

        holder.itemView.setOnClickListener {
            val bookId = book.id

            // Navigate to the book details activity, passing the book ID as an argument
            val intent = Intent(it.context, BookInfoActivity::class.java)
            intent.putExtra("bookId", bookId)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = books.size

    class RowViewHolder(private val binding: BooklistBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String, status: String,author:String,thumbnail:String){
            binding.blBkTitle.text = title
            binding.blBkStatus.text = status
            binding.tvBlAuthor.text = author

            Glide.with(itemView.context)
                .load(thumbnail)
                .into(binding.imageView)
        }
    }
}
