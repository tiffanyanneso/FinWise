package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ItemShoppingListBinding
import ph.edu.dlsu.finwise.databinding.ItemTransactionBinding
import ph.edu.dlsu.finwise.model.SellingItems
import ph.edu.dlsu.finwise.model.ShoppingListItem
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class EarningSalesAdapter : RecyclerView.Adapter<EarningSalesAdapter.EarningSalesViewHolder>{

    private var salesIDArrayList = ArrayList<SellingItems>()
    private var context: Context

    private var firestore = Firebase.firestore



    constructor(context: Context, salesIDArrayList:ArrayList<SellingItems>) {
        this.context = context
        this.salesIDArrayList = salesIDArrayList
    }

    override fun getItemCount(): Int {
        return salesIDArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EarningSalesAdapter.EarningSalesViewHolder {
        val itemBinding = ItemTransactionBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return EarningSalesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EarningSalesAdapter.EarningSalesViewHolder,
                                  position: Int) {
        holder.bindItem(salesIDArrayList[position])
    }

    inner class EarningSalesViewHolder(private val itemBinding: ItemTransactionBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(sale: SellingItems){
            itemBinding.tvName.text = sale.itemName
            itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(sale.amount)
            itemBinding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(sale.date!!.toDate())
        }

        override fun onClick(p0: View?) {
        }

    }

}