package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.financialActivitiesModule.performance.GoalSettingPerformanceActivity
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactGoalSettingAdapter
import ph.edu.dlsu.finwise.databinding.*
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.NewGoal
import ph.edu.dlsu.finwise.financialAssessmentModule.FinancialAssessmentActivity
import ph.edu.dlsu.finwise.model.FinancialAssessmentAttempts
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.GoalRating
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

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

    private var assessmentTaken = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nRatings = 0
        nOverall = 0.00F
        nSpecific = 0.00F
        nMeasurable = 0.00F
        nAchievable = 0.00F
        nRelevant = 0.00F
        nTimeBound = 0.00F
        assessmentTaken = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalSettingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = "Overall Goal Setting Performance"
        getAssessmentStatus()
        getForReviewGoals()
        getOnGoingGoals()
        initializeRating()


        binding.btnViewSMARTGoalsInfo.setOnClickListener{
            showGoalDialog()
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
                var goalObject = goalForReview.toObject<FinancialGoals>()
                goalFilterArrayList.add(GoalFilter(goalForReview.id, goalObject.targetDate!!.toDate()))
            }
            goalFilterArrayList.sortBy { it.goalTargetDate }

            for (goal in goalFilterArrayList)
                goalIDArrayList.add(goal.financialGoalID.toString())
            loadRecyclerView(goalIDArrayList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAssessmentStatus() {
        firestore.collection("Assessments").whereEqualTo("assessmentType", "Pre-Activity").whereEqualTo("assessmentCategory", "Goal Setting").get().addOnSuccessListener {
            if (it.size() != 0) {
                var assessmentID = it.documents[0].id
                firestore.collection("AssessmentAttempts")
                    .whereEqualTo("assessmentID", assessmentID)
                    .whereEqualTo("childID", currentUser)
                    .orderBy("dateTaken", Query.Direction.DESCENDING)
                    .get().addOnSuccessListener { results ->
                    if (results.size() != 0) {
                        val assessmentAttemptsObjects = results.toObjects<FinancialAssessmentAttempts>()
                        val latestAssessmentAttempt = assessmentAttemptsObjects[0].dateTaken
                        val lastTakenFormat = SimpleDateFormat("MM/dd/yyyy").format(latestAssessmentAttempt!!.toDate())
                        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                        val from = LocalDate.parse(lastTakenFormat, dateFormatter)
                        val today = LocalDate.now()
                        // includes the month difference as well instead of just the day difference
                        val daysDifference = ChronoUnit.DAYS.between(from, today)
                        assessmentTaken = daysDifference < 7
                        Log.d("seiko", "daysDifference: "+ daysDifference)
                        Log.d("seiko", "from: "+ from)
                        Log.d("seiko", "today: "+ today)
                        /*val from = LocalDate.parse(lastTakenFormat.toString(), dateFormatter)
                        val today = SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate())
                        val to = LocalDate.parse(today.toString(), dateFormatter)
                        val difference = Period.between(from, to)*/
                        /*Log.d("seiko", "latestAssessmentAttempt: "
                                +SimpleDateFormat("MM/dd/yyyy")
                            .format(latestAssessmentAttempt.toDate()))
                        Log.d("seiko", "today: "+today)*/

                    } else
                        assessmentTaken = false
                }.continueWith {
                    binding.btnNewGoal.setOnClickListener {
                        if (!assessmentTaken)
                            buildAssessmentDialog()
                        else if (ongoingGoals >= 5)
                            buildDialog()
                        else
                            showSMARTGoalsDialog()
                    }
                }
            }
        }
    }

    private fun buildAssessmentDialog() {
        val dialogBinding= DialogTakeAssessmentBinding.inflate(layoutInflater)
        val dialog= Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)
        dialog.window!!.setLayout(950, 900)
        dialog.setCancelable(false)
        dialogBinding.btnOk.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("assessmentType", "Pre-Activity")
            bundle.putString("assessmentCategory", "Goal Setting")

            val assessmentQuiz = Intent(requireContext(), FinancialAssessmentActivity::class.java)
            assessmentQuiz.putExtras(bundle)
            startActivity(assessmentQuiz)
            dialog.dismiss()
        }
        dialog.show()
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

    @SuppressLint("SetTextI18n")
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
            var overall = 0.00F
            binding.tvOverallRating.visibility = View.GONE
            if (nRatings!=0) {
                overall = nOverall / nRatings
                var percentage = (overall / 5) * 100

                if (percentage >= 96) {
                    binding.imgFace.setImageResource(R.drawable.excellent)
                    binding.tvPerformanceStatus.text = "Excellent"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                    binding.tvPerformanceText.text = "Keep up the excellent work! Goal Setting is your strong point. Keep setting those goals!"
                    showSeeMoreButton()
                } else if (percentage < 96 && percentage >= 86) {
                    binding.imgFace.setImageResource(R.drawable.amazing)
                    binding.tvPerformanceStatus.text = "Amazing"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Amazing job! You are performing well. Goal Setting is your strong point. Keep setting those goals!"
                    showSeeMoreButton()
                } else if (percentage < 86 && percentage >= 76) {
                    binding.imgFace.setImageResource(R.drawable.great)
                    binding.tvPerformanceStatus.text = "Great"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Great job! You are performing well. Keep setting those goals!"
                    showSeeMoreButton()
                } else if (percentage < 76 && percentage >= 66) {
                    binding.imgFace.setImageResource(R.drawable.good)
                    binding.tvPerformanceStatus.text = "Good"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                    binding.tvPerformanceText.text = "Good job! With a bit more dedication and effort, you’ll surely up your performance!"
                    showSeeMoreButton()
                } else if (percentage < 66 && percentage >= 56) {
                    binding.imgFace.setImageResource(R.drawable.average)
                    binding.tvPerformanceStatus.text = "Average"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                    binding.tvPerformanceText.text = "Nice work! Work on improving your goal setting performance. Review SMART Goals!"
                    showSeeMoreButton()
                } else if (percentage < 56 && percentage >= 46) {
                    binding.imgFace.setImageResource(R.drawable.nearly_there)
                    binding.tvPerformanceStatus.text = "Nearly There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "You're nearly there! Click review to learn how to get there!"
                    showSeeMoreButton()
                } else if (percentage < 46 && percentage >= 36) {
                    binding.imgFace.setImageResource(R.drawable.almost_there)
                    binding.tvPerformanceStatus.text = "Almost There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Almost there! You need to work on your goal setting. Click review to learn how!"
                    showSeeMoreButton()
                } else if (percentage < 36 && percentage >= 26) {
                    binding.imgFace.setImageResource(R.drawable.getting_there)
                    binding.tvPerformanceStatus.text = "Getting There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Getting there! You need to work on your goal setting. Click review to learn how!"
                    showSeeMoreButton()
                } else if (percentage < 26 && percentage >= 16) {
                    binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                    binding.tvPerformanceStatus.text = "Not Quite\nThere"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
                    showSeeMoreButton()
                } else if (percentage < 15) {
                    binding.imgFace.setImageResource(R.drawable.bad)
                    binding.tvPerformanceStatus.text = "Needs\nImprovement"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Uh oh! You need to work on your goal setting. Click review to learn how!"
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
        binding.rvViewGoals.visibility = View.VISIBLE
        binding.loadingItems.stopShimmer()
        binding.loadingItems.visibility = View.GONE
    }

    private fun buildDialog() {

        var dialogBinding= DialogNewGoalWarningBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())
        // Initialize dialog

        dialog.window!!.setLayout(900, 600)

        dialogBinding.tvMessage.text= "You have $ongoingGoals ongoing goals.\nAre you sure you want to start another one?"

        dialogBinding.btnOk.setOnClickListener {
            var newGoal = Intent (requireContext().applicationContext, NewGoal::class.java)

            var bundle = Bundle()
            bundle.putString("source", "Child")
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

    private fun showSMARTGoalsDialog() {
        var dialogBinding= DialogSmartGoalInfoBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext());
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            var goToNewGoal = Intent(requireContext().applicationContext, NewGoal::class.java)
            var bundle = Bundle()
            bundle.putString("source", "Child")
            goToNewGoal.putExtras(bundle)
            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToNewGoal)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showGoalDialog() {

        var dialogBinding= DialogSmartReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnReviewGoals.setOnClickListener {
            dialog.dismiss()
            var goToGoalSetting = Intent(requireContext().applicationContext, FinancialActivity::class.java)
            this.startActivity(goToGoalSetting)
        }

        dialogBinding.btnSetNewGoal.setOnClickListener {
            dialog.dismiss()
            var goToNewGoal = Intent(requireContext().applicationContext, NewGoal::class.java)
            var bundle = Bundle()
            bundle.putString("source", "Child")
            goToNewGoal.putExtras(bundle)
            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToNewGoal)
        }

        dialog.show()
    }
}