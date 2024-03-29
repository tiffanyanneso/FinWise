package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class GoalSettingFragment : Fragment() {

    private lateinit var binding: FragmentGoalSettingBinding
    private var firestore = Firebase.firestore
    private lateinit var goalSettingAdapter: FinactGoalSettingAdapter

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerGoalDialog: MediaPlayer
    private lateinit var mediaPlayerSmartDialog: MediaPlayer

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
        if (isAdded) {
            binding.title.text = "Overall Goal Setting Performance"
            getAssessmentStatus()
            getForReviewGoals()
            getOnGoingGoals()
            initializeRating()
        }


        binding.btnViewSMARTGoalsInfo.setOnClickListener{
            if (this::mediaPlayer.isInitialized)
                pauseMediaPlayer(mediaPlayer)
            showGoalDialog()
        }

        binding.btnSeeMore.setOnClickListener {
            if (this::mediaPlayer.isInitialized)
                pauseMediaPlayer(mediaPlayer)
            var goToPerformance = Intent(requireContext().applicationContext, GoalSettingPerformanceActivity::class.java)
            this.startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            if (this::mediaPlayer.isInitialized)
                pauseMediaPlayer(mediaPlayer)
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
                        if (this::mediaPlayer.isInitialized)
                            pauseMediaPlayer(mediaPlayer)
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
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", Arrays.asList("In Progress", "For Review", "For Editing")).get().addOnSuccessListener { results ->
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
            //TODO: Change audio
            var audio = 0
            if (nRatings!=0) {
                overall = nOverall / nRatings
                var percentage = (overall / 5) * 100

                if (percentage >= 96) {
                    audio = R.raw.goal_setting_performance_overall_excellent
                    binding.imgFace.setImageResource(R.drawable.excellent)
                    binding.tvPerformanceStatus.text = "Excellent"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                    binding.tvPerformanceText.text = "Keep up the excellent work! Goal Setting is your strong point. Keep setting those goals!"
                    showSeeMoreButton()
                } else if (percentage < 96 && percentage >= 86) {
                    audio = R.raw.goal_setting_performance_overall_amazing
                    binding.imgFace.setImageResource(R.drawable.amazing)
                    binding.tvPerformanceStatus.text = "Amazing"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Amazing job! You are performing well. Goal Setting is your strong point. Keep setting those goals!"
                    showSeeMoreButton()
                } else if (percentage < 86 && percentage >= 76) {
                    audio = R.raw.goal_setting_performance_overall_great
                    binding.imgFace.setImageResource(R.drawable.great)
                    binding.tvPerformanceStatus.text = "Great"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Great job! You are performing well. Keep setting those goals!"
                    showSeeMoreButton()
                } else if (percentage < 76 && percentage >= 66) {
                    audio = R.raw.goal_setting_performance_overall_good
                    binding.imgFace.setImageResource(R.drawable.good)
                    binding.tvPerformanceStatus.text = "Good"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                    binding.tvPerformanceText.text = "Good job! With a bit more dedication and effort, you’ll surely up your performance!"
                    showSeeMoreButton()
                } else if (percentage < 66 && percentage >= 56) {
                    audio = R.raw.goal_setting_performance_overall_average
                    binding.imgFace.setImageResource(R.drawable.average)
                    binding.tvPerformanceStatus.text = "Average"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                    binding.tvPerformanceText.text = "Nice work! Work on improving your goal setting performance. Review SMART Goals!"
                    showSeeMoreButton()
                } else if (percentage < 56 && percentage >= 46) {
                    audio = R.raw.goal_setting_performance_overall_nearly_there
                    binding.imgFace.setImageResource(R.drawable.nearly_there)
                    binding.tvPerformanceStatus.text = "Nearly\nThere"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
                    binding.tvPerformanceText.text = "You're nearly there! Click review to learn how to get there!"
                    showSeeMoreButton()
                } else if (percentage < 46 && percentage >= 36) {
                    audio = R.raw.goal_setting_performance_overall_almost_there
                    binding.imgFace.setImageResource(R.drawable.almost_there)
                    binding.tvPerformanceStatus.text = "Almost\nThere"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.almost_there_yellow))
                    binding.tvPerformanceText.text = "Almost there! You need to work on your goal setting. Click review to learn how!"
                    showSeeMoreButton()
                } else if (percentage < 36 && percentage >= 26) {
                    audio = R.raw.goal_setting_performance_overall_getting_there
                    binding.imgFace.setImageResource(R.drawable.getting_there)
                    binding.tvPerformanceStatus.text = "Getting\nThere"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.getting_there_orange))
                    binding.tvPerformanceText.text = "Getting there! You need to work on your goal setting. Click review to learn how!"
                    showSeeMoreButton()
                } else if (percentage < 26 && percentage >= 16) {
                    audio = R.raw.goal_setting_performance_overall_not_quite_there_yet
                    binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                    binding.tvPerformanceStatus.text = "Not Quite\nThere"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.not_quite_there_red))
                    binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
                    showSeeMoreButton()
                } else if (percentage < 15) {
                    audio = R.raw.goal_setting_performance_overall_needs_improvement
                    binding.imgFace.setImageResource(R.drawable.bad)
                    binding.tvPerformanceStatus.text = "Needs\nImprovement"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Uh oh! You need to work on your goal setting. Click review to learn how!"
                    showSeeMoreButton()
                }
                binding.tvOverallRating.visibility = View.VISIBLE
                binding.tvOverallRating.text ="${DecimalFormat("0.0").format(overall)}/5.0"
            } else {
                audio = R.raw.goal_setting_performance_default
                binding.imgFace.setImageResource(R.drawable.peso_coin)
                binding.tvPerformanceStatus.text = "Get\nStarted!"
                binding.tvPerformanceText.text = "Set goals to see your performance!"
            }
            binding.layoutLoading.visibility= View.GONE
            binding.mainLayout.visibility = View.VISIBLE
            loadOverallAudio(audio)
        }

    }

    private fun loadOverallAudio(audio: Int) {
        binding.btnAudioOverallGoalSettingPerformance.setOnClickListener {
            if (!this::mediaPlayer.isInitialized) {
                mediaPlayer = MediaPlayer.create(context, audio)
            }

            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayer.start()
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
        /*binding.loadingItems.stopShimmer()
        binding.loadingItems.visibility = View.GONE*/
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
        loadSmartDialogAudio(dialogBinding)
        //dialog.setOnDismissListener { mediaPlayerSmartDialog.let { it1 -> pauseMediaPlayer(it1) } }
        dialog.show()
    }

    override fun onDestroy() {
        if (this::mediaPlayer.isInitialized)
            releaseMediaPlayer(mediaPlayer)

        if (this::mediaPlayerGoalDialog.isInitialized)
            releaseMediaPlayer(mediaPlayerGoalDialog)

        if (this::mediaPlayerSmartDialog.isInitialized)
            releaseMediaPlayer(mediaPlayerSmartDialog)

        super.onDestroy()
    }

    private fun releaseMediaPlayer(mediaPlayer: MediaPlayer) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    private fun loadSmartDialogAudio(dialogBinding: DialogSmartGoalInfoBinding) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        val audio = R.raw.dialog_smart_goal_info

        dialogBinding.btnSoundSmartGoalInfo.setOnClickListener {
            if (!this::mediaPlayerSmartDialog.isInitialized) {
                mediaPlayerSmartDialog = MediaPlayer.create(context, audio)
            }

            if (mediaPlayerSmartDialog.isPlaying) {
                mediaPlayerSmartDialog.pause()
                mediaPlayerSmartDialog.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerSmartDialog.start()
        }
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

        loadGoalDialogAudio(dialogBinding)
        dialog.setOnDismissListener { pauseMediaPlayer(mediaPlayerGoalDialog) }

        dialog.show()
    }

    private fun pauseMediaPlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
        }
    }


    private fun loadGoalDialogAudio(dialogBinding: DialogSmartReviewBinding) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        val audio = R.raw.dialog_smart_review

        dialogBinding.btnSoundSmartReview.setOnClickListener {
            if (!this::mediaPlayerGoalDialog.isInitialized) {
                mediaPlayerGoalDialog = MediaPlayer.create(context, audio)
            }

            if (mediaPlayerGoalDialog.isPlaying) {
                mediaPlayerGoalDialog.pause()
                mediaPlayerGoalDialog.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerGoalDialog.start()
        }
    }

}