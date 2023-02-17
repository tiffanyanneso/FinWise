package ph.edu.dlsu.finwise.financialActivitiesModule.childGoalFragment

import androidx.fragment.app.Fragment

class GoalSettingFragment : Fragment() {

    /*private lateinit var binding: FragmentForReviewBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: ChildGoalAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getForReviewGoals()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForReviewBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }


    private fun getForReviewGoals() {
        var goalIDArrayList = ArrayList<String>()
        var filter = "For Review"
        var goalFilterArrayList = ArrayList<GoalFilter>()

        //TODO:change to get transactions of current user
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        //firestore.collection("Transactions").whereEqualTo("companyID", currentUser).get().addOnSuccessListener{ documents ->

        firestore.collection("FinancialGoals").whereEqualTo("status", filter).get().addOnSuccessListener { documents ->
            for (goalSnapshot in documents) {
                //creating the object from list retrieved in db
                var goalID = goalSnapshot.id
                var goal = goalSnapshot.toObject<FinancialGoals>()
                //goalIDArrayList.add(goalID)
                goalFilterArrayList.add(GoalFilter(goalID, goal?.targetDate!!.toDate()))
            }
            goalFilterArrayList.sortBy { it.goalTargetDate }
            for (goalFilter in goalFilterArrayList)
                goalIDArrayList.add(goalFilter.financialGoalID!!)
            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalAdapter = ChildGoalAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = goalAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        goalAdapter.notifyDataSetChanged()
    }*/
}