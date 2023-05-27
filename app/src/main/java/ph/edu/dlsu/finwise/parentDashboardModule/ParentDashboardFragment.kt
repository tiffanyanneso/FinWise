package ph.edu.dlsu.finwise.parentDashboardModule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ph.edu.dlsu.finwise.databinding.FragmentParentDashboardBinding


class ParentDashboardFragment : Fragment() {

    private lateinit var binding: FragmentParentDashboardBinding
    private var firestore = Firebase.firestore

    private lateinit var childID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = requireArguments()
        childID = bundle.getString("childID").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            println("print child id in fragment " + childID)
            personalFinanceScore()
            financialActivitiesScore()
            assessmentsScore()
        }
    }

    private suspend fun personalFinanceScore() {

    }

    private suspend fun financialActivitiesScore() {

    }

    private suspend fun assessmentsScore() {

    }




}