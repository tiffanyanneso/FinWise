package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.EarningChoreRequestAdapter
import ph.edu.dlsu.finwise.adapter.EarningCompletedAdapter
import ph.edu.dlsu.finwise.adapter.EarningOverdueAdapter
import ph.edu.dlsu.finwise.adapter.EarningPendingConfirmationAdapter
import ph.edu.dlsu.finwise.databinding.DialogChoreRequestBinding
import ph.edu.dlsu.finwise.databinding.FragmentEarningCompletedBinding
import ph.edu.dlsu.finwise.databinding.FragmentEarningPendingConfirmationBinding
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.NewEarningActivity

class EarningChoreRequestFragment : Fragment() {

    private lateinit var binding: FragmentEarningPendingConfirmationBinding
    private lateinit var earningRequestAdapter: EarningChoreRequestAdapter

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
        binding = FragmentEarningPendingConfirmationBinding.inflate(inflater, container, false)
        return binding .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getChoreRequests()
    }

    private fun getChoreRequests() {
        var earningRequests = ArrayList<String>()
        firestore.collection("EarningActivities").whereEqualTo("childID", childID).whereEqualTo("status", "Request").get().addOnSuccessListener { results ->
            for (earning in results)
                earningRequests.add(earning.id)

            if (isAdded) {
                if (!earningRequests.isEmpty())
                    loadRecyclerView(earningRequests)
                else
                    emptyList()
                binding.rvViewActivitiesCompleted.visibility = View.VISIBLE
                binding.loadingItems.stopShimmer()
                binding.loadingItems.visibility = View.GONE
            }
        }
    }

    private fun loadRecyclerView(earningPendingArrayList: ArrayList<String>) {
        earningRequestAdapter = EarningChoreRequestAdapter(requireActivity().applicationContext, earningPendingArrayList, object:EarningChoreRequestAdapter.ChoreClick {
            override fun clickRequest(earningID:String, childID:String) {
                //only show dialog to approve chore if the user is a parent
                checkUserAndAction(earningID)
            }
        })
        binding.rvViewActivitiesCompleted.adapter = earningRequestAdapter
        binding.rvViewActivitiesCompleted.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
        earningRequestAdapter.notifyDataSetChanged()
    }

    private fun checkUserAndAction(earningID:String) {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var userType = it.toObject<Users>()!!.userType
            if (userType == "Parent") {
                val dialogBinding= DialogChoreRequestBinding.inflate(layoutInflater)
                val dialog= Dialog(requireContext())
                dialog.setContentView(dialogBinding.root)
                dialog.window!!.setLayout(950, 900)
                dialogBinding.btnApprove.setOnClickListener {
                    val newChore = Intent (context, NewEarningActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("earningActivityID", earningID)
                    bundle.putString("childID", childID)
                    newChore.putExtras(bundle)
                    startActivity(newChore)
                    dialog.dismiss()
                }

                dialogBinding.btnDecline.setOnClickListener {
                    firestore.collection("EarningActivities").document(earningID).delete()
                    dialog.dismiss()
                }
                dialog.show()
            }
        }

    }

    private fun emptyList() {
        binding.rvViewActivitiesCompleted.visibility = View.GONE
        binding.layoutEmptyActivity.visibility = View.VISIBLE
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var userType = it.toObject<Users>()?.userType
            if (userType == "Parent")
                binding.tvEmptyListMessage.text = "There are currently no chore requests from your child."
            else if (userType == "Child")
                binding.tvEmptyListMessage.text = "Create a chore request to see them here."
        }
    }

}