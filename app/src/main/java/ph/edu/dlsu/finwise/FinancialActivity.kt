package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityFinancialBinding
import ph.edu.dlsu.finwise.adapter.ChildGoalAdapter
import ph.edu.dlsu.finwise.financialActivitiesModule.ChildNewGoal
import ph.edu.dlsu.finwise.model.FinancialGoals

class FinancialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialBinding
    private lateinit var goalAdapter: ChildGoalAdapter

    private var firestore = Firebase.firestore

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getGoals()

        binding.btnNewGoal.setOnClickListener {
            context=this
            var goToNewGoal = Intent(context, ChildNewGoal::class.java)
            context.startActivity(goToNewGoal)
        }
    }

    private fun getGoals() {
        var goalArrayList = ArrayList<FinancialGoals>()

        //TODO:change to get goals of current user
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        //firestore.collection("Internships").whereEqualTo("companyID", currentUser).get().addOnSuccessListener{ documents ->

        firestore.collection("FinancialGoals").get().addOnSuccessListener{ documents ->
            for (goalSnapshot in documents) {
                //creating the object from list retrieved in db
                val goal = goalSnapshot.toObject<FinancialGoals>()
                goalArrayList.add(goal!!)
            }

            binding.rvViewGoals.setLayoutManager(LinearLayoutManager(applicationContext))
            goalAdapter = ChildGoalAdapter(applicationContext, goalArrayList)
            binding.rvViewGoals.setAdapter(goalAdapter)
        }
    }
}