package ph.edu.dlsu.finwise.childDashboardModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarParentFirst
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityReconcileBinding
import ph.edu.dlsu.finwise.databinding.DialogConfirmReconcillationBinding
import ph.edu.dlsu.finwise.databinding.DialogPfmParentTipsBinding
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningMenuActivity

class ReconcileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReconcileBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReconcileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        supportActionBar?.hide()
        NavbarParentFirst(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance)

        binding.btnConfirm.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {

        var dialogBinding= DialogConfirmReconcillationBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1500)

        dialogBinding.btnConfirm.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {}

        dialog.show()
    }
}