package ph.edu.dlsu.finwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import ph.edu.dlsu.finwise.*
import ph.edu.dlsu.finwise.databinding.ActivityMenuBinding


class MenuActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(FinancialGoals())

        binding.bottomNavigationView.setOnItemReselectedListener{

            when(it.itemId){

              R.id.finance -> replaceFragment(PersonalFinance())
                R.id.goal -> replaceFragment(FinancialGoals())
                R.id.dashboard -> replaceFragment(Dashboard())
                R.id.assessment -> replaceFragment(FinancialAssessment())
                R.id.profile -> replaceFragment(Profile())

                else ->{
                }

            }
            true
        }


    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}

