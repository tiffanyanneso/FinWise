package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
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
    private var goalIDArrayList = ArrayList<String>()
    private lateinit var status: String


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
        goToNewGoal()
        getAllGoals()
        sortTransactions()
    }
    private fun sortTransactions() {
        val sortSpinner = binding.spinnerStatus
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                status = sortSpinner.selectedItem.toString()

                if (status == "--Status--") {
                    goalIDArrayList.clear()
                    getAllGoals()
                }
                else {
                    goalIDArrayList.clear()
                    firestore.collection("FinancialGoals")
                        .whereEqualTo("status", status).get().addOnSuccessListener { documents ->
                            for (goalSnapshot in documents) {
                                //creating the object from list retrieved in db
                                val goalID = goalSnapshot.id
                                goalIDArrayList.add(goalID)
                            }
                            loadRecyclerView(goalIDArrayList)
                        }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
    }

    private fun getAllGoals() {

        //TODO:change to get transactions of current user
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        //firestore.collection("Transactions").whereEqualTo("companyID", currentUser).get().addOnSuccessListener{ documents ->

        firestore.collection("FinancialGoals").get().addOnSuccessListener { documents ->
            for (goalSnapshot in documents) {
                //creating the object from list retrieved in db
                val goalID = goalSnapshot.id
                goalIDArrayList.add(goalID)
            }
            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalAdapter = ChildGoalAdapter(this, goalIDArrayList)
        binding.rvViewGoals.adapter = goalAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        goalAdapter.notifyDataSetChanged()
    }

    private fun goToNewGoal() {
        binding.btnNewGoal.setOnClickListener {
            val goToNewGoal = Intent(this, ChildNewGoal::class.java)
            context.startActivity(goToNewGoal)
        }
    }

}