package ph.edu.dlsu.finwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityAddFriendsBinding
import ph.edu.dlsu.finwise.databinding.ActivityMainBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinancialAssessmentFinlitExpertActivity
import ph.edu.dlsu.finwise.loginRegisterModule.LoginActivity
import ph.edu.dlsu.finwise.loginRegisterModule.ParentRegisterActivity
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialManagementModule.ParentFinancialManagementActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (currentUser != null) {
//            firestore.collection("Users").document(currentUser!!.uid).get().addOnSuccessListener {
//                var userObject = it.toObject<Users>()
//                if (userObject?.userType == "Child")
//                    startActivity(Intent(this, PersonalFinancialManagementActivity::class.java))
//                else if (userObject?.userType == "Parent")
//                    startActivity(Intent(this, ParentFinancialManagementActivity::class.java))
//                else if (userObject.userType == "Financial Expert")
//                     startActivity(Intent(this, FinancialAssessmentFinlitExpertActivity::class.java))
//            }
//        }

        binding.btnLogin.setOnClickListener {
            var login = Intent(this, LoginActivity::class.java)
            startActivity(login)
        }

        binding.btnRegister.setOnClickListener {
            var register = Intent(this, ParentRegisterActivity::class.java)
            startActivity(register)
        }
    }
}