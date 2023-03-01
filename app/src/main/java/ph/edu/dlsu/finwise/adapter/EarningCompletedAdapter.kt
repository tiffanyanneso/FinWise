package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemEarningCompletedBinding
import ph.edu.dlsu.finwise.databinding.ItemShoppingListBinding
import ph.edu.dlsu.finwise.model.ShoppingList

class EarningCompletedAdapter : RecyclerView.Adapter<EarningCompletedAdapter.EarningCompletedViewHolder>{

    private var earningCompletedArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, earningCompletedArrayList:ArrayList<String>) {
        this.context = context
        this.earningCompletedArrayList = earningCompletedArrayList
    }

    override fun getItemCount(): Int {
        return earningCompletedArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EarningCompletedAdapter.EarningCompletedViewHolder {
        val itemBinding = ItemEarningCompletedBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return EarningCompletedViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EarningCompletedAdapter.EarningCompletedViewHolder,
                                  position: Int) {
        holder.bindItem(earningCompletedArrayList[position])
    }

    inner class EarningCompletedViewHolder(private val itemBinding: ItemEarningCompletedBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(earningID: String){

        }

        override fun onClick(p0: View?) {

        }
    }
}