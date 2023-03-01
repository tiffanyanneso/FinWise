package ph.edu.dlsu.finwise.financialActivitiesModule.budgetExpenseFragments
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.ShoppingListAdapter
import ph.edu.dlsu.finwise.databinding.DialogNewShoppingListItemBinding
import ph.edu.dlsu.finwise.databinding.FragmentBudgetShoppingListBinding
import ph.edu.dlsu.finwise.model.ShoppingList
import java.util.*
import kotlin.collections.ArrayList


class BudgetShoppingListFragment : Fragment() {

    private lateinit var binding:FragmentBudgetShoppingListBinding
    private lateinit var budgetActivityID:String

    private lateinit var budgetItemID:String
    private lateinit var spendingActivityID:String

    private var firestore = Firebase.firestore

    private lateinit var shoppingListAdapter: ShoppingListAdapter

    private var shoppingListArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var bundle = arguments
            budgetActivityID = bundle?.getString("budgetActivityID").toString()
            budgetItemID = bundle?.getString("budgetItemID").toString()
            spendingActivityID = bundle?.getString("spendingActivityID").toString()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getShoppingListItems()

        binding.btnAddShoppingListItem.setOnClickListener {
            addShoppingListItemDialog()
        }
    }

    private fun getShoppingListItems() {
        shoppingListArrayList.clear()
        firestore.collection("ShoppingListItems").whereEqualTo("spendingActivityID", spendingActivityID).whereIn("status", Arrays.asList("In List", "Purchased")).get().addOnSuccessListener { results ->
            for (item in results)
                shoppingListArrayList.add(item.id)
        }.continueWith {
            println("print " +shoppingListArrayList.size)
            shoppingListAdapter = ShoppingListAdapter(requireActivity().applicationContext, shoppingListArrayList, object:ShoppingListAdapter.ShoppingListSetting{
                override fun editShoppingListItem(shoppingListItemID: String) {
                    funEditShoppingListItem(shoppingListItemID)
                }

                override fun deleteShoppingListItem(position: Int, shoppingListItemID: String) {
                    funDeleteShoppingListItem(position, shoppingListItemID)
                }

            })
            binding.rvViewItems.adapter = shoppingListAdapter
            binding.rvViewItems.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            shoppingListAdapter.notifyDataSetChanged()
        }
    }

    private fun addShoppingListItemDialog() {
        var dialogBinding= DialogNewShoppingListItemBinding.inflate(layoutInflater)
        var dialog= Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)
        dialog.window!!.setLayout(850, 900)

        dialogBinding.btnSave.setOnClickListener {
            var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
            var shoppingListItem = ShoppingList(dialogBinding.etShoppingListItemName.text.toString(), budgetItemID, currentUser, "In List", spendingActivityID)
            firestore.collection("ShoppingListItems").add(shoppingListItem).addOnSuccessListener { newItem ->
                dialog.dismiss()
                shoppingListArrayList.add(newItem.id)
                shoppingListAdapter.notifyDataSetChanged()
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun funEditShoppingListItem(shoppingListItemID:String) {
        var dialogBinding= DialogNewShoppingListItemBinding.inflate(layoutInflater)
        var dialog= Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)
        dialog.window!!.setLayout(850, 900)

        firestore.collection("ShoppingListItems").document(shoppingListItemID).get().addOnSuccessListener {
            var shoppingListItem = it.toObject<ShoppingList>()
            dialogBinding.etShoppingListItemName.setText(shoppingListItem?.itemName)
        }

        dialogBinding.btnSave.setOnClickListener {
            firestore.collection("ShoppingListItems").document(shoppingListItemID).update("itemName", dialogBinding.etShoppingListItemName.text.toString())
            shoppingListAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun funDeleteShoppingListItem(position:Int, shoppingListItemID: String) {
        firestore.collection("ShoppingListItems").document(shoppingListItemID).update("status", "Deleted")
        shoppingListArrayList.removeAt(position)
        shoppingListAdapter.notifyDataSetChanged()
    }
}