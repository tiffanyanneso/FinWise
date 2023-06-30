package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.ParentSettingsActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentFinancialActivity
import ph.edu.dlsu.finwise.databinding.ItemChildBinding
import ph.edu.dlsu.finwise.model.ScoreModel
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialManagementModule.ParentFinancialManagementActivity
import java.text.DecimalFormat

class ParentChildrenAdapter: RecyclerView.Adapter<ParentChildrenAdapter.ChildViewHolder> {

    private var childIDArrayList = ArrayList<String>()
    private var context: Context
    private var firestore = Firebase.firestore

    constructor(context: Context, childIDArrayList:ArrayList<String>) {
        this.context = context
        this.childIDArrayList = childIDArrayList
    }
    override fun getItemCount(): Int {
        return childIDArrayList.size
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParentChildrenAdapter.ChildViewHolder {
        val itemBinding = ItemChildBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ChildViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ParentChildrenAdapter.ChildViewHolder,
                                  position: Int) {
        holder.bindTransaction(childIDArrayList[position])
    }

    inner class ChildViewHolder(private val itemBinding: ItemChildBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindTransaction(childID: String) {
            firestore.collection("Users").document(childID).get().addOnSuccessListener {
                val child = it.toObject<Users>()
                itemBinding.tvChildId.text = it.id
                itemBinding.tvFirstName.text = child?.firstName
            }

            //compute for the child's financial health score
            firestore.collection("Scores").whereEqualTo("childID", childID).get().addOnSuccessListener {
                var pfm = 0.00F
                var pfmCount = 0
                var finact = 0.00F
                var finactCount = 0
                var assessments = 0.00F
                var assessmentsCount = 0

                if (!it.isEmpty) {
                    var scores = it.toObjects<ScoreModel>()
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

                    pfm /= pfmCount
                    finact /= finactCount
                    assessments /= assessmentsCount

                    var finHealthScore = 0.00F
                    //there is score for all components
                    if (pfm > 0.00F && finact > 0.00F && assessments > 0.00F)
                        finHealthScore = ((pfm*.35) + (finact*.35) + (assessments*.30)).toFloat()
                    //there is pfm score, but no finact score
                    else if (pfm > 0.00F && finact == 0.00F && assessments > 0.00F)
                        finHealthScore = ((pfm*.5) + (assessments*.50)).toFloat()
                    //there is no score for pfm, which also means there is no finact score, but they've taken pre lim assessment before
                    else if (pfm == 0.00F && finact ==  0.00F && assessments > 0.00F)
                        finHealthScore = assessments


                    itemBinding.tvScore.text = DecimalFormat("##0.0").format(finHealthScore)
                    if (finHealthScore < 56.0F)
                        itemBinding.imgWarning.visibility = View.VISIBLE
                    else
                        itemBinding.imgWarning.visibility = View.GONE

                    if (pfm == 0.00F && finact ==  0.00F && assessments == 0.00F) {
                        itemBinding.tvScore.text = "Not Yet Started"
                        itemBinding.imgWarning.visibility = View.GONE
                    }
                }
                else {
                    itemBinding.imgWarning.visibility = View.GONE
                    itemBinding.tvScore.text = "Not yet started"
                }
            }

            itemBinding.btnSettings.setOnClickListener {
                var settings = Intent(context, ParentSettingsActivity::class.java)
                var bundle = Bundle()
                bundle.putString("childID", itemBinding.tvChildId.text.toString())
                bundle.putString("parentID", FirebaseAuth.getInstance().currentUser!!.uid)
                settings.putExtras(bundle)
                context.startActivity(settings)
            }
        }

        override fun onClick(p0: View?) {
            val bundle = Bundle()
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            val parentGoal = Intent(context, ParentFinancialManagementActivity::class.java)
            parentGoal.putExtras(bundle)
            context.startActivity(parentGoal)
        }
    }
}