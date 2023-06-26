package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemQuestionsLowCorrectAnswersBinding
import ph.edu.dlsu.finwise.databinding.ItemSpecificAssessmentQuestionsBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinlitExpertEditQuestionsActivity
import ph.edu.dlsu.finwise.model.FinancialAssessmentQuestions
import java.text.DecimalFormat

class AssessmentLowAccuracyQuestionsAdapter : RecyclerView.Adapter<AssessmentLowAccuracyQuestionsAdapter.AssessmentLowAccuracyQuestionsViewHolder> {

    private var assessmentID:String

    private var questionsArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, questionsArrayList: ArrayList<String>,  assessmentID:String,) {
        this.context = context
        this.questionsArrayList = questionsArrayList
        this.assessmentID = assessmentID
    }

    override fun getItemCount(): Int {
        return questionsArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AssessmentLowAccuracyQuestionsAdapter.AssessmentLowAccuracyQuestionsViewHolder {
        val itemBinding = ItemQuestionsLowCorrectAnswersBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        return AssessmentLowAccuracyQuestionsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: AssessmentLowAccuracyQuestionsAdapter.AssessmentLowAccuracyQuestionsViewHolder,
        position: Int
    ) {
        holder.bindChoice(questionsArrayList[position])
    }

    inner class AssessmentLowAccuracyQuestionsViewHolder(private val itemBinding: ItemQuestionsLowCorrectAnswersBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindChoice(questionID: String) {
            firestore.collection("AssessmentQuestions").document(questionID).get()
                .addOnSuccessListener {
                    itemBinding.btnEdit.setOnClickListener {
                        var bundle = Bundle()
                        var editQuestion = Intent(context, FinlitExpertEditQuestionsActivity::class.java)
                        bundle.putString("questionID", itemBinding.tvQuestionsId.text.toString())
                        bundle.putString("assessmentID", assessmentID)
                        editQuestion.putExtras(bundle)
                        editQuestion.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(editQuestion)
                    }

                    itemBinding.tvQuestionsId.text = questionID
                    var question = it.toObject<FinancialAssessmentQuestions>()
                    itemBinding.tvQuestion.text = "Question: " + question?.question
                    itemBinding.tvDifficulty.text = "Difficulty: " + question?.difficulty
                    itemBinding.switchQuestionActive.isChecked = question?.isUsed!!

                    var answeredCorrectly = question?.nAnsweredCorrectly?.toFloat()
                    var nAssessment = question?.nAssessments!!?.toFloat()
                    var correctPercentage = 0.00F
                    if (nAssessment != 0.00F)
                        correctPercentage = (answeredCorrectly!! / nAssessment!!) * 100
                    itemBinding.tvCorrectPercentage.text =
                        "Correct percentage: " + DecimalFormat("#,##0.00").format(correctPercentage) + "%"
                }
        }

        override fun onClick(p0: View?) {

        }
    }
}