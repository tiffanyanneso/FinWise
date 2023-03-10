package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentGoalActivity
import ph.edu.dlsu.finwise.databinding.ItemChildBinding
import ph.edu.dlsu.finwise.model.Users

class ParentChildrenAdapter: RecyclerView.Adapter<ParentChildrenAdapter.ChildViewHolder> {

    private var childIDArrayList = ArrayList<String>()
    private var context: Context
    private var firestore = Firebase.firestore

    constructor(context: Context, childIDArrayList:ArrayList<String>) {
        this.context = context
        this.childIDArrayList = childIDArrayList
    }
    override fun getItemCount(): Int {
        return childIDArrayList.size
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParentChildrenAdapter.ChildViewHolder {
        val itemBinding = ItemChildBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ChildViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ParentChildrenAdapter.ChildViewHolder,
                                  position: Int) {
        holder.bindTransaction(childIDArrayList[position])
    }

    inner class ChildViewHolder(private val itemBinding: ItemChildBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindTransaction(childID: String) {
            firestore.collection("Users").document(childID).get().addOnSuccessListener {
                var child = it.toObject<Users>()
                itemBinding.tvChildId.text = it.id
                itemBinding.tvUsername.text = child?.username
            }
        }

        override fun onClick(p0: View?) {
            var bundle = Bundle()
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            var parentGoal = Intent(context, ParentGoalActivity::class.java)
            parentGoal.putExtras(bundle)
            context.startActivity(parentGoal)
        }
    }
}