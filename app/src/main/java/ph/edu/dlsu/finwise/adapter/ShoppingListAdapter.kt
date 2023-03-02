package ph.edu.dlsu.finwise.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import ph.edu.dlsu.finwise.databinding.DialogNewShoppingListItemBinding
import ph.edu.dlsu.finwise.databinding.DialogShoppingListRecordExpenseBinding
import ph.edu.dlsu.finwise.databinding.ItemAddFriendBinding
import ph.edu.dlsu.finwise.databinding.ItemShoppingListBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivityRecordExpense
import ph.edu.dlsu.finwise.model.ChildUser
import ph.edu.dlsu.finwise.model.ShoppingList

class ShoppingListAdapter : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>{

    private var shoppingListIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore

    private var savingActivityID:String
    private var shoppingListSetting:ShoppingListSetting
    private var checkShoppingListItem:CheckShoppingList


    constructor(context: Context, shoppingListIDArrayList:ArrayList<String>, savingActivityID:String, shoppingListSetting: ShoppingListSetting, checkShoppingListItem: CheckShoppingList) {
        this.context = context
        this.shoppingListIDArrayList = shoppingListIDArrayList
        this.savingActivityID = savingActivityID
        this.shoppingListSetting = shoppingListSetting
        this.checkShoppingListItem = checkShoppingListItem
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
               itemBinding.tvSpendingActivityId.text = shoppingListItem?.spendingActivityID
               //item has already been bought, do not allow them to record expense again
               if (shoppingListItem?.status =="Purchased") {
                   itemBinding.cbBought.isChecked = true
                   itemBinding.cbBought.isClickable = false
               }
               else if (shoppingListItem?.status == "In List") {
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
                   itemBinding.cbBought.setOnCheckedChangeListener { compoundButton, b ->
                       checkShoppingListItem.checkShoppingListItem(itemBinding.tvShoppingListItemId.text.toString())
                   }
               }


           }
        }

        override fun onClick(p0: View?) {
            //if item is not yet crossed out, allow them to record expense for the shopping list item
            if (!itemBinding.cbBought.isChecked)
                checkShoppingListItem.checkShoppingListItem(itemBinding.tvShoppingListItemId.text.toString())
        }

    }

    interface ShoppingListSetting{
        fun editShoppingListItem(shoppingListItemID:String)
        fun deleteShoppingListItem(position:Int, shoppingListItemID:String)
    }

    interface CheckShoppingList {
        fun checkShoppingListItem(shoppingListItemID:String)
    }
}