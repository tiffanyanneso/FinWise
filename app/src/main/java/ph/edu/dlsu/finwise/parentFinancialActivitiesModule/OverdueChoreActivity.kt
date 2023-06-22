package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityOverdueChoreBinding
import ph.edu.dlsu.finwise.databinding.DialogConfirmDeleteEarningBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.CompletedEarningActivity
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class OverdueChoreActivity : AppCompatActivity() {

    private lateinit var binding:ActivityOverdueChoreBinding

    private var firestore = Firebase.firestore

    private lateinit var earningActivityID:String
    private lateinit var childID:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    lateinit var ImageUri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOverdueChoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigationBar()
        loadBackButton()
        var bundle = intent.extras!!
        earningActivityID = bundle.getString("earningActivityID").toString()
        childID = bundle.getString("childID").toString()

        getDetails()

        binding.btnEdit.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("earningActivityID", earningActivityID)
            bundle.putString("childID", childID)
            var editChore = Intent(this, EditEarningActivity::class.java)
            editChore.putExtras(bundle)
            startActivity(editChore)
        }

        binding.btnDelete.setOnClickListener {
            confirmDeleteDialog()
        }

        binding.btnCompleted.setOnClickListener {
            selectImage()
        }

        binding.btnUpload.setOnClickListener {
            uploadImage()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==  100 && resultCode ==  RESULT_OK) {
            ImageUri = data?.data!!
            binding.imageProof.setImageURI(ImageUri)
            binding.layoutButton.visibility = View.GONE
            binding.layoutTwoButtons.visibility = View.VISIBLE
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading file...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        var storageReference = Firebase.storage.getReference("images/$earningActivityID")
        storageReference.putFile(ImageUri).addOnSuccessListener {
            binding.imageProof.setImageURI(null)
            val completedEarning = Intent(this, CompletedEarningActivity::class.java)
            firestore.collection("EarningActivities").document(earningActivityID).update("dateCompleted", Timestamp.now())
            val bundle = Bundle()
            bundle.putString("earningActivityID", earningActivityID)
            bundle.putString("childID", childID)
            completedEarning.putExtras(bundle)
            startActivity(completedEarning)

            if (progressDialog.isShowing) progressDialog.dismiss()
        }.addOnFailureListener{
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmDeleteDialog() {
        var dialogBinding= DialogConfirmDeleteEarningBinding.inflate(layoutInflater)
        var dialog= Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.window!!.setLayout(850, 680)
        dialogBinding.btnYes.setOnClickListener {
            firestore.collection("EarningActivities").document(earningActivityID).update("status", "Deleted").addOnSuccessListener {
                val earning = Intent(this, EarningActivity::class.java)
                val sendBundle = Bundle()
                sendBundle.putString("childID", childID)
                earning.putExtras(sendBundle)
                startActivity(earning)
            }
        }

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getDetails() {
        firestore.collection("EarningActivities").document(earningActivityID).get().addOnSuccessListener {
            val earning = it.toObject<ph.edu.dlsu.finwise.model.EarningActivityModel>()
            binding.tvActivity.text = earning?.activityName
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(earning?.targetDate!!.toDate()).toString()
            binding.tvPaymentType.text = earning?.paymentType
            binding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(earning?.amount)
            binding.tvDuration.text = earning?.requiredTime.toString() + " minutes"
            binding.tvSource.text = earning?.depositTo

            if (earning.depositTo == "Financial Goal") {
                binding.layoutGoalName.visibility = View.VISIBLE
                firestore.collection("FinancialActivities").document(earning?.savingActivityID!!).get().addOnSuccessListener {
                    var goalID = it.toObject<FinancialActivities>()!!.financialGoalID
                    firestore.collection("FinancialGoals").document(goalID!!).get().addOnSuccessListener {
                        binding.tvGoalName.text = it.toObject<FinancialGoals>()!!.goalName
                    }
                }
            } else
                binding.layoutGoalName.visibility = View.GONE
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
                //sends the ChildID to the parent navbar
                val bundle = intent.extras!!
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
            } else if (it.toObject<Users>()!!.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
            }
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}