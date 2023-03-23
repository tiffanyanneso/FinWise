package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertBinding
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertTypeBinding
import ph.edu.dlsu.finwise.databinding.DialogNewAssessmentBinding
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.sql.Time

class FinlitExpertAssessmentTypeActivity : AppCompatActivity () {

    private lateinit var binding:ActivityFinancialAssessmentFinlitExpertTypeBinding
    private var firestore = Firebase.firestore

    private var existing = false

    private lateinit var assessmentCategory:String
    private lateinit var assessmentType:String
    private lateinit var assessmentID:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        assessmentCategory = bundle.getString("assessmentCategory").toString()

        var specificAssessment = Intent(this, FinlitExpertSpecificAssessmentActivity::class.java)
        var dataBundle = Bundle()
        dataBundle.putString("assessmentCategory", assessmentCategory)


        binding.btnPreliminary.setOnClickListener {
            assessmentType = binding.tvPreliminary.text.toString()
            checkExisting(assessmentType)
        }

        binding.btnPreActivity.setOnClickListener {
            assessmentType = binding.tvPreactivity.text.toString()
            checkExisting(assessmentType)
        }

        binding.btnPostActivity.setOnClickListener {
            assessmentType = binding.tvPostactivity.text.toString()
            checkExisting(assessmentType)
        }

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_assessment)
        loadBackButton()
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToFinancialAssessmentFinlitExpert = Intent(applicationContext, FinancialAssessmentFinlitExpertActivity::class.java)
            startActivity(goToFinancialAssessmentFinlitExpert)
        }
    }

    //check if the assessment already exists, otherwise open dialog to prompt user to create new assessment
    private fun checkExisting(assessmentType:String) {
        firestore.collection("Assessments").whereEqualTo("assessmentCategory", assessmentCategory).
            whereEqualTo("assessmentType", assessmentType).get().addOnSuccessListener { results ->
                if (results.size() > 0) {
                    assessmentID = results.documents[0].id
                    var specificAssessment = Intent(this, FinlitExpertSpecificAssessmentActivity::class.java)
                    var dataBundle = Bundle()
                    dataBundle.putString("assessmentID", assessmentID)
                    specificAssessment.putExtras(dataBundle)
                    this.startActivity(specificAssessment)
                } else
                    createNewAssessmentDialog()
            }
    }


    private fun createNewAssessmentDialog() {
        var dialogBinding= DialogNewAssessmentBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(800, 800)

        dialogBinding.btnYes.setOnClickListener {
            var assessment = hashMapOf(
                "assessmentName" to "assessmentName",
                "assessmentCategory" to assessmentCategory,
                "assessmentType" to assessmentType,
                "createdOn" to Timestamp.now(),
                "createdBy" to currentUser,
                "nTakes" to 0
            )
            firestore.collection("Assessments").add(assessment).addOnSuccessListener {
                var editAssessment = Intent(this, FinlitExpertEditAssessmentActivity::class.java)
                var bundle = Bundle()
                bundle.putString("assessmentID", it.id)
                editAssessment.putExtras(bundle)
                this.startActivity(editAssessment)
                dialog.dismiss()
            }
        }

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}