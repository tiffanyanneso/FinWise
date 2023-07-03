package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityReconcileBinding
import ph.edu.dlsu.finwise.databinding.DialogConfirmReconcillationBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningMenuActivity
import java.text.DecimalFormat

class ReconcileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReconcileBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    private lateinit var childID:String
    private lateinit var cashWalletID:String
    private lateinit var mayaWalletID:String

    private var updatedCashBalance = 0.00F
    private var updatedMayaBalance = 0.00F
    private var updatedBalance = 0.00F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReconcileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        loadBackButton()
        var bundle = intent.extras!!
        childID = bundle.getString("childID").toString()

        CoroutineScope(Dispatchers.Main).launch {
            getCurrentBalance()
        }

        supportActionBar?.hide()
        val bundleNavBar = Bundle()
        bundleNavBar.putString("childID", childID)
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance, bundleNavBar)

        binding.btnConfirm.setOnClickListener {
            if (isFilledUp())
                showConfirmationDialog()
        }

        binding.etUpdatedCash.doAfterTextChanged { editable ->
            computeUpdatedBalance()
        }

        binding.etUpdatedMaya.doAfterTextChanged { editable ->
            computeUpdatedBalance()
        }

        binding.btnCancel.setOnClickListener{
            goToParentPFM()
        }
    }

    private fun computeUpdatedBalance() {
        updatedBalance = 0.0F

        if (binding.etUpdatedCash.text.toString().isNotEmpty()) {
            updatedBalance += binding.etUpdatedCash.text.toString().toFloat()
            updatedCashBalance = binding.etUpdatedCash.text.toString().toFloat()
        }
        if (binding.etUpdatedMaya.text.toString().isNotEmpty()) {
            updatedBalance += binding.etUpdatedMaya.text.toString().toFloat()
            updatedMayaBalance = binding.etUpdatedMaya.text.toString().toFloat()
        }

        binding.tVUpdatedTotal.text = "₱ " + DecimalFormat("#,##0.00").format(updatedBalance)
    }

    private suspend fun getCurrentBalance() {
        var totalBalance = 0.0
        var wallets = firestore.collection("ChildWallet").whereEqualTo("childID", childID).get().await()
        for (wallet in wallets) {
            var walletObject = wallet.toObject<ChildWallet>()
            if (walletObject.type == "Cash") {
                binding.tvCashBalance.text = "₱ " + DecimalFormat("#,##0.00").format(walletObject.currentBalance)
                cashWalletID = wallet.id
            }
            else if (walletObject.type == "Maya") {
                binding.tvMayaBalance.text = "₱ " + DecimalFormat("#,##0.00").format(walletObject.currentBalance)
                mayaWalletID = wallet.id
            }

            totalBalance += walletObject.currentBalance!!
        }

        binding.tvCurrentBalanceOfChild.text = "₱ " + DecimalFormat("#,##0.00").format(totalBalance)
        binding.layoutLoading.visibility = View.GONE
        binding.mainLayout.visibility = View.VISIBLE
    }

    private fun showConfirmationDialog() {
        var dialogBinding= DialogConfirmReconcillationBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialogBinding.tvUpdatedBalanceOfChild.text = "₱ " + DecimalFormat("#,##0.00").format(updatedBalance)
        dialogBinding.tvUpdatedCashBalance.text = "₱ " + DecimalFormat("#,##0.00").format(updatedCashBalance)
        dialogBinding.tvUpdatedMayaBalance.text = "₱ " + DecimalFormat("#,##0.00").format(updatedMayaBalance)

        dialog.window!!.setLayout(1000, 1200)

        dialogBinding.btnConfirm.setOnClickListener {
            dialog.dismiss()
            updateBalance()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {}

        dialog.show()
    }

    private fun updateBalance() {
        firestore.collection("ChildWallet").document(cashWalletID).update("currentBalance",updatedCashBalance)
        firestore.collection("ChildWallet").document(mayaWalletID).update("currentBalance",updatedMayaBalance)

        Toast.makeText(this, "Balance Successfully Updated", Toast.LENGTH_SHORT).show()
        goToParentPFM()
    }

    private fun isFilledUp() : Boolean {
        var valid = true
        if (binding.etUpdatedCash.text.toString().isEmpty()) {
            valid = false
            binding.cashContainer.helperText = "Input a cash balance"
        } else
            binding.cashContainer.helperText = ""


        if (binding.etUpdatedMaya.text.toString().isEmpty()) {
            valid = false
            binding.mayaContainer.helperText = "Input a maya balance"
        } else
            binding.mayaContainer.helperText = ""

        return valid
    }

    private fun goToParentPFM() {
        var parentPfm = Intent(this, ParentFinancialManagementActivity::class.java)
        var bundle = Bundle()
        bundle.putString("childID", childID)
        parentPfm.putExtras(bundle)
        startActivity(parentPfm)
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
           onBackPressed()
        }
    }
}