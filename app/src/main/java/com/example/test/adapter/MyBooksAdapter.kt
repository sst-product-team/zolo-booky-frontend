package com.example.test.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
        holder.bind(book.name)
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

        fun bind(title: String) {
            binding.blBkTitle.text = title
        }
    }
}
