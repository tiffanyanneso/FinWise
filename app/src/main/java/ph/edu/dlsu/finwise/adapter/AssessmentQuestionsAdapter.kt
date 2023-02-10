package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemAddNewChoiceBinding
import ph.edu.dlsu.finwise.databinding.ItemSpecificAssessmentQuestionsBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinlitExpertAddNewQuestionsActivity
import ph.edu.dlsu.finwise.model.AssessmentQuestions

class AssessmentQuestionsAdapter : RecyclerView.Adapter<AssessmentQuestionsAdapter.AssessmentQuestionsViewHolder>{

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
    ): AssessmentQuestionsAdapter.AssessmentQuestionsViewHolder {
        val itemBinding = ItemSpecificAssessmentQuestionsBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return AssessmentQuestionsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: AssessmentQuestionsAdapter.AssessmentQuestionsViewHolder,
                                  position: Int) {
        holder.bindChoice(questionsArrayList[position])
    }

    inner class AssessmentQuestionsViewHolder(private val itemBinding: ItemSpecificAssessmentQuestionsBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindChoice(questionID: String){
            firestore.collection("AssessmentQuestions").document(questionID).get().addOnSuccessListener {
                var question = it.toObject<AssessmentQuestions>()
                itemBinding.tvQuestion.text = question?.question
            }
        }

        override fun onClick(p0: View?) {

        }
    }
}