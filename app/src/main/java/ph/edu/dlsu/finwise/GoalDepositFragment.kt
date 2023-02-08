package ph.edu.dlsu.finwise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.FragmentGoalDepositBinding
import ph.edu.dlsu.finwise.databinding.FragmentInProgressBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childGoalFragment.InProgressFragment
import ph.edu.dlsu.finwise.model.FinancialGoals

class GoalDepositFragment : Fragment() {

    private lateinit var binding: FragmentGoalDepositBinding
    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            getGoalDeposits()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalDepositBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun getGoalDeposits() {
//        var goalDepositIDArrayList = ArrayList<String>()
//        var filter = "In Progress"
//        var goalFilterArrayList = ArrayList<InProgressFragment.GoalFilter>()
//
//        //TODO:change to get transactions of current user
//        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
//        //firestore.collection("Transactions").whereEqualTo("companyID", currentUser).get().addOnSuccessListener{ documents ->
//
//        firestore.collection("FinancialGoals").whereEqualTo("status", filter).get().addOnSuccessListener { documents ->
//            for (goalSnapshot in documents) {
//                //creating the object from list retrieved in db
//                var goalID = goalSnapshot.id
//                var goal = goalSnapshot.toObject<FinancialGoals>()
//                //goalIDArrayList.add(goalID)
//                goalFilterArrayList.add(
//                    InProgressFragment.GoalFilter(
//                        goalID,
//                        goal?.targetDate!!.toDate()
//                    )
//                )
//            }
//            goalFilterArrayList.sortBy { it.goalTargetDate }
//            for (goalFilter in goalFilterArrayList)
//                goalIDArrayList.add(goalFilter.financialGoalID!!)
//            loadRecyclerView(goalIDArrayList)
//        }
    }
}