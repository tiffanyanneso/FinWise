package ph.edu.dlsu.finwise

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import ph.edu.dlsu.finwise.databinding.ActivityTabbedExampleBinding

class TabbedActivityExample : AppCompatActivity() {

    private lateinit var binding: ActivityTabbedExampleBinding

    data class FragmentData(val title:String, val hex: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTabbedExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentDataList = listOf<FragmentData>(
            FragmentData("Red", "#FF0000"),
            FragmentData("Green", "#00FF00"),
            FragmentData("Blue", "#0000FF")
        )

        val tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = fragmentDataList[position].title

        }
        binding.viewPager.adapter = MyAdapter(this, fragmentDataList)
        tabLayoutMediator.attach()
    }

    class MyAdapter(activity: AppCompatActivity, private val fragmentDataList: List<FragmentData>): FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return fragmentDataList.size
        }

        override fun createFragment(position: Int): Fragment {
            return ViewPagerFragment().apply {
                arguments = Bundle().apply {
                    putString("hex_color", fragmentDataList[position].hex)
                }
            }
        }

    }

    class ViewPagerFragment: Fragment(R.layout.fragment_view_pager) {
        private val hexColoString: String by lazy {
            requireArguments().getString("hex_color") ?: "#E4E4E4"
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            view.findViewById<ConstraintLayout>(R.id.root).setBackgroundColor(
                Color.parseColor(hexColoString)
            )
        }

    }
}