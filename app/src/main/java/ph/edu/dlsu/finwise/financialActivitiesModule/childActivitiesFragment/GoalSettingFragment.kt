package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.FinactGoalSettingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentGoalSettingBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.GoalRating
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class GoalSettingFragment : Fragment() {

    private lateinit var binding: FragmentGoalSettingBinding
    private var firestore = Firebase.firestore
    private lateinit var goalSettingAdapter: FinactGoalSettingAdapter

    private var nRatings = 0
    private var nOverall = 0.00F
    private var nSpecific = 0.00F
    private var nMeasurable = 0.00F
    private var nAchievable = 0.00F
    private var nRelevant = 0.00F
    private var nTimeBound = 0.00F

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nRatings = 0
        nOverall = 0.00F
        nSpecific = 0.00F
        nMeasurable = 0.00F
        nAchievable = 0.00F
        nRelevant = 0.00F
        nTimeBound = 0.00F
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalSettingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getForReviewGoals()
        initializeStars()
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }


    private fun getForReviewGoals() {
        var goalIDArrayList = ArrayList<String>()
        var goalFilterArrayList = ArrayList<GoalFilter>()
        goalIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "For Review").get().addOnSuccessListener { results ->
            for (goalForReview in results) {
                var goalObject = goalForReview.toObject<FinancialActivities>()
                goalIDArrayList.add(goalObject?.financialGoalID.toString())
            }
            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun initializeStars() {
        firestore.collection("GoalRating").whereEqualTo("childID", currentUser).get().addOnSuccessListener { results ->
           nRatings = results.size()
            for (rating in results) {
                var ratingObject = rating.toObject<GoalRating>()
                nOverall += ratingObject.overallRating!!
                nSpecific += ratingObject.specific!!
                nMeasurable += ratingObject.measurable!!
                nAchievable +=ratingObject.achievable!!
                nRelevant +=ratingObject.relevant!!
                nTimeBound += ratingObject.timeBound!!
            }
        }.continueWith {
            var overall = nOverall/nRatings
            binding.tvOverallRating.text ="${overall}/5"
            binding.ratingBarOverall.rating = overall
            binding.progressBarSpecific.progress = (nSpecific/5).roundToInt()
            binding.ratingSpecific.text = "${(nSpecific/5)}/5"
            binding.progressBarMeasurable.progress = (nMeasurable/5).roundToInt()
            binding.ratingMeasurable.text = "${(nMeasurable/5)}/5"
            binding.progressBarAchievable.progress = (nAchievable/5).roundToInt()
            binding.ratingAchievable.text = "${(nAchievable/5)}/5"
            binding.progressBarRelevant.progress = (nRelevant/5).roundToInt()
            binding.ratingRelevant.text = "${(nRelevant/5)}/5"
            binding.progressBarTime.progress = (nTimeBound/5).roundToInt()
            binding.ratingTime.text = "${(nTimeBound/5)}/5"
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalSettingAdapter = FinactGoalSettingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = goalSettingAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        goalSettingAdapter.notifyDataSetChanged()
    }
}