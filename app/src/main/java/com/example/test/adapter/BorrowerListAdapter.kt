import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.test.databinding.BorrowerDetailsBinding
import com.example.test.entity.BorrowerEntity

class BorrowerListAdapter(var borrowers: List<BorrowerEntity>) : RecyclerView.Adapter<BorrowerListAdapter.BorrowerViewHolder>() {

    class BorrowerViewHolder(val binding: BorrowerDetailsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowerViewHolder {
        val binding = BorrowerDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BorrowerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BorrowerViewHolder, position: Int) {
        val borrower = borrowers[position]
        // Bind the data to the views in the layout
        holder.binding.tvBorrowerName.text = borrower.name
        holder.binding.tvNumberOfDaysRequested.text = borrower.name+" requested for "+" days"
    }

    override fun getItemCount() = borrowers.size
}