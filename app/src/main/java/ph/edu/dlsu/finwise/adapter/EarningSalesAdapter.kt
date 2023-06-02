package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemSellingBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.SellingItems
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.SellingDetailsFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.TransactionFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class EarningSalesAdapter : RecyclerView.Adapter<EarningSalesAdapter.EarningSalesViewHolder>{

    private var salesIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore



    constructor(context: Context, salesIDArrayList:ArrayList<String>) {
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
        val itemBinding = ItemSellingBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return EarningSalesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EarningSalesAdapter.EarningSalesViewHolder,
                                  position: Int) {
        holder.bindItem(salesIDArrayList[position])
    }

    inner class EarningSalesViewHolder(private val itemBinding: ItemSellingBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(saleID: String){
            firestore.collection("SellingItems").document(saleID).get().addOnSuccessListener {
                itemBinding.tvSellingId.text = saleID
                var sale = it.toObject<SellingItems>()!!
                itemBinding.tvItemName.text = sale.itemName
                itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(sale.amount)
                itemBinding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(sale.date!!.toDate())
                itemBinding.tvSource.text = sale.depositTo

                if (sale?.depositTo == "Financial Goal") {
                    firestore.collection("FinancialActivities").document(sale?.savingActivityID!!).get().addOnSuccessListener {
                        var goalID = it.toObject<FinancialActivities>()!!.financialGoalID
                        firestore.collection("FinancialGoals").document(goalID!!).get().addOnSuccessListener {
                            itemBinding.tvGoalName.text = it.toObject<FinancialGoals>()!!.goalName
                        }
                    }
                } else
                    itemBinding.tvGoalName.visibility = View.GONE
            }
        }

        override fun onClick(p0: View?) {
            val bundle = Bundle()
            val sellingID = itemBinding.tvSellingId.text.toString()

            //Show dialog fragment
            val activity = context as FragmentActivity
            val fm: FragmentManager = activity.supportFragmentManager
            val dialogFragment = SellingDetailsFragment()
            bundle.putString("sellingID", sellingID);
            dialogFragment.arguments = bundle
            dialogFragment.show(fm, "fragment_alert")
        }

    }

}