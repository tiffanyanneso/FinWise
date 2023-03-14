package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.ParentChildrenAdapter
import ph.edu.dlsu.finwise.databinding.ActivityParentLandingPageBinding
import ph.edu.dlsu.finwise.loginRegisterModule.ParentRegisterChildActivity

class ParentLandingPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityParentLandingPageBinding
    private lateinit var context: Context

    private var firestore = Firebase.firestore

    private lateinit var childAdapter:ParentChildrenAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        loadChildren()

        binding.btnAddChild.setOnClickListener {
            val goToChildRegister = Intent(this, ParentRegisterChildActivity::class.java)
            startActivity(goToChildRegister)
        }
    }

    private fun loadChildren() {
        var parentID = FirebaseAuth.getInstance().currentUser?.uid
        var childIDArrayList = ArrayList<String>()
        firestore.collection("Users").whereEqualTo("userType", "Child").whereEqualTo("parentID", parentID).get().addOnSuccessListener { results ->
            if (results.size()!=0) {
                for (child in results)
                    childIDArrayList.add(child.id)
                childAdapter = ParentChildrenAdapter(this, childIDArrayList)
                binding.rvViewChildren.adapter = childAdapter
                binding.rvViewChildren.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            }
        }
    }
}