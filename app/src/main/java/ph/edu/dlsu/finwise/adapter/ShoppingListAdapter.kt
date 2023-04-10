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
import ph.edu.dlsu.finwise.financialActivitiesModule.spendingExpenseFragments.SpendingShoppingListFragment
import ph.edu.dlsu.finwise.model.ShoppingListItem

class ShoppingListAdapter : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>{

    private var shoppingListIDArrayList = ArrayList<SpendingShoppingListFragment.ShoppingListAdapterItem>()
    private var context: Context

    private var firestore = Firebase.firestore

    private var savingActivityID:String
    private var shoppingListSetting:ShoppingListSetting
    private var checkShoppingListItem:CheckShoppingList


    constructor(context: Context, shoppingListIDArrayList:ArrayList<SpendingShoppingListFragment.ShoppingListAdapterItem>, savingActivityID:String, shoppingListSetting: ShoppingListSetting, checkShoppingListItem: CheckShoppingList) {
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

        fun bindItem(shoppingListItem: SpendingShoppingListFragment.ShoppingListAdapterItem){
           firestore.collection("ShoppingListItems").document(shoppingListItem.shoppingListItemID).get().addOnSuccessListener {
               var shoppingListItemObjet = it.toObject<ShoppingListItem>()
               itemBinding.tvShoppingListItemId.text = shoppingListItem.shoppingListItemID
               itemBinding.tvShoppingListItemName.text = shoppingListItemObjet?.itemName
               itemBinding.tvSpendingActivityId.text = shoppingListItemObjet?.spendingActivityID
               //item has already been bought, do not allow them to record expense again
               if (shoppingListItemObjet?.status =="Purchased") {
                   itemBinding.cbBought.isChecked = true
                   itemBinding.cbBought.isClickable = false
                   itemBinding.btnSettings.visibility = View.GONE
               }
               else if (shoppingListItemObjet?.status == "In List") {
                   itemBinding.cbBought.isChecked = false
                   itemBinding.btnSettings.visibility = View.VISIBLE
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
                   itemBinding.cbBought.setOnClickListener {
                       checkShoppingListItem.checkShoppingListItem(itemBinding.tvShoppingListItemId.text.toString(), position)
                   }
               }


           }
        }

        override fun onClick(p0: View?) {
            //if item is not yet crossed out, allow them to record expense for the shopping list item
            if (!itemBinding.cbBought.isChecked)
                checkShoppingListItem.checkShoppingListItem(itemBinding.tvShoppingListItemId.text.toString(), position)
        }

    }

    interface ShoppingListSetting{
        fun editShoppingListItem(shoppingListItemID:String)
        fun deleteShoppingListItem(position:Int, shoppingListItemID:String)
    }

    interface CheckShoppingList {
        fun checkShoppingListItem(shoppingListItemID:String, position: Int)
    }
}