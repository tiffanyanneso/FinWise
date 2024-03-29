package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemQuestionsBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinlitExpertEditAssessmentActivity
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinlitExpertEditQuestionsActivity

class EditAssessmentQuestionsAdapter : RecyclerView.Adapter<EditAssessmentQuestionsAdapter.EditAssessmentQuestionsViewHolder>{

    private var assessmentID:String
    private var questionStatusArrayList = ArrayList<FinlitExpertEditAssessmentActivity.QuestionStatus>()
    private var context: Context

    private var firestore = Firebase.firestore

    private var questionStatusSwitch: QuestionStatusSwitch


    constructor(context: Context, questionStatusArrayList:ArrayList<FinlitExpertEditAssessmentActivity.QuestionStatus>, assessmentID:String, questionStatusSwitch: QuestionStatusSwitch) {
        this.context = context
        this.questionStatusArrayList = questionStatusArrayList
        this.assessmentID = assessmentID
        this.questionStatusSwitch = questionStatusSwitch
    }

    override fun getItemCount(): Int {
        return questionStatusArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditAssessmentQuestionsAdapter.EditAssessmentQuestionsViewHolder {
        val itemBinding = ItemQuestionsBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return EditAssessmentQuestionsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EditAssessmentQuestionsAdapter.EditAssessmentQuestionsViewHolder,
                                  position: Int) {
        holder.bindQuestion(questionStatusArrayList[position])
    }

    inner class EditAssessmentQuestionsViewHolder(private val itemBinding: ItemQuestionsBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindQuestion(questionStatusObject: FinlitExpertEditAssessmentActivity.QuestionStatus){
            itemBinding.btnEdit.setOnClickListener {
                var bundle = Bundle()
                var editQuestion = Intent(context, FinlitExpertEditQuestionsActivity::class.java)
                bundle.putString("questionID", itemBinding.tvQuestionsId.text.toString())
                bundle.putString("assessmentID", assessmentID)
                editQuestion.putExtras(bundle)
                editQuestion.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(editQuestion)
            }
            itemBinding.tvQuestionsId.text = questionStatusObject.questionID
            itemBinding.tvQuestion.text = questionStatusObject.question
            itemBinding.tvDifficulty.text = questionStatusObject.difficulty
            itemBinding.switchQuestionActive.isChecked = questionStatusObject.isActive!!
            /*firestore.collection("AssessmentQuestions").document(questionID).get().addOnSuccessListener {
                var question = it.toObject<AssessmentQuestions>()
                itemBinding.tvQuestion.text  =question?.question
                itemBinding.tvDifficulty.text = question?.difficulty
                itemBinding.switchQuestionActive.isChecked = question?.isUsed!!
            }*/

            itemBinding.switchQuestionActive.setOnClickListener {
                questionStatusSwitch.clickQuestionStatusSwitch(position, itemBinding.tvQuestionsId.text.toString(), itemBinding.switchQuestionActive.isChecked)
            }
        }

        override fun onClick(p0: View?) {

        }
    }

    interface QuestionStatusSwitch {
        fun clickQuestionStatusSwitch(position:Int, questionID:String, isActive:Boolean)
    }
}