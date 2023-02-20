package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ItemAddNewChoiceBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinlitExpertAddNewQuestionsActivity

class NewChoicesAdapter : RecyclerView.Adapter<NewChoicesAdapter.ChoicesViewHolder>{

    private var choicesArrayList = ArrayList<FinlitExpertAddNewQuestionsActivity.Choice>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, choicesArrayList:ArrayList<FinlitExpertAddNewQuestionsActivity.Choice>) {
        this.context = context
        this.choicesArrayList = choicesArrayList
    }

    override fun getItemCount(): Int {
        //limit the choices to only 4
        if(choicesArrayList.size > 4)
            return 4
        else
            return choicesArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewChoicesAdapter.ChoicesViewHolder {
        val itemBinding = ItemAddNewChoiceBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ChoicesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: NewChoicesAdapter.ChoicesViewHolder,
                                  position: Int) {
        holder.bindChoice(choicesArrayList[position])
    }

    inner class ChoicesViewHolder(private val itemBinding: ItemAddNewChoiceBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindChoice(choice: FinlitExpertAddNewQuestionsActivity.Choice){
            itemBinding.tvChoice.text = choice.choice
            if(choice.correct == true) {
                itemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_green))
                itemBinding.imgCorrectAnswer.visibility = View.VISIBLE
            }
            else if (choice.correct == false) {
                itemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.very_light_green))
                itemBinding.imgCorrectAnswer.visibility = View.GONE
            }

            //itemBinding.switchSetChoices.isChecked = choice.correct!!
        }

        override fun onClick(p0: View?) {

        }
    }
}