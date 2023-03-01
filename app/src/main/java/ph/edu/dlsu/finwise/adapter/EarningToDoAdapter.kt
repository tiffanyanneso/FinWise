package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.financialActivitiesModule.IncomeActivity
import ph.edu.dlsu.finwise.databinding.ItemEarningBinding

class EarningToDoAdapter : RecyclerView.Adapter<EarningToDoAdapter.EarningToDoViewHolder> {

    private var earningToDoArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, earningToDoArrayList: ArrayList<String>) {
        this.context = context
        this.earningToDoArrayList = earningToDoArrayList
    }

    override fun getItemCount(): Int {
        return earningToDoArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EarningToDoAdapter.EarningToDoViewHolder {
        val itemBinding = ItemEarningBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        return EarningToDoViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EarningToDoAdapter.EarningToDoViewHolder, position: Int) {
        holder.bindItem(earningToDoArrayList[position])
    }

    inner class EarningToDoViewHolder(private val itemBinding: ItemEarningBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(earningID: String) {
            itemBinding.tvEarningActivityId.text = earningID
        }

        override fun onClick(p0: View?) {
            var income = Intent (context, IncomeActivity::class.java)
            var bundle = Bundle()
            bundle.putString("earningActivityID", itemBinding.tvEarningActivityId.text.toString())
            income.putExtras(bundle)
            context.startActivity(income)
        }
    }
}