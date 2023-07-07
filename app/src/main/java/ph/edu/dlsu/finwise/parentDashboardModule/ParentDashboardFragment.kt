package ph.edu.dlsu.finwise.parentDashboardModule

import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.DialogDashboardFinancialHealthScoreBinding
import ph.edu.dlsu.finwise.databinding.DialogDashboardGoalDifferenceBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentDashboardBinding
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*


class ParentDashboardFragment : Fragment() {

    private lateinit var binding: FragmentParentDashboardBinding
    private var firestore = Firebase.firestore

    private lateinit var childID:String

    private var age = 0
    private var personalFinancePerformance = 0.00F

    private var goalSettingPercentage = 0.00F
    private var savingPercentage = 0.00F
    private var budgetingPercentage = 0.00F
    private var nParent = 0
    private var purchasedBudgetItemCount = 0.00F
    private var totalBudgetAccuracy = 0.00F
    //number of budget items, including those from ongoing budgeting activities
    private var budgetItemCount = 0.00F
    private var spendingPercentage = 0.00F
    //number of budget items of completed budgting activities
    private var nBudgetItems = 0.00F
    private var overSpending = 0.00F
    private var overspendingPercentage = 0.00F
    private var nPlanned = 0.00F
    private var nTotalPurchased = 0.00F

    private var financialActivitiesPerformance = 0.00F

    private var financialAssessmentPerformance = 0.00F

    private var nGoalSettingCompleted = 0
    private var nSavingCompleted = 0
    private var nBudgetingCompleted = 0
    private var nSpendingCompleted = 0
    //if there are no assessments taken yet, do not include in fin health computation
    private var nAssessmentsTaken = 0

    private var overallFinancialHealth = 0.00F

    private var coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = requireArguments()
        childID = bundle.getString("childID").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coroutineScope.launch {

            if (isAdded) {
                getOngoingCount()
                getOverallFinancialHealth()
            }
        }

        binding.layoutFinancialHealthScore.setOnClickListener {
            var dialogBinding= DialogDashboardFinancialHealthScoreBinding.inflate(getLayoutInflater())
            var dialog= Dialog(requireContext());
            dialog.setContentView(dialogBinding.getRoot())
            dialog.window!!.setLayout(1000, 900)
            dialog.show()

            dialogBinding.btnGotIt.setOnClickListener {
                dialog.dismiss()
            }
        }

        binding.layoutGoalDifference.setOnClickListener {
            var dialogBinding= DialogDashboardGoalDifferenceBinding.inflate(getLayoutInflater())
            var dialog= Dialog(requireContext());
            dialog.setContentView(dialogBinding.getRoot())
            dialog.window!!.setLayout(1000, 1500)
            dialog.show()

            dialogBinding.btnGotIt.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private suspend fun getOngoingCount() {
        var child = firestore.collection("Users").document(childID).get().await().toObject<Users>()
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val from = LocalDate.now()
        val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
        val to = LocalDate.parse(date.toString(), dateFormatter)
        val difference = Period.between(to, from)

        if (difference.years == 10 || difference.years == 11) {
            var goalSetting = firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereIn("status", Arrays.asList("For Review", "For Editing")).get().await()
            binding.tvGoalSetting.text = goalSetting.size().toString()
            binding.layoutGoalSettingInProgress.visibility = View.VISIBLE
        } else
            binding.layoutGoalSettingInProgress.visibility = View.GONE

        var saving = firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().await()
        binding.tvSaving.text = saving.size().toString()

        var budgeting = firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "In Progress").get().await()
        binding.tvBudgeting.text = budgeting.size().toString()

        var spending = firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "In Progress").get().await()
        binding.tvSpending.text = spending.size().toString()

    }

    private suspend fun getOverallFinancialHealth(){
        var scoreQuery = firestore.collection("Scores").whereEqualTo("childID", childID).get().await()
        var pfm = 0.00F
        var pfmCount = 0
        var finact = 0.00F
        var finactCount = 0
        var assessments = 0.00F
        var assessmentsCount = 0

        if (!scoreQuery.isEmpty) {
            var scores = scoreQuery.toObjects<ScoreModel>()
            for (score in scores) {
                when (score.type) {
                    "pfm" -> {
                        pfm += score.score!!
                        pfmCount++
                    }
                    "finact" -> {
                        finact += score.score!!
                        finactCount++
                    }
                    "assessments" -> {
                        assessments += score.score!!
                        assessmentsCount++
                    }
                }
            }

            if (pfmCount!=0)
                pfm /= pfmCount
            if (finactCount!=0)
                finact /= finactCount
            if (assessmentsCount!=0)
                assessments /= assessmentsCount

            //there is score for all components
            if (pfm > 0.00F && finact > 0.00F && assessments > 0.00F)
                overallFinancialHealth = ((pfm*.35) + (finact*.35) + (assessments*.30)).toFloat()
            //there is pfm score, but no finact score
            else if (pfm > 0.00F && finact == 0.00F && assessments > 0.00F)
                overallFinancialHealth = ((pfm*.5) + (assessments*.50)).toFloat()
            //there is no score for pfm, which also means there is no finact score, but they've taken pre lim assessment before
            else if (pfm == 0.00F && finact ==  0.00F && assessments > 0.00F)
                overallFinancialHealth = assessments

            if (isAdded) {
                binding.tvFinancialHealthScore.text = DecimalFormat("##0.0").format(overallFinancialHealth) + "%"
                binding.tvPersonalFinancePercentage.text = DecimalFormat("##0.0").format(pfm) + "%"
                binding.progressBarPersonalFinance.progress = pfm.toInt()
                binding.tvFinancialActivitiesPercentage.text = DecimalFormat("##0.0").format(finact) + "%"
                binding.progressBarFinancialActivities.progress = finact.toInt()
                binding.tvFinancialAssessmentsPercentage.text = DecimalFormat("##0.0").format(assessments) + "%"
                binding.progressBarFinancialAssessments.progress = assessments.toInt()
            }

            if (isAdded) {
                if (pfm > 0.0 && finact > 0.0 && assessments >0.0) {
                    loadDifferenceFromGoal()
                    setPerformanceView()
                } else
                    noScoreLayout()
            }
        } else {
            if (isAdded)
                noScoreLayout()
        }
    }

    private suspend fun loadDifferenceFromGoal() {
        var settings = firestore.collection("Settings").whereEqualTo("childID", childID).get().await()
        if (!settings.isEmpty) {
            var literacyGoal =settings.documents[0].toObject<SettingsModel>()?.literacyGoal
            var lower = 0.00F
            var upper = 0.00F
            when (literacyGoal) {
                "Excellent" -> {
                    lower = 90.0F
                    upper = 100F
                }

                "Amazing" -> {
                    lower = 80.0F
                    upper = 89.99F
                }

                "Great" -> {
                    lower = 70.0F
                    upper = 79.9F
                }

                "Good" -> {
                    lower = 60.0F
                    upper = 69.9F
                }
            }


            if (isAdded) {
                if (overallFinancialHealth > upper) {
                    binding.tvGoalDiffPercentage.text = "${DecimalFormat("##0.0").format(overallFinancialHealth - upper)}%"
                    binding.tvGoalDiffStatus.text = "Above Target"
                    binding.tvGoalDiffStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_green))
                    binding.ivGoalDiffImg.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.up_arrow))
                } else if (overallFinancialHealth in lower..upper) {
                    //binding.tvGoalDiffPercentage.text = "${DecimalFormat("##0.0").format(overallFinancialHealthCurrentMonth - upper)}%"
                    binding.tvGoalDiffStatus.text = "Within Target"
                    binding.tvGoalDiffStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
                    binding.ivGoalDiffImg.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.icon_equal))
                } else if (overallFinancialHealth < lower) {
                    binding.tvGoalDiffPercentage.text = "${DecimalFormat("##0.0").format(lower - overallFinancialHealth)}%"
                    binding.tvGoalDiffStatus.text = "Below Target"
                    binding.tvGoalDiffStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.ivGoalDiffImg.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.down_arrow))
                }
            }
        }
    }

    private fun setPerformanceView() {
        var percentage = overallFinancialHealth * 100

        if (percentage >= 96) {
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.tvPerformanceStatus.text = "Excellent"
            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Your child is a financial guru! Celebrate their accomplishments and encourage them to keep it up!"
        } else if (percentage < 96 && percentage >= 86) {
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.tvPerformanceStatus.text = "Amazing"
            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Your child has a solid foundation in personal finance, financial activities, and concepts. Keep empowering them!"
        } else if (percentage < 86 && percentage >= 76) {
            binding.imgFace.setImageResource(R.drawable.great)
            binding.tvPerformanceStatus.text = "Great"
            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Your child has strong financial decision-making skills. Encourage them to keep this up!"
        } else if (percentage < 76 && percentage >= 66) {
            binding.imgFace.setImageResource(R.drawable.good)
            binding.tvPerformanceStatus.text = "Good"
            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Your child is good at real-life financial decision-making & has a good grasp of financial concepts!"
        } else if (percentage < 66 && percentage >= 56) {
            binding.imgFace.setImageResource(R.drawable.average)
            binding.tvPerformanceStatus.text = "Average"
            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Continue supporting your child in their development by having them participate in decision making activities at home!"
        } else if (percentage < 56 && percentage >= 46) {
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.tvPerformanceStatus.text = "Nearly There"
            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text = "Your child is nearly there. Have them participate in decision making activities at home!"
        } else if (percentage < 46 && percentage >= 36) {
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.tvPerformanceStatus.text = "Almost There"
            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text = "Your child is developing their financial decision-making. Have them participate in decision making activities at home!"
        } else if (percentage < 36 && percentage >= 26) {
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.tvPerformanceStatus.text = "Getting There"
            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text = "Your child is still developing their financial decision-making. Allow them to practice this skill at home!"
        } else if (percentage < 26 && percentage >= 16) {
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.tvPerformanceStatus.text = "Not Quite\nThere"
            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is still developing their financial decision-making. Allow them to practice this skill at home!"
        } else if (percentage < 15) {
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.tvPerformanceStatus.text = "Needs\nImprovement"
            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is starting their financial journey! Encourage them to keep exploring financial decision-making!"
        }

        binding.layoutLoading.visibility = View.GONE
        binding.mainLayout.visibility = View.VISIBLE
    }

    private suspend fun noScoreLayout() {
        binding.imgFace.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.peso_coin))
        binding.tvFinancialHealthScore.visibility = View.GONE
        binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.tvPerformanceStatus.text = "Get\nStarted"
        var settings = firestore.collection("Settings").whereEqualTo("childID", childID).get().await()
        if (!settings.isEmpty) {
            var literacyGoal = settings.documents[0].toObject<SettingsModel>()?.literacyGoal
            var lower = 0.00F
            var upper = 0.00F
            when (literacyGoal) {
                "Excellent" -> {
                    lower = 90.0F
                    upper = 100F
                }

                "Amazing" -> {
                    lower = 80.0F
                    upper = 89.99F
                }

                "Great" -> {
                    lower = 70.0F
                    upper = 79.9F
                }

                "Good" -> {
                    lower = 60.0F
                    upper = 69.9F
                }
            }
            binding.tvGoalDiffPercentage.text = "${lower} -  ${upper}"
        }
        binding.ivGoalDiffImg.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.goal))
        binding.tvGoalDiffStatus.text = "Target Score"
        binding.tvGoalDiffStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.tvPersonalFinancePercentage.text = "0.0%"
        binding.tvFinancialActivitiesPercentage.text = "0.0%"
        binding.tvFinancialAssessmentsPercentage.text = "0.0%"
        binding.layoutLoading.visibility = View.GONE
        binding.mainLayout.visibility = View.VISIBLE
    }

}
