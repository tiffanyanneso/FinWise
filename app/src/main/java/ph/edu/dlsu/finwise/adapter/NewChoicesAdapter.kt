package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemAddNewChoiceBinding

class NewChoicesAdapter : RecyclerView.Adapter<NewChoicesAdapter.ChoicesViewHolder>{

    private var choicesArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, choicesArrayList:ArrayList<String>) {
        this.context = context
        this.choicesArrayList = choicesArrayList
    }

    override fun getItemCount(): Int {
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

        fun bindChoice(choice: String){
            itemBinding.tvChoice.text = choice
        }

        override fun onClick(p0: View?) {

        }
    }
}