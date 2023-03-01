package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemAddFriendBinding
import ph.edu.dlsu.finwise.databinding.ItemShoppingListBinding
import ph.edu.dlsu.finwise.model.ChildUser
import ph.edu.dlsu.finwise.model.ShoppingList

class ShoppingListAdapter : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>{

    private var shoppingListIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, shoppingListIDArrayList:ArrayList<String>) {
        this.context = context
        this.shoppingListIDArrayList = shoppingListIDArrayList
    }

    override fun getItemCount(): Int {
        return shoppingListIDArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingListAdapter.ShoppingListViewHolder {
        val itemBinding = ItemShoppingListBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ShoppingListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ShoppingListAdapter.ShoppingListViewHolder,
                                  position: Int) {
        holder.bindItem(shoppingListIDArrayList[position])
    }

    inner class ShoppingListViewHolder(private val itemBinding: ItemShoppingListBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(shoppingListItemID: String){
           firestore.collection("ShoppingListItems").document(shoppingListItemID).get().addOnSuccessListener {
               var shoppingListItem = it.toObject<ShoppingList>()
               itemBinding.tvShoppingListItemName.text = shoppingListItem?.itemName
           }
        }

        override fun onClick(p0: View?) {

        }
    }
}