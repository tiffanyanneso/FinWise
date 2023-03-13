package ph.edu.dlsu.finwise.financialActivitiesModule.goalDetailsFragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.DialogDeleteGoalWarningBinding
import ph.edu.dlsu.finwise.databinding.FragmentGoalDetailsBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class GoalDetailsFragment : Fragment() {

    private lateinit var binding: FragmentGoalDetailsBinding
    private var firestore = Firebase.firestore
    private lateinit var financialGoalID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            financialGoalID = arguments?.getString("financialGoalID").toString();
        }
        getGoalDetails()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUser()
        binding.btnDelete.setOnClickListener {
            var dialogBinding= DialogDeleteGoalWarningBinding.inflate(getLayoutInflater())
            var dialog= Dialog(requireContext().applicationContext);
            dialog.setContentView(dialogBinding.getRoot())

            dialog.window!!.setLayout(800, 1000)

            dialogBinding.btnOk.setOnClickListener {
                dialog.dismiss()
                //mark financial goal as deleted
                firestore.collection("FinancialGoals").document(financialGoalID).update("status", "Deleted")
                //mark related activities as deleted
                firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener { results ->
                    for (activity in results )
                        firestore.collection("FinancialActivities").document(activity.id).update("status", "Deleted")
                }
                var finact = Intent(requireContext().applicationContext, FinancialActivity::class.java)
                startActivity(finact)
            }

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun getGoalDetails() {

        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var goal = it.toObject<FinancialGoals>()
            binding.tvGoalAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount!!)
            binding.tvGoalName.text = goal.goalName
            binding.tvActivity.text = goal.financialActivity.toString()

            //convert timestamp to string date
            binding.tvDateSet.text = SimpleDateFormat("MM/dd/yyyy").format(goal.dateCreated?.toDate()).toString()
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(goal.targetDate?.toDate()).toString()
            binding.tvStatus.text = goal?.status.toString()
            if (goal?.goalIsForSelf == true)
                binding.tvIsForChild.text = "Yes"
            else
                binding.tvIsForChild.text = "No"

        }
    }

    private fun checkUser() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ParentUser").document(currentUser).get().addOnSuccessListener {
            if (it.exists())
                binding.btnDelete.visibility = View.GONE
            else
                binding.btnDelete.visibility = View.VISIBLE
        }
    }
}