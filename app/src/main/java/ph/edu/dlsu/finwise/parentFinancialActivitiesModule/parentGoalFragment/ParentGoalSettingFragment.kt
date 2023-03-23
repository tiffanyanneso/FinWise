package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentGoalFragment

import android.annotation.SuppressLint
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
import ph.edu.dlsu.finwise.GoalSettingPerformanceActivity
import ph.edu.dlsu.finwise.ParentGoalSettingPerformanceActivity
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactGoalSettingAdapter
import ph.edu.dlsu.finwise.databinding.*
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.NewGoal
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.SavingFragment
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.GoalRating
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentGoalActivity
import java.text.DecimalFormat
import java.util.*

class ParentGoalSettingFragment : Fragment() {

    private lateinit var binding: FragmentParentGoalSettingBinding
    private var firestore = Firebase.firestore
    private lateinit var goalSettingAdapter: FinactGoalSettingAdapter

    private var nRatings = 0
    private var nOverall = 0.00F
    private var nSpecific = 0.00F
    private var nMeasurable = 0.00F
    private var nAchievable = 0.00F
    private var nRelevant = 0.00F
    private var nTimeBound = 0.00F

    private lateinit var childID:String

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
        var bundle = requireArguments()
        childID = bundle.getString("childID").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentGoalSettingBinding.inflate(inflater, container, false)
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
                var goToNewGoal = Intent(requireContext().applicationContext, NewGoal::class.java)
                var bundle = Bundle()
                bundle.putString("childID", childID)
                bundle.putString("source", "Parent")
                goToNewGoal.putExtras(bundle)
                goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(goToNewGoal)
            }
        }

        binding.btnViewSMARTGoalsInfo.setOnClickListener{
            showGoalDialog()
        }

        binding.btnSeeMore.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, ParentGoalSettingPerformanceActivity::class.java)

            var bundle = Bundle()

            bundle.putString("childID", childID)
            goToPerformance.putExtras(bundle)
            goToPerformance.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            this.startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, ParentGoalSettingPerformanceActivity::class.java)

            var bundle = Bundle()

            bundle.putString("childID", childID)
            goToPerformance.putExtras(bundle)
            goToPerformance.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            this.startActivity(goToPerformance)
        }
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }


    private fun getForReviewGoals() {
        var goalIDArrayList = ArrayList<String>()
        var goalFilterArrayList = ArrayList<GoalFilter>()
        goalIDArrayList.clear()
        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereIn("status", listOf("For Review", "For Editing")).get().addOnSuccessListener { results ->
            for (goalForReview in results) {
                var goalObject = goalForReview.toObject<FinancialGoals>()
                goalFilterArrayList.add(GoalFilter(goalForReview.id, goalObject.targetDate!!.toDate()))
            }
            goalFilterArrayList.sortBy { it.goalTargetDate }

            for (goal in goalFilterArrayList)
                goalIDArrayList.add(goal.financialGoalID.toString())

            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun getOnGoingGoals() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            ongoingGoals = results.size()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initializeRating() {
        firestore.collection("GoalRating").whereEqualTo("childID", childID).get().addOnSuccessListener { results ->
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
            var overall = 0.00F
            binding.tvOverallRating.visibility = View.GONE
            if (nRatings!=0) {
                overall = nOverall / nRatings
                var percentage = (overall / 5) * 100

                if (percentage >= 96) {
                    binding.imgFace.setImageResource(R.drawable.excellent)
                    binding.tvPerformanceStatus.text = "Excellent"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                    binding.tvPerformanceText.text = "Your child excels at goal setting! Encourage them to continue."
                    showSeeMoreButton()
                } else if (percentage < 96 && percentage >= 86) {
                    binding.imgFace.setImageResource(R.drawable.amazing)
                    binding.tvPerformanceStatus.text = "Amazing"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Your child is amazing at goal setting! Encourage them to keep setting those goals!"
                    showSeeMoreButton()
                } else if (percentage < 86 && percentage >= 76) {
                    binding.imgFace.setImageResource(R.drawable.great)
                    binding.tvPerformanceStatus.text = "Great"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Your child is doing a great job of setting goals!"
                    showSeeMoreButton()
                } else if (percentage < 76 && percentage >= 66) {
                    binding.imgFace.setImageResource(R.drawable.good)
                    binding.tvPerformanceStatus.text = "Good"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                    binding.tvPerformanceText.text = "Your child is doing a great job of setting goals! Encourage them to follow the SMART framework."
                    showSeeMoreButton()
                } else if (percentage < 66 && percentage >= 56) {
                    binding.imgFace.setImageResource(R.drawable.average)
                    binding.tvPerformanceStatus.text = "Average"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                    binding.tvPerformanceText.text = "Your child is doing a nice job of setting goals! Encourage them to follow the SMART framework."
                    showReviewButton()
                } else if (percentage < 56 && percentage >= 46) {
                    binding.imgFace.setImageResource(R.drawable.nearly_there)
                    binding.tvPerformanceStatus.text = "Nearly There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to help them get there!"
                    showReviewButton()
                } else if (percentage < 46 && percentage >= 36) {
                    binding.imgFace.setImageResource(R.drawable.almost_there)
                    binding.tvPerformanceStatus.text = "Almost There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is almost there! Click the tips button to learn how to help them get there!"
                    showReviewButton()
                } else if (percentage < 36 && percentage >= 26) {
                    binding.imgFace.setImageResource(R.drawable.getting_there)
                    binding.tvPerformanceStatus.text = "Getting There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
                    showReviewButton()
                } else if (percentage < 26 && percentage >= 16) {
                    binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                    binding.tvPerformanceStatus.text = "Not Quite\nThere"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
                    showReviewButton()
                } else if (percentage < 15) {
                    binding.imgFace.setImageResource(R.drawable.bad)
                    binding.tvPerformanceStatus.text = "Needs\nImprovement"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Uh oh! Click the tips button to learn how to help them improve their goal setting!"
                    showReviewButton()
                } else {
                    binding.imgFace.setImageResource(R.drawable.good)
                    binding.tvPerformanceStatus.text = ""
                    binding.tvPerformanceText.text = "Child has yet to complete goals."
                    showSeeMoreButton()
                }
                binding.tvOverallRating.visibility = View.VISIBLE
                binding.tvOverallRating.text ="${DecimalFormat("0.0").format(overall)}/5.0"
            }
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

    private fun showSeeMoreButton() {
        binding.btnSeeMore.visibility = View.VISIBLE
        binding.layoutButtons.visibility = View.GONE
    }

    private fun showReviewButton() {
        binding.btnSeeMore.visibility = View.GONE
        binding.layoutButtons.visibility = View.VISIBLE
    }


    private fun buildDialog() {

        var dialogBinding= DialogNewGoalWarningBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())
        // Initialize dialog

        dialog.window!!.setLayout(900, 600)

        dialogBinding.tvMessage.text= "Your child has $ongoingGoals ongoing goals.\nAre you sure you want to start another one?"

        dialogBinding.btnOk.setOnClickListener {
            var newGoal = Intent (requireContext().applicationContext, NewGoal::class.java)

            var bundle = Bundle()
            bundle.putString("source", "Parent")
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

    private fun showGoalDialog() {

        var dialogBinding= DialogParentSmartTipBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnReviewGoals.setOnClickListener {
            dialog.dismiss()
            var goToGoalSetting = Intent(requireContext().applicationContext, ParentGoalActivity::class.java)
            this.startActivity(goToGoalSetting)
        }

        dialog.show()
    }
}