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
import ph.edu.dlsu.finwise.databinding.ItemAddFriendBinding
import ph.edu.dlsu.finwise.databinding.ItemShoppingListBinding
import ph.edu.dlsu.finwise.model.ChildUser
import ph.edu.dlsu.finwise.model.ShoppingList

class ShoppingListAdapter : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>{

    private var shoppingListIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore

    private var shoppingListSetting:ShoppingListSetting


    constructor(context: Context, shoppingListIDArrayList:ArrayList<String>, shoppingListSetting: ShoppingListSetting) {
        this.context = context
        this.shoppingListIDArrayList = shoppingListIDArrayList
        this.shoppingListSetting = shoppingListSetting
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
               itemBinding.tvShoppingListItemId.text = shoppingListItemID
               itemBinding.tvShoppingListItemName.text = shoppingListItem?.itemName
               if (shoppingListItem?.status =="Purchased")
                    itemBinding.cbBought.isChecked = true
               else if (shoppingListItem?.status == "In List")
                   itemBinding.cbBought.isChecked = false
               itemBinding.btnSettings.setOnClickListener {
                   val popup = PopupMenu(context, it)
                   popup.inflate(R.menu.menu_shopping_list)
                   popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item:MenuItem? ->

                       when (item!!.itemId) {
                           R.id.tv_edit_shopping_item -> {
                               popup.dismiss()
                               shoppingListSetting.editShoppingListItem(itemBinding.tvShoppingListItemId.text.toString())
                           }
                           R.id.tv_delete_shopping_item -> {
                               popup.dismiss()
                               shoppingListSetting.deleteShoppingListItem(position, itemBinding.tvShoppingListItemId.text.toString())
                           }
                       }
                       true
                   })
                   popup.show()
               }
           }
        }

        override fun onClick(p0: View?) {
            //if item is not yet crossed out, allow them to record expense for the shopping list item
            if (!itemBinding.cbBought.isChecked) {

            }
        }
    }

    interface ShoppingListSetting{
        fun editShoppingListItem(shoppingListItemID:String)
        fun deleteShoppingListItem(position:Int, shoppingListItemID:String)
    }
}