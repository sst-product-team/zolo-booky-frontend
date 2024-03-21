package com.example.test.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

class BookRequestsAdapter(private val context: Context, private val books: List<ListAppealEntity>) :
    RecyclerView.Adapter<BookRequestsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = BooklistBinding.bind(view)
    }

    var queue = Volley.newRequestQueue(context)

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
            showCustomDialog(book)
        }
        Glide.with(context)
            .load(book.thumbnail)
            .into(holder.binding.imageView)
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

        val appealStatus = appeal.trans_status
        tvTitleDialogBox.text = appeal.name
        if (appealStatus == "PENDING") {
            tvBorrowDateText.text = "Approve request for " + appeal.owner
            btnCancel.text = "Reject"
            btnConfirm.text = "Accept"
            btnCancel.setOnClickListener {
                dialog.dismiss()
                rejectAppeal(appeal)
            }

            btnConfirm.setOnClickListener {
                acceptaAppeal(appeal)
            }
        } else if (appealStatus == "ONGOING") {
            tvBorrowDateText.text = "Update request for " + appeal.owner
            btnCancel.text = "Remind"
            btnConfirm.text = "Complete"
            btnCancel.setOnClickListener {
                dialog.dismiss()
//                remindBorrower(appeal)
            }

            btnConfirm.setOnClickListener {
//                completeAppeal(appeal)
            }
        } else if (appealStatus == "REJECTED") {
            tvBorrowDateText.text = "Request rejected on " + appeal.stats_change_date
            btnCancel.visibility = View.GONE
            btnConfirm.visibility = View.GONE
//            btnCancel.setOnClickListener {
//                dialog.dismiss()
////                remindBorrower(appeal)
//            }
//
//            btnConfirm.setOnClickListener {
////                completeAppeal(appeal)
//            }

        } else if (appealStatus == "COMPLETED") {
            tvBorrowDateText.text = "Request completed on " + appeal.stats_change_date
            btnCancel.visibility = View.GONE
            btnConfirm.visibility = View.GONE
            btnCancel.setOnClickListener {
//                remindBorrower(appeal)
            }

            btnConfirm.setOnClickListener {
//                completeAppeal(appeal)
            }
        }

        dialog.show()
    }

    fun rejectAppeal(appeal: ListAppealEntity) {
        val url = "${Constants.BASE_URL}/v0/appeals/${appeal.trans_id}"
        val newStatus = JSONObject().apply {
            put("trans_status", "REJECTED")
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PATCH, url, newStatus,
            { response ->
                Log.d("APPEAL UPDATE", "APPEAL UPDATED")
            },
            { error ->
                Log.d("APPEAL UPDATE", "ERROR OCCURED")
            }
        )
        queue.add(jsonObjectRequest)
    }

    fun acceptaAppeal(appeal: ListAppealEntity) {
        val url = "${Constants.BASE_URL}/v0/appeals/${appeal.trans_id}"
        Log.d("ACCEPT URL", url)
        val newStatus = JSONObject().apply {
            put("trans_status", "ONGOING")
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PATCH, url, newStatus,
            { response ->
                Log.d("APPEAL UPDATE", "APPEAL UPDATED")
            },
            { error ->
                Log.d("APPEAL UPDATE", "ERROR OCCURED")
            }
        )
        queue.add(jsonObjectRequest)
    }

    override fun getItemCount() = books.size
}
