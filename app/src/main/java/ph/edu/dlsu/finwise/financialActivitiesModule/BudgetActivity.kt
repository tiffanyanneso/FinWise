package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityBudgetBinding
import ph.edu.dlsu.finwise.databinding.DialogNewBudgetCategoryBinding

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetBinding

    private var firestore = Firebase.firestore

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNewCategory.setOnClickListener {
            showNewBudgetCategoryDialog()
        }
    }


    private fun showNewBudgetCategoryDialog() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_new_budget_category)

        val btnSave = dialog.findViewById<Button>(R.id.btn_save)
        var btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)


        btnSave.setOnClickListener {
            Toast.makeText(this, "Save category", Toast.LENGTH_SHORT).show()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}