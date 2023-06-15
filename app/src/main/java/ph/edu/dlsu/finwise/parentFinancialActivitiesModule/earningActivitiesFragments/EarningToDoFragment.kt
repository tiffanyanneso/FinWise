package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.adapter.EarningCompletedAdapter
import ph.edu.dlsu.finwise.adapter.EarningOverdueAdapter
import ph.edu.dlsu.finwise.adapter.EarningToDoAdapter
import ph.edu.dlsu.finwise.databinding.FragmentEarningCompletedBinding
import ph.edu.dlsu.finwise.databinding.FragmentEarningToDoBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.Users
import java.util.*
import kotlin.collections.ArrayList

class EarningToDoFragment : Fragment() {

    private lateinit var binding:FragmentEarningToDoBinding
    private lateinit var earningToDoAdapter: EarningToDoAdapter
    private lateinit var childID:String

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var bundle = arguments
            childID = bundle?.getString("childID").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEarningToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentUserType()
        getToDoEarning()
    }

    private fun getToDoEarning() {
        var earningToDoArrayList = ArrayList<String>()
        firestore.collection("EarningActivities").whereEqualTo("childID", childID).whereEqualTo("status", "Ongoing").get().addOnSuccessListener { results ->
            var dateToday = Date()
            for (earning in results){
                var earningObject = earning.toObject<EarningActivityModel>()
                if (dateToday.before(earningObject.targetDate!!.toDate()))
                    earningToDoArrayList.add(earning.id)
            }

            if (!earningToDoArrayList.isEmpty())
                loadRecyclerView(earningToDoArrayList)
            else
                emptyList()

            binding.rvViewActivitiesToDo.visibility = View.VISIBLE
            binding.loadingItems.stopShimmer()
            binding.loadingItems.visibility = View.GONE
        }
    }

    private fun loadRecyclerView(earningToDoArrayList: ArrayList<String>) {
        earningToDoAdapter = EarningToDoAdapter(requireActivity().applicationContext, earningToDoArrayList)
        binding.rvViewActivitiesToDo.adapter = earningToDoAdapter
        binding.rvViewActivitiesToDo.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
        earningToDoAdapter.notifyDataSetChanged()
    }

    private fun emptyList() {
        binding.rvViewActivitiesToDo.visibility = View.GONE
        binding.layoutEmptyActivity.visibility = View.VISIBLE
    }

    private fun getCurrentUserType() {
      firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
          var userType = it.toObject<Users>()?.userType!!
          if (userType == "Parent") {
              binding.layoutPictureReminder.visibility = View.GONE
              binding.tvEmptyListMessage.text =
                  "Your child doesn't have any chores now.\nCreate one to help them start earning!"
          }
          else if (userType == "Child") {
              binding.layoutPictureReminder.visibility = View.VISIBLE
              binding.tvEmptyListMessage.text =
                  "There are no chores at the moment.\nAsk your parent to create one for you"
          }
      }

    }
}