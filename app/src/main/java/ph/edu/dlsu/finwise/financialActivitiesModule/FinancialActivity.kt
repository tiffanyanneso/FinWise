package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialBinding
import ph.edu.dlsu.finwise.adapter.ChildGoalAdapter

class FinancialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialBinding
    private lateinit var goalAdapter: ChildGoalAdapter

    private var firestore = Firebase.firestore

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
       supportActionBar?.hide()
       Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        getGoals()

        binding.btnNewGoal.setOnClickListener {
            context=this
            var goToNewGoal = Intent(context, ChildNewGoal::class.java)
            context.startActivity(goToNewGoal)
        }
    }

    private fun getGoals() {
        var goalIDArrayList = ArrayList<String>()

        //TODO:change to get goals of current user
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        //firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).get().addOnSuccessListener{ documents ->

        firestore.collection("FinancialGoals").get().addOnSuccessListener{ documents ->
            for (goalSnapshot in documents) {
                //creating the object from list retrieved in db
                val goalID = goalSnapshot.id
                goalIDArrayList.add(goalID!!)
            }

            binding.rvViewGoals.setLayoutManager(LinearLayoutManager(applicationContext))
            goalAdapter = ChildGoalAdapter(applicationContext, goalIDArrayList)
            binding.rvViewGoals.setAdapter(goalAdapter)
        }
    }
}