package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityEarningBinding
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments.EarningCompletedFragment
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments.EarningToDoFragment

class EarningActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEarningBinding

    private lateinit var savingActivityID:String

    private var firestore = Firebase.firestore

    private lateinit var childID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        savingActivityID = bundle.getString("savingActivityID").toString()
        childID = bundle.getString("childID").toString()


        checkUser()
        initializeFragments()

        binding.btnAddEarningActivity.setOnClickListener {
            var newEarning = Intent(this, NewEarningActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("savingActivityID", savingActivityID)
            sendBundle.putString("childID", childID)
            newEarning.putExtras(sendBundle)
            startActivity(newEarning)
        }
    }

    private fun initializeFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        var fragmentBundle = Bundle()
        fragmentBundle.putString("childID", childID)

        var earningToDoFragment = EarningToDoFragment()
        earningToDoFragment.arguments = fragmentBundle

        var earningCompletedFragment = EarningCompletedFragment()
        earningCompletedFragment.arguments = fragmentBundle

        adapter.addFragment(earningToDoFragment,"To-Do")
        adapter.addFragment(earningCompletedFragment,"Completed")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun checkUser() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener {
            //current user is a child
            if (it.exists())
                binding.btnAddEarningActivity.visibility = View.GONE
        }
    }

    class ViewPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm){

        private val mFrgmentList = ArrayList<Fragment>()
        private val mFrgmentTitleList = ArrayList<String>()
        override fun getCount() = mFrgmentList.size
        override fun getItem(position: Int) = mFrgmentList[position]
        override fun getPageTitle(position: Int) = mFrgmentTitleList[position]

        fun addFragment(fragment: Fragment, title:String){
            mFrgmentList.add(fragment)
            mFrgmentTitleList.add(title)
        }
    }
}