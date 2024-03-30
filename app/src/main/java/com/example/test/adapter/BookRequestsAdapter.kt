package com.example.test.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
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

    override fun onBindViewHolder(holder: BookRequestsAdapter.ViewHolder, position: Int) {
        val book = books[position]
        holder.binding.blBkTitle.text = book.name
        holder.binding.blBkStatus.text = book.trans_status
        holder.binding.tvBlAuthor.text = "requested by " + book.owner
        holder.binding.tvBlOwner.text = "requested till " + book.expected_completion_date

        Glide.with(context)
            .load(book.thumbnail)
            .into(holder.binding.imageView)
        if (book.status == "AVAILABLE") {
            if (book.trans_status == "PENDING") {
                holder.itemView.setOnClickListener {
                    showCustomDialog(book)
                }
                val statusTextView = holder.binding.blBkStatus
                val status = book.trans_status
                updateButtonColor(status, statusTextView)

            } else if (book.trans_status == "REJECTED") {
                holder.itemView.setOnClickListener(View.OnClickListener {
                    Toast.makeText(
                        context,
                        "You Rejected this book request",
                        Toast.LENGTH_SHORT
                    ).show()
                })
                val statusTextView = holder.binding.blBkStatus
                val status = book.trans_status
                updateButtonColor(status, statusTextView)
            } else if (book.trans_status == "COMPLETED") {
                holder.itemView.setOnClickListener(View.OnClickListener {
                    Toast.makeText(context, "Book Borrow ", Toast.LENGTH_SHORT).show()
                })
                val statusTextView = holder.binding.blBkStatus
                val status = book.trans_status
                updateButtonColor(status, statusTextView)
            }
        } else {
            holder.itemView.setOnClickListener {
                showCustomDialog(book)
            }
            val statusTextView = holder.binding.blBkStatus
            val status = book.trans_status
            updateButtonColor(status, statusTextView)
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

        val appealStatus = appeal.trans_status
        tvTitleDialogBox.text = appeal.name
        if (appealStatus == "PENDING") {
            tvBorrowDateText.text = "Approve request for " + appeal.owner
            btnCancel.text = "Reject"
            btnConfirm.text = "Accept"
            btnCancel.setOnClickListener {
                dialog.dismiss()
                rejectAppeal(appeal,dialog)
            }

            btnConfirm.setOnClickListener {
                acceptaAppeal(appeal,dialog)
            }
        } else if (appealStatus == "ONGOING") {
            tvBorrowDateText.text = "Update request for " + appeal.owner
            btnCancel.text = "Remind"
            btnConfirm.text = "Complete"
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnConfirm.setOnClickListener {
                dialog.dismiss()
            }
        } else if (appealStatus == "REJECTED") {
            tvBorrowDateText.text = "Request rejected on " + appeal.stats_change_date
            btnCancel.visibility = View.GONE
            btnConfirm.visibility = View.GONE

        } else if (appealStatus == "COMPLETED") {
            tvBorrowDateText.text = "Request completed on " + appeal.stats_change_date
            btnCancel.visibility = View.GONE
            btnConfirm.visibility = View.GONE
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnConfirm.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    fun rejectAppeal(appeal: ListAppealEntity ,dialog: BottomSheetDialog) {
        val url = "${Constants.BASE_URL}/v0/appeals/${appeal.trans_id}"
        val newStatus = JSONObject().apply {
            put("trans_status", "REJECTED")
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PATCH, url, newStatus,
            { response ->
                Log.d("APPEAL UPDATE", "APPEAL UPDATED")
                Toast.makeText(context, "Request rejected.", Toast.LENGTH_SHORT).show()

            },
            { error ->
                Log.d("APPEAL UPDATE", "ERROR OCCURED")
            }
        )
        queue.add(jsonObjectRequest)
    }


    fun acceptaAppeal(appeal: ListAppealEntity ,dialog: BottomSheetDialog) {
        val url = "${Constants.BASE_URL}/v0/appeals/${appeal.trans_id}"
        Log.d("ACCEPT URL", url)
        val newStatus = JSONObject().apply {
            put("trans_status", "ONGOING")
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PATCH, url, newStatus,
            { response ->
                Log.d("APPEAL UPDATE", "APPEAL UPDATED")
                Toast.makeText(context, "Request approved.", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Log.d("APPEAL UPDATE", "ERROR OCCURED")
            }
        )
        queue.add(jsonObjectRequest)
    }
    fun updateButtonColor(status: String, button: TextView) {
        Log.d("hi v", "updateButtonColor: " + status)

        when (status) {
            "PENDING" -> button.backgroundTintList = ColorStateList(
                arrayOf(intArrayOf(0)),
                intArrayOf(context.resources.getColor(R.color.pending_yellow))
            )

            "REJECTED" -> button.backgroundTintList = ColorStateList(
                arrayOf(intArrayOf(0)),
                intArrayOf(context.resources.getColor(R.color.rejected_red))
            )

            "COMPLETED" -> button.backgroundTintList = ColorStateList(
                arrayOf(intArrayOf(0)),
                intArrayOf(context.resources.getColor(R.color.completed_green))
            )

            else -> button.backgroundTintList = ColorStateList(
                arrayOf(intArrayOf(0)),
                intArrayOf(context.resources.getColor(R.color.zolo_bluey))
            )
        }
    }

    override fun getItemCount() = books.size
}
