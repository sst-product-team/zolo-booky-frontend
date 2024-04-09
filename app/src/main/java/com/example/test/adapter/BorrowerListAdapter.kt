import android.content.Context
import android.content.Intent
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
import com.example.test.R
import com.example.test.databinding.BorrowerDetailsBinding
import com.example.test.entity.BorrowerEntity
import com.example.test.entity.ListAppealEntity
import com.example.test.globalContexts.Constants
import com.example.test.globalContexts.Constants.isPosted
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


class BorrowerListAdapter(private val context: Context, private val borrowers: MutableList<BorrowerEntity>) : RecyclerView.Adapter<BorrowerListAdapter.BorrowerViewHolder>() {

    class BorrowerViewHolder(val binding: BorrowerDetailsBinding) : RecyclerView.ViewHolder(binding.root)
    var queue = Volley.newRequestQueue(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowerViewHolder {
        val binding = BorrowerDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BorrowerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BorrowerViewHolder, position: Int) {
        val borrower = borrowers[position]
        // Bind the data to the views in the layout
        holder.binding.tvBorrowerName.text = borrower.name
        holder.binding.tvNumberOfDaysRequested.text = formatDate(borrower.initiationDate)



        holder.itemView.setOnClickListener {
            showCustomDialog(borrower)
        }

    }

    private fun showCustomDialog(appeal: BorrowerEntity) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.bottomsheet_conformation, null)
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)

        val tvTitleDialogBox: TextView = dialogView.findViewById(R.id.tvTitleDialogBox)
        val tvBorrowDateText: TextView = dialogView.findViewById(R.id.tvBorrowDateText)
        val btnCancel: MaterialButton = dialogView.findViewById(R.id.btnCancel)
        val btnConfirm: MaterialButton = dialogView.findViewById(R.id.btnConfirm)

        val appealStatus = appeal.bkStatus
        tvTitleDialogBox.text = appeal.name
        if (appealStatus == "AVAILABLE") {
            tvBorrowDateText.text = "Approve request till " + formatDate(appeal.completionDate)

            btnCancel.text = "Reject"
            btnConfirm.text = "Accept"
            btnCancel.setOnClickListener {
                dialog.dismiss()
                rejectAppeal(appeal,dialog)

                val intent = Intent("com.example.test.RELOAD_POP")
                context.sendBroadcast(intent)

            }

            btnConfirm.setOnClickListener {
                dialog.dismiss()
                acceptaAppeal(appeal,dialog)

                val intent = Intent("com.example.test.RELOAD_POP")
                context.sendBroadcast(intent)
            }
        }


        dialog.show()
    }


    fun rejectAppeal(appeal: BorrowerEntity, dialog: BottomSheetDialog) {
        val url = "${Constants.BASE_URL}/v0/appeals/${appeal.trans_id}"
        val newStatus = JSONObject().apply {
            put("trans_status", "REJECTED")
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PATCH, url, newStatus,
            { response ->
                Log.d("APPEAL UPDATE", "APPEAL UPDATED")
                Toast.makeText(context, "Request rejected.", Toast.LENGTH_SHORT).show()

                val intent = Intent("com.example.test.RELOAD_OWNERINFO")
                context.sendBroadcast(intent)

                val intent2 = Intent("com.example.test.RELOAD_YOURBOOKS")
                context.sendBroadcast(intent2)

                val intent3 = Intent("com.example.test.RELOAD_SEARCH")
                context.sendBroadcast(intent3)

            },
            { error ->
                Log.d("APPEAL UPDATE", "ERROR OCCURED")
            }
        )
        queue.add(jsonObjectRequest)


    }


    fun acceptaAppeal(appeal: BorrowerEntity, dialog: BottomSheetDialog) {
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
                val intent = Intent("com.example.test.RELOAD_OWNERINFO")
                context.sendBroadcast(intent)

                val intent2 = Intent("com.example.test.RELOAD_YOURBOOKS")
                context.sendBroadcast(intent2)

                val intent3 = Intent("com.example.test.RELOAD_SEARCH")
                context.sendBroadcast(intent3)
            },
            { error ->
                Log.d("APPEAL UPDATE", "ERROR OCCURED")
            }
        )
        queue.add(jsonObjectRequest)


    }

    override fun getItemCount() = borrowers.size


    fun formatDate(inputDate: String): String {

            // Find the date part by splitting the input string
            val datePart = inputDate.split(" ")[0]
            Log.d("chkker","${datePart}")

            // Parse the date part
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dateTime = LocalDate.parse(datePart, formatter)

            // Extract day, month, and year from the parsed date
            val day = dateTime.dayOfMonth
            val month = dateTime.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            val year = dateTime.year

            // Create the formatted string
            val formattedDate = "${ordinal(day)} $month, $year"

            return formattedDate

    }

    fun ordinal(number: Int): String {
        return when {
            number in 11..13 -> "${number}th"
            number % 10 == 1 -> "${number}st"
            number % 10 == 2 -> "${number}nd"
            number % 10 == 3 -> "${number}rd"
            else -> "${number}th"
        }
    }
}