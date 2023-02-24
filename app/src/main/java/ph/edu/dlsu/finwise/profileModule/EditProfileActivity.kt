package ph.edu.dlsu.finwise.profileModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityEditProfileBinding
import ph.edu.dlsu.finwise.model.ChildUser
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context
    lateinit var birthday: Date

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        getProfileData()

        binding.etBirthday.setOnClickListener{
            showCalendar()
        }

        binding.btnSave.setOnClickListener{
            updateInternProfile()
            var goToProfile = Intent(this, ProfileActivity::class.java)
            context.startActivity(goToProfile)
        }

        binding.btnCancel.setOnClickListener {
            var goToProfile = Intent(this, ProfileActivity::class.java)
            context.startActivity(goToProfile)
        }

    }

    fun getProfileData() {
        //val currentUser:String = FirebaseAuth.getInstance().currentUser!!.uid
        val currentUser = "eWZNOIb9qEf8kVNdvdRzKt4AYrA2"

        firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener { documentSnapshot ->
            var child = documentSnapshot.toObject<ChildUser>()
            if (child?.firstName != null)
                binding.etFirstName.setText(child?.firstName.toString())
            if (child?.lastName != null && child?.lastName != null)
                binding.etLastName.setText(child?.lastName.toString())
            if (child?.birthday != null && child?.birthday != null) {
                binding.etBirthday.setText(child?.birthday.toString())
            }
        }
    }

    private fun updateInternProfile() {
        //TODO update profile picture
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val birthday = binding.etBirthday.toString()
        //val currentUser:String = FirebaseAuth.getInstance().currentUser!!.uid
        val currentUser:String = "JoCGIUSVMWTQ2IB7Rf41ropAv3S2"

        val child = mapOf<String, String>(
            "firstName" to firstName,
            "lastName" to lastName,
            "birthday" to birthday
        )

        firestore.collection("ChildUser").document(currentUser).set(child, SetOptions.merge()).addOnSuccessListener {
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT)
            val intent = Intent (this, ProfileActivity::class.java)
            startActivity (intent)
            finish()
        }.addOnFailureListener{
            Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etBirthday.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            birthday = SimpleDateFormat("MM/dd/yyyy").parse((mMonth + 1).toString() + "/" +
                    mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
}