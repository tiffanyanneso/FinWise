package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalDetailsBinding
import ph.edu.dlsu.finwise.databinding.DialogDeleteGoalWarningBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ViewGoalDetails : AppCompatActivity() {

    private lateinit var binding:ActivityViewGoalDetailsBinding
    private var firestore = Firebase.firestore

    private lateinit var financialGoalID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewGoalDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavigationBar()

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        getGoal()
        checkUser()

        binding.topAppBar.setNavigationOnClickListener {
            val goToGoal = Intent(applicationContext, ViewGoalActivity::class.java)
            goToGoal.putExtras(bundle)
            goToGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToGoal)
        }

        binding.btnDelete.setOnClickListener {
            var dialogBinding= DialogDeleteGoalWarningBinding.inflate(getLayoutInflater())
            var dialog= Dialog(this);
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
                var finact = Intent(this, FinancialActivity::class.java)
                startActivity(finact)
                finish()
            }

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun getGoal() {
        firestore.collection("FinancialGoals").document(financialGoalID!!).get().addOnSuccessListener { document ->
            if (document != null) {
                var goal = document.toObject(FinancialGoals::class.java)
                //binding.tvMyGoals.text = goal?.goalName.toString()
                binding.tvGoalName.text = goal?.goalName.toString()
                binding.tvGoalAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount?.toFloat())
                binding.tvActivity.text = goal?.financialActivity.toString()

                //convert timestamp to string date
                binding.tvDateSet.text = SimpleDateFormat("MM/dd/yyyy").format(goal?.dateCreated?.toDate()).toString()
                binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate?.toDate()).toString()
                binding.tvStatus.text = goal?.status.toString()
                if (goal?.goalIsForSelf == true)
                    binding.tvIsForChild.text = "Yes"
                else
                    binding.tvIsForChild.text = "No"

            }
        }
    }

    private fun checkUser() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            if (it.toObject<Users>()!!.userType == "Parent")
                binding.btnDelete.visibility = View.GONE
            else if (it.toObject<Users>()!!.userType == "Child")
                binding.btnDelete.visibility = View.VISIBLE
        }
    }

    private fun setNavigationBar() {

        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.toObject<Users>()!!.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)
            } else if (it.toObject<Users>()!!.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
            }
        }
    }
}