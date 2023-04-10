package ph.edu.dlsu.finwise.financialActivitiesModule.goalDetailsFragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.DialogDeleteGoalWarningBinding
import ph.edu.dlsu.finwise.databinding.FragmentGoalDetailsBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class GoalDetailsFragment : Fragment() {

    private lateinit var binding: FragmentGoalDetailsBinding
    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var financialGoalID:String
    private lateinit var savingActivityID:String

    private var cashBalance = 0.00F
    private var mayaBalance = 0.00F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            financialGoalID = arguments?.getString("financialGoalID").toString()
            savingActivityID = arguments?.getString("savingActivityID").toString()
            cashBalance = arguments?.getFloat("cashBalance")!!
            mayaBalance = arguments?.getFloat("mayaBalance")!!
        }
        getGoalDetails()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUser()
        binding.btnDelete.setOnClickListener {
            var dialogBinding= DialogDeleteGoalWarningBinding.inflate(getLayoutInflater())
            var dialog= Dialog(requireContext());
            dialog.setContentView(dialogBinding.getRoot())

            dialog.window!!.setLayout(800, 1000)

            dialogBinding.btnOk.setOnClickListener {
                deleteGoal()
                dialog.dismiss()
            }

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun deleteGoal() {
        //mark financial goal as deleted
        firestore.collection("FinancialGoals").document(financialGoalID).update("status", "Deleted", "currentSavings", 0).addOnSuccessListener {
            //mark related activities as deleted
            firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener { results ->
                for (activity in results )
                    firestore.collection("FinancialActivities").document(activity.id).update("status", "Deleted")

                returnSavings()
                var goalList = Intent(requireContext().applicationContext, FinancialActivity::class.java)
                startActivity(goalList)
            }
        }
    }

    private fun returnSavings() {
        if (cashBalance > 0.00F) {
            var cashWithdrawal = hashMapOf(
                "transactionName" to binding.tvGoalName.text.toString() + " Withdrawal",
                "transactionType" to "Withdrawal",
                "category" to "Goal",
                "date" to Timestamp.now(),
                "userID" to currentUser,
                "amount" to cashBalance,
                "financialActivityID" to savingActivityID,
                "paymentType" to "Cash"
            )
            firestore.collection("Transactions").add(cashWithdrawal)
            firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).whereEqualTo("type", "Cash").get().addOnSuccessListener {
                var walletID = it.documents[0].id
                firestore.collection("ChildWallet").document(walletID).get().addOnSuccessListener {
                    var updatedBalance = it.toObject<ChildWallet>()?.currentBalance!! + cashBalance
                    firestore.collection("ChildWallet").document(walletID).update("currentBalance", updatedBalance)
                }
            }
        }
        if (mayaBalance > 0.00F) {
            var mayaWithdrawal = hashMapOf(
                "transactionName" to binding.tvGoalName.text.toString() + " Withdrawal",
                "transactionType" to "Withdrawal",
                "category" to "Goal",
                "date" to Timestamp.now(),
                "userID" to currentUser,
                "amount" to mayaBalance,
                "financialActivityID" to savingActivityID,
                "paymentType" to "Maya"
            )
            firestore.collection("Transactions").add(mayaWithdrawal)
            firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).whereEqualTo("type", "Maya").get().addOnSuccessListener {
                var walletID = it.documents[0].id
                firestore.collection("ChildWallet").document(walletID).get().addOnSuccessListener {
                    var updatedBalance = it.toObject<ChildWallet>()?.currentBalance!! + mayaBalance
                    firestore.collection("ChildWallet").document(walletID).update("currentBalance", updatedBalance)
                }
            }
        }
    }


    private fun getGoalDetails() {

        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var goal = it.toObject<FinancialGoals>()
            binding.tvGoalAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount!!)
            binding.tvGoalName.text = goal.goalName
            binding.tvActivity.text = goal.financialActivity.toString()

            //convert timestamp to string date
            binding.tvDateSet.text = SimpleDateFormat("MM/dd/yyyy").format(goal.dateCreated?.toDate()).toString()
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(goal.targetDate?.toDate()).toString()
            binding.tvStatus.text = goal?.status.toString()
            if (goal?.goalIsForSelf == true)
                binding.tvIsForChild.text = "Yes"
            else
                binding.tvIsForChild.text = "No"

        }
    }

    private fun checkUser() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            if (it.toObject<Users>()!!.userType == "Parent")
                binding.btnDelete.visibility = View.GONE
            else if (it.toObject<Users>()!!.userType == "Child")
                binding.btnDelete.visibility = View.VISIBLE
        }
    }
}