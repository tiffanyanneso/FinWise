package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemQuestionsBinding
import ph.edu.dlsu.finwise.databinding.ItemSpecificAssessmentQuestionsBinding
import ph.edu.dlsu.finwise.model.AssessmentQuestions

class EditAssessmentQuestionsAdapter : RecyclerView.Adapter<EditAssessmentQuestionsAdapter.EditAssessmentQuestionsViewHolder>{

    private var questionsArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, questionsArrayList:ArrayList<String>) {
        this.context = context
        this.questionsArrayList = questionsArrayList
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
            firestore.collection("AssessmentQuestions").document(questionID).get().addOnSuccessListener {
                var question = it.toObject<AssessmentQuestions>()
                itemBinding.tvQuestion.text  =question?.question
                //itemBinding.switchQuestionActive.isChecked = question?.isUsed!!
            }
        }

        override fun onClick(p0: View?) {

        }
    }
}