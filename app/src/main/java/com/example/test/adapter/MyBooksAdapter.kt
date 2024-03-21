package com.example.test.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test.databinding.BooklistBinding
import com.example.test.entity.MyBookEntity


class MyBooksAdapter(private val books: MutableList<MyBookEntity>) : RecyclerView.Adapter<MyBooksAdapter.RowViewHolder>() {

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
