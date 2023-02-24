package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

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
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.DialogNewGoalWarningBinding
import ph.edu.dlsu.finwise.databinding.FragmentSavingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.ChildNewGoal
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.GoalSettings
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class SavingFragment : Fragment() {

    private lateinit var binding: FragmentSavingBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: FinactSavingAdapter

    var goalIDArrayList = ArrayList<String>()
    var savingsArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var ongoingGoals = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalIDArrayList.clear()
        savingsArrayList.clear()
        setGoalCount()
        getSavingActivities()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavingBinding.inflate(inflater, container, false)
        binding.btnNewGoal.setOnClickListener {
            if (ongoingGoals >= 5)
                buildDialog()
            else {
                var goToNewGoal = Intent(requireContext().applicationContext, ChildNewGoal::class.java)

                var bundle = Bundle()
                bundle.putString("source", "childFinancialActivity")
                goToNewGoal.putExtras(bundle)
                goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                this.startActivity(goToNewGoal)
            }

        }
        return binding.root
    }

    private fun getTotalSavings() {
        var savedAmount = 0.00F
        for (goalID in goalIDArrayList) {
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener {
                var goal = it.toObject<FinancialGoals>()
                savedAmount += goal?.currentSavings!!
            }.continueWith { binding.tvGoalSavings.text = "₱ " + DecimalFormat("#,##0.00").format(savedAmount) }
        }
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){ }

    private fun getSavingActivities() {
        goalIDArrayList.clear()
        ///TODO: CHANGE TO FIREBASEAUTH.CURRENTUSER
        var currentUser = "eWZNOIb9qEf8kVNdvdRzKt4AYrA2"
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (activity in results) {
                var activityObject = activity.toObject<FinancialActivities>()
                goalIDArrayList.add(activityObject?.financialGoalID.toString())
            }
            binding.tvInProgress.text = goalIDArrayList.size.toString()
            binding.tvNearingCompletion.text = "X"
            binding.tvNearingDeadline.text = "X"
            loadRecyclerView(goalIDArrayList)
        }.continueWith { getTotalSavings() }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalAdapter = FinactSavingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = goalAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        goalAdapter.notifyDataSetChanged()
    }

    private fun setGoalCount() {
        var currentUser = "eWZNOIb9qEf8kVNdvdRzKt4AYrA2"
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).get().addOnSuccessListener { results ->
            for (goalSnapshot in results) {
                var goal = goalSnapshot.toObject<FinancialGoals>()
                if (goal?.status == "In Progress")
                    ongoingGoals++
            }
        }
    }


    private fun buildDialog() {

        var dialogBinding= DialogNewGoalWarningBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())
        // Initialize dialog

        dialog.window!!.setLayout(900, 600)

        dialogBinding.tvMessage.text= "You have $ongoingGoals ongoing goals.\nAre you sure you want to start another one?"

        dialogBinding.btnOk.setOnClickListener {
            var newGoal = Intent (requireContext().applicationContext, ChildNewGoal::class.java)

            var bundle = Bundle()
            bundle.putString("source", "childFinancialActivity")
            newGoal.putExtras(bundle)
            newGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            this.startActivity(newGoal)
            dialog.dismiss()
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun checkSettings() {
        var currentChildUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("GoalSettings").whereEqualTo("childID", currentChildUser).get().addOnSuccessListener {
            var goalSettings = it.documents[0].toObject<GoalSettings>()
            //hide button
            if (goalSettings?.setOwnGoal == false)
                binding.btnNewGoal.visibility = View.GONE
            else if (goalSettings?.setOwnGoal == true)
                binding.btnNewGoal.visibility = View.VISIBLE
        }
    }
}