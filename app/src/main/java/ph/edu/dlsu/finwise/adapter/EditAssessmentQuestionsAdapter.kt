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
import ph.edu.dlsu.finwise.databinding.ItemQuestionsBinding
import ph.edu.dlsu.finwise.databinding.ItemSpecificAssessmentQuestionsBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinlitExpertEditQuestionsActivity
import ph.edu.dlsu.finwise.model.AssessmentQuestions

class EditAssessmentQuestionsAdapter : RecyclerView.Adapter<EditAssessmentQuestionsAdapter.EditAssessmentQuestionsViewHolder>{

    private var assessmentID:String
    private var questionsArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, questionsArrayList:ArrayList<String>, assessmentID:String) {
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
    ): EditAssessmentQuestionsAdapter.EditAssessmentQuestionsViewHolder {
        val itemBinding = ItemQuestionsBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return EditAssessmentQuestionsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EditAssessmentQuestionsAdapter.EditAssessmentQuestionsViewHolder,
                                  position: Int) {
        holder.bindQuestion(questionsArrayList[position])
    }

    inner class EditAssessmentQuestionsViewHolder(private val itemBinding: ItemQuestionsBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindQuestion(questionID: String){
            itemBinding.btnEdit.setOnClickListener {
                var bundle = Bundle()
                var editQuestion = Intent(context, FinlitExpertEditQuestionsActivity::class.java)
                bundle.putString("questionID", itemBinding.tvQuestionsId.text.toString())
                bundle.putString("assessmentID", assessmentID)
                editQuestion.putExtras(bundle)
                context.startActivity(editQuestion)
            }
            itemBinding.tvQuestionsId.text = questionID
            firestore.collection("AssessmentQuestions").document(questionID).get().addOnSuccessListener {
                var question = it.toObject<AssessmentQuestions>()
                itemBinding.tvQuestion.text  =question?.question
                itemBinding.tvDifficulty.text = question?.difficulty
                itemBinding.switchQuestionActive.isChecked = question?.isUsed!!
            }
        }

        override fun onClick(p0: View?) {

        }
    }
}