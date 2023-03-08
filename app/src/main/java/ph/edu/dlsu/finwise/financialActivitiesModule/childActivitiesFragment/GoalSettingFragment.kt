package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.app.Dialog
import android.content.Intent
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
import ph.edu.dlsu.finwise.GoalSettingPerformanceActivity
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactGoalSettingAdapter
import ph.edu.dlsu.finwise.databinding.DialogNewGoalWarningBinding
import ph.edu.dlsu.finwise.databinding.FragmentGoalSettingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.ChildNewGoal
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

    private var ongoingGoals = 0

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
        binding.title.text = "Overall Goal Setting Performance"
        getForReviewGoals()
        getOnGoingGoals()
        initializeRating()
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

        binding.btnSeeMore.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, GoalSettingPerformanceActivity::class.java)
            this.startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, GoalSettingPerformanceActivity::class.java)
            this.startActivity(goToPerformance)
        }
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }


    private fun getForReviewGoals() {
        var goalIDArrayList = ArrayList<String>()
        var goalFilterArrayList = ArrayList<GoalFilter>()
        goalIDArrayList.clear()
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereIn("status", listOf("For Review", "For Editing")).get().addOnSuccessListener { results ->
            for (goalForReview in results) {
                goalIDArrayList.add(goalForReview.id)
            }
            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun getOnGoingGoals() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
           ongoingGoals = results.size()
        }
    }

    private fun showSeeMoreButton() {
        binding.btnSeeMore.visibility = View.VISIBLE
        binding.layoutButtons.visibility = View.GONE
    }

    private fun showReviewButton() {
        binding.btnSeeMore.visibility = View.GONE
        binding.layoutButtons.visibility = View.VISIBLE
    }

    private fun initializeRating() {
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

            var percentage = (overall / 5) * 100

            if (percentage >= 90) {
                binding.imgFace.setImageResource(R.drawable.excellent)
                binding.tvPerformanceStatus.text = "Excellent"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvPerformanceText.text = "Keep up the excellent work! Goal Setting is your strong point. Keep setting those goals!"
                showSeeMoreButton()
            } else if (percentage < 90 && percentage >= 80) {
                binding.imgFace.setImageResource(R.drawable.great)
                binding.tvPerformanceStatus.text = "Great"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Great job! You are performing well. Keep setting those goals!"
                showSeeMoreButton()
            } else if (percentage < 80 && percentage >= 70) {
                binding.imgFace.setImageResource(R.drawable.good)
                binding.tvPerformanceStatus.text = "Good"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvPerformanceText.text = "Good job! With a bit more dedication and effort, you’ll surely up your performance!"
                showSeeMoreButton()
            } else if (percentage < 70 && percentage >= 60) {
                binding.imgFace.setImageResource(R.drawable.average)
                binding.tvPerformanceStatus.text = "Average"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvPerformanceText.text = "Nice work! Work on improving your goal setting performance. Review SMART Goals!"
                showReviewButton()
            } else if (percentage < 60) {
                binding.imgFace.setImageResource(R.drawable.bad)
                binding.tvPerformanceStatus.text = "Bad"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Uh oh! You need to work on your goal setting.  Click review to learn how!"
                showReviewButton()
            }
//            binding.ratingBarOverall.rating = overall
//            binding.progressBarSpecific.progress = (nSpecific/nRatings).roundToInt()
//            binding.ratingSpecific.text = "${(nSpecific/nRatings)}/5"
//            binding.progressBarMeasurable.progress = (nMeasurable/nRatings).roundToInt()
//            binding.ratingMeasurable.text = "${(nMeasurable/nRatings)}/5"
//            binding.progressBarAchievable.progress = (nAchievable/nRatings).roundToInt()
//            binding.ratingAchievable.text = "${(nAchievable/nRatings)}/5"
//            binding.progressBarRelevant.progress = (nRelevant/nRatings).roundToInt()
//            binding.ratingRelevant.text = "${(nRelevant/nRatings)}/5"
//            binding.progressBarTime.progress = (nTimeBound/nRatings).roundToInt()
//            binding.ratingTime.text = "${(nTimeBound/nRatings)}/5"
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
}