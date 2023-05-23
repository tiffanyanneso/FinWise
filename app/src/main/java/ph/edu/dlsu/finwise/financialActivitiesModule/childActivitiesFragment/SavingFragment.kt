package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.*
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.NewGoal
import ph.edu.dlsu.finwise.financialActivitiesModule.performance.SavingPerformanceActivity
import ph.edu.dlsu.finwise.financialAssessmentModule.FinancialAssessmentActivity
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class SavingFragment : Fragment() {

    private lateinit var binding: FragmentFinactSavingBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: FinactSavingAdapter

    var goalIDArrayList = ArrayList<String>()
    var savingsArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var ongoingGoals = 0
    //includes achieved goals in count
    private var totalGoals = 0

    private var setOwnGoals = false

    private var assessmentTaken = true

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerDialog: MediaPlayer



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ongoingGoals = 0
        totalGoals = 0
        goalIDArrayList.clear()
        savingsArrayList.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinactSavingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAssessmentStatus()
        binding.title.text = "Overall Saving Performance"

        binding.btnReviewSaving.setOnClickListener{
            if (this::mediaPlayer.isInitialized)
                pauseMediaPlayer(mediaPlayer)
            showGoalDialog()
        }

        binding.btnSeeMore.setOnClickListener {
            if (this::mediaPlayer.isInitialized)
                pauseMediaPlayer(mediaPlayer)
            var goToPerformance = Intent(requireContext().applicationContext, SavingPerformanceActivity::class.java)
            startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, SavingPerformanceActivity::class.java)
            startActivity(goToPerformance)
            if (this::mediaPlayer.isInitialized)
                pauseMediaPlayer(mediaPlayer)
        }


        checkAge()
        getGoals()
        getSavingActivities()
        computeOverallScore()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAssessmentStatus() {
        firestore.collection("Assessments").whereEqualTo("assessmentType", "Pre-Activity").whereEqualTo("assessmentCategory", "Saving").get().addOnSuccessListener {
            if (it.size() != 0) {
                var assessmentID = it.documents[0].id
                firestore.collection("AssessmentAttempts").whereEqualTo("assessmentID", assessmentID).whereEqualTo("childID", currentUser).orderBy("dateTaken", Query.Direction.DESCENDING).get().addOnSuccessListener { results ->
                    if (results.size() != 0) {
                        var assessmentAttemptsObjects = results.toObjects<FinancialAssessmentAttempts>()
                        var latestAssessmentAttempt = assessmentAttemptsObjects.get(0).dateTaken
                        val dateFormatter: DateTimeFormatter =
                            DateTimeFormatter.ofPattern("MM/dd/yyyy")
                        val lastTakenFormat =
                            SimpleDateFormat("MM/dd/yyyy").format(latestAssessmentAttempt!!.toDate())
                        val from = LocalDate.parse(lastTakenFormat.toString(), dateFormatter)
                        val today = SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate())
                        val to = LocalDate.parse(today.toString(), dateFormatter)
                        var difference = Period.between(from, to)

                        if (difference.days >= 7)
                            assessmentTaken = false
                        else
                            assessmentTaken = true
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
//        loadSmartAudioDialog(dialogBinding)
//        dialog.setOnDismissListener { pauseMediaPlayer(mediaPlayerDialog) }

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


    private fun buildAssessmentDialog() {
        var dialogBinding= DialogTakeAssessmentBinding.inflate(layoutInflater)
        var dialog= Dialog(requireContext());
        dialog.setContentView(dialogBinding.root)
        dialog.window!!.setLayout(950, 900)
        dialog.setCancelable(false)
        dialogBinding.btnOk.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("assessmentType", "Pre-Activity")
            bundle.putString("assessmentCategory", "Saving")

            val assessmentQuiz = Intent(requireContext(), FinancialAssessmentActivity::class.java)
            assessmentQuiz.putExtras(bundle)
            startActivity(assessmentQuiz)
            dialog.dismiss()
        }
        dialog.show()
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){ }

    private fun getSavingActivities() {
        goalIDArrayList.clear()
        goalFilterArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            binding.tvTitleInProgress.text = "My Goals (" + results.size().toString() + ")"
            for (goal in results) {
                var goalObject = goal.toObject<FinancialGoals>()
                goalFilterArrayList.add(GoalFilter(goal.id, goalObject.targetDate!!.toDate()))
            }
            goalFilterArrayList.sortBy { it.goalTargetDate }

            for (goal in goalFilterArrayList)
                goalIDArrayList.add(goal.financialGoalID.toString())

            if (!goalIDArrayList.isEmpty())
                loadRecyclerView(goalIDArrayList)
            else {
                binding.loadingItems.stopShimmer()
                binding.loadingItems.visibility = View.GONE
                binding.rvViewGoals.visibility = View.GONE
                binding.layoutEmptyActivity.visibility = View.VISIBLE
            }
        }.continueWith {


            getTotalSavings()
            setGoalCount()} 
    }

    private fun getTotalSavings() {
        var savedAmount = 0.00F
        var cashBalance = 0.00F
        var mayaBalance = 0.00F

        binding.tvGoalSavings.text = "₱ " + DecimalFormat("#,##0.00").format(savedAmount)

        firestore.collection("Transactions").whereEqualTo("userID", currentUser).whereIn("transactionType", Arrays.asList("Deposit", "Withdrawal")).get().addOnSuccessListener { results ->
            for (transaction in results) {
               var transactionObject = transaction.toObject<Transactions>()
               if (transactionObject?.transactionType == "Deposit")
                   savedAmount += transactionObject?.amount!!
               else if (transactionObject.transactionType == "Withdrawal")
                   savedAmount -= transactionObject?.amount!!

                if (transactionObject.paymentType == "Cash") {
                    if (transactionObject?.transactionType == "Deposit")
                        cashBalance += transactionObject?.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        cashBalance -= transactionObject?.amount!!
                }

                else if (transactionObject.paymentType == "Maya") {
                    if (transactionObject?.transactionType == "Deposit")
                        mayaBalance += transactionObject?.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        mayaBalance -= transactionObject?.amount!!
                }
           }
        }.continueWith {
            binding.tvGoalSavings.text = "₱ " + DecimalFormat("#,##0.00").format(savedAmount)
            binding.tvCashSavings.text = "₱ " + DecimalFormat("#,##0.00").format(cashBalance)
            binding.tvMayaSavings.text = "₱ " + DecimalFormat("#,##0.00").format(mayaBalance)

        }
    }

    private fun setGoalCount() {
        var nearCompletion = 0
        var nearDeadline = 0
//        binding.tvNearingCompletion.text = nearCompletion.toString()
//        binding.tvNearingDeadline.text = nearDeadline.toString()
        var currentTime = Timestamp.now().toDate().time

        //set number of goals nearing completion and nearing deadline
        for (goalID in goalIDArrayList) {
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener {
                var goalObject = it.toObject<FinancialGoals>()

                //there is only 20% left before they are able to accomplish their goal, mark goal as near completion
                var amountRemaining =
                    ((goalObject?.targetAmount!! - goalObject?.currentSavings!!) / goalObject?.targetAmount!!) * 100
                if (amountRemaining <= 20)
                    nearCompletion++

                //there is only 20% left before their target date, mark goal as near deadline
                //target date in miliseconds
                var targetDate = goalObject?.targetDate!!.toDate().time

                var timeRemaining = ((targetDate!! - currentTime!!) / 100)
                if (timeRemaining <= 20)
                    nearDeadline++

            }.continueWith {
//                binding.tvNearingCompletion.text = nearCompletion.toString()
//                binding.tvNearingDeadline.text = nearDeadline.toString()
            }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun computeOverallScore() {
        var nTotal = 0.00F
        var nOnTime =0.00F
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            //TODO: Change audio
            var audio = 0

            if (results.size()!=0) {
                nTotal = results.size().toFloat()
                for (goal in results) {
                    var goalObject = goal.toObject<FinancialGoals>()
                    if (goalObject.dateCompleted != null) {
                        var targetDate = goalObject.targetDate!!.toDate()
                        var completedDate = goalObject.dateCompleted!!.toDate()

                        //goal was completed before the target date, meaning it was completed on time
                        if (completedDate.before(targetDate) || completedDate.equals(targetDate)) {
                            nOnTime++
                        }
                    }
                }

                var overall = 0.00F
                if (nTotal != 0.00F)
                    overall = (nOnTime/nTotal) * 100

                val overallRoundedNumber = "%.1f".format(overall).toFloat()

                binding.tvPerformancePercentage.text ="${overallRoundedNumber}%"


                if (overall >= 96) {
                    audio = R.raw.sample
                    binding.imgFace.setImageResource(R.drawable.excellent)
                    binding.tvPerformanceStatus.text = "Excellent"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                    binding.tvPerformanceText.text = "Keep up the excellent work! Saving is your strong point. Keep completing those goals!"
                    showSeeMoreButton()
                } else if (overall < 96 && overall >= 86) {
                    audio = R.raw.sample
                    binding.imgFace.setImageResource(R.drawable.amazing)
                    binding.tvPerformanceStatus.text = "Amazing"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Amazing job! You are performing well. Saving is your strong point. Keep completing those goals!"
                    showSeeMoreButton()
                } else if (overall < 86 && overall >= 76) {
                    audio = R.raw.sample
                    binding.imgFace.setImageResource(R.drawable.great)
                    binding.tvPerformanceStatus.text = "Great"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Great job! You are performing well. Keep completing those goals!"
                    showSeeMoreButton()
                } else if (overall < 76 && overall >= 66) {
                    audio = R.raw.sample
                    binding.imgFace.setImageResource(R.drawable.good)
                    binding.tvPerformanceStatus.text = "Good"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                    binding.tvPerformanceText.text = "Good job! With a bit more dedication and effort, you’ll surely up your performance!"
                    showSeeMoreButton()
                } else if (overall < 66 && overall >= 56) {
                    audio = R.raw.sample
                    binding.imgFace.setImageResource(R.drawable.average)
                    binding.tvPerformanceStatus.text = "Average"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                    binding.tvPerformanceText.text = "Nice work! Work on improving your saving performance through time and effort. You’ll get there soon!"
                    showSeeMoreButton()
                } else if (overall < 56 && overall >= 46) {
                    audio = R.raw.sample
                    binding.imgFace.setImageResource(R.drawable.nearly_there)
                    binding.tvPerformanceStatus.text = "Nearly There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "You're nearly there! Click review to learn how to get there!"
                    showSeeMoreButton()
                }  else if (overall < 46 && overall >= 36) {
                    audio = R.raw.sample
                    binding.imgFace.setImageResource(R.drawable.almost_there)
                    binding.tvPerformanceStatus.text = "Almost There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Almost there! You need to work on your saving. Click review to learn how!"
                    showSeeMoreButton()
                } else if (overall < 36 && overall >= 26) {
                    audio = R.raw.sample
                    binding.imgFace.setImageResource(R.drawable.getting_there)
                    binding.tvPerformanceStatus.text = "Getting There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Getting there! You need to work on your saving. Click review to learn how!"
                    showSeeMoreButton()
                } else if (overall < 26 && overall >= 16) {
                    audio = R.raw.sample
                    binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                    binding.tvPerformanceStatus.text = "Not Quite\nThere"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
                    showSeeMoreButton()
                } else if (overall < 15) {
                    audio = R.raw.sample
                    binding.imgFace.setImageResource(R.drawable.bad)
                    binding.tvPerformanceStatus.text = "Needs\nImprovement"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Uh oh! Your saving performance needs a lot of improvement.  Click review to learn how!"
                    showSeeMoreButton()
                }
            } else {
                audio = R.raw.sample
                binding.imgFace.setImageResource(R.drawable.peso_coin)
                binding.tvPerformancePercentage.text = "Get Started!"
                binding.tvPerformanceText.text = "Complete your goals to see your performance"
            }
            loadAudio(audio)
        }
    }

    private fun loadAudio(audio: Int) {
        //TODO: Change binding and Audio file in mediaPlayer

        binding.btnAudioOverallSavingPerformance.setOnClickListener {
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

    private fun loadSmartAudioDialog(dialogBinding: DialogSmartGoalInfoBinding) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        val audio = R.raw.sample

        dialogBinding.btnSoundSmartGoalInfo.setOnClickListener {
           if (!this::mediaPlayerDialog.isInitialized) {
               mediaPlayerDialog = MediaPlayer.create(context, audio)
           }

            if (mediaPlayerDialog.isPlaying) {
                mediaPlayerDialog.pause()
                mediaPlayerDialog.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerDialog.start()
        }
    }


    override fun onDestroy() {
        if (this::mediaPlayer.isInitialized)
            releaseMediaPlayer(mediaPlayer)

        if (this::mediaPlayerDialog.isInitialized)
            releaseMediaPlayer(mediaPlayerDialog)

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

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalAdapter = FinactSavingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = goalAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        goalAdapter.notifyDataSetChanged()
        binding.rvViewGoals.visibility = View.VISIBLE
        binding.loadingItems.stopShimmer()
        binding.loadingItems.visibility = View.GONE
    }

    private fun getGoals() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).get().addOnSuccessListener { results ->
            totalGoals = results.size()
            for (goalSnapshot in results) {
                var goal = goalSnapshot.toObject<FinancialGoals>()
                if (goal.status == "In Progress")
                    ongoingGoals++
            }
        }
    }


    private fun buildDialog() {

        var dialogBinding= DialogNewGoalWarningBinding.inflate(layoutInflater)
        var dialog= Dialog(requireContext());
        dialog.setContentView(dialogBinding.getRoot())
        // Initialize dialog

        dialog.window!!.setLayout(900, 800)

        dialogBinding.tvMessage.text= "You have $ongoingGoals ongoing goals.\nAre you sure you want to start another one?"

        dialogBinding.btnOk.setOnClickListener {
            var newGoal = Intent (requireContext(), NewGoal::class.java)

            var bundle = Bundle()
            bundle.putString("source", "Child")
            newGoal.putExtras(bundle)
            newGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(newGoal)
            dialog.dismiss()
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAge() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var child = it.toObject<Users>()
            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age < 12 )
                binding.btnNewGoal.visibility = View.GONE
            else {
                binding.btnNewGoal.visibility = View.VISIBLE
                setOwnGoals = true
            }
        }
    }

    private fun showGoalDialog() {

        var dialogBinding= DialogSavingReviewBinding.inflate(layoutInflater)
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.root)

        dialog.window!!.setLayout(1000, 1700)

        if (!setOwnGoals)
            dialogBinding.btnSetNewGoal.visibility = View.GONE
        else
            dialogBinding.btnSetNewGoal.visibility = View.GONE

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