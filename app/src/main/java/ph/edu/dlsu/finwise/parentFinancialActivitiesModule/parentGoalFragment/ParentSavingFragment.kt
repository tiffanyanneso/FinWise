package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentGoalFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentParentSavingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.NewGoal
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class ParentSavingFragment : Fragment() {

    private lateinit var binding: FragmentParentSavingBinding
    private var firestore = Firebase.firestore
    private lateinit var savingAdapter: FinactSavingAdapter

    var goalIDArrayList = ArrayList<String>()
    var savingsArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private lateinit var childID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        childID = bundle?.getString("childID").toString()
        goalIDArrayList.clear()
        savingsArrayList.clear()
        getSaving()
        //getGoals()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentSavingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = "Overall Saving Performance"
        binding.tvPerformancePercentage.text = "0.00%"
        binding.btnNewGoal.setOnClickListener {
            var sendBundle = Bundle()
            sendBundle.putString("childID", childID)
            var newGoal = Intent(requireContext().applicationContext, NewGoal::class.java)
            newGoal.putExtras(sendBundle)
            startActivity(newGoal)
        }
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){ }

    private fun getSaving() {
        goalIDArrayList.clear()
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (saving in results) {
                var savingActivity = saving.toObject<FinancialActivities>()
                savingsArrayList.add(savingActivity)

                goalIDArrayList.add(savingActivity.financialGoalID.toString())
                loadRecyclerView(goalIDArrayList)
            }
        }.continueWith { getTotalSavings() }
    }


    private fun getTotalSavings() {
        var savedAmount = 0.00F
        binding.tvGoalSavings.text = "₱ " + DecimalFormat("#,##0.00").format(savedAmount)

        firestore.collection("Transactions").whereEqualTo("createdBy", childID).whereIn("transactionType", Arrays.asList("Deposit", "Withdrawal")).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                if (transactionObject?.transactionType == "Deposit")
                    savedAmount += transactionObject?.amount!!
                else if (transactionObject.transactionType == "Withdrawal")
                    savedAmount -= transactionObject?.amount!!
            }
        }.continueWith {
            binding.tvGoalSavings.text = "₱ " + DecimalFormat("#,##0.00").format(savedAmount)
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        savingAdapter = FinactSavingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = savingAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        savingAdapter.notifyDataSetChanged()
    }
}