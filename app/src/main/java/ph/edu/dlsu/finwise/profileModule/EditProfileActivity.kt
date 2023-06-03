package ph.edu.dlsu.finwise.profileModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firestore.v1.FirestoreGrpc.FirestoreBlockingStub
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityEditProfileBinding
import ph.edu.dlsu.finwise.model.Users
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Color
import android.graphics.PorterDuff

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var context: Context
    lateinit var birthday: Date

    private var isColorChanged = false // Flag to track if color is changed

    private val colors = arrayOf(
        Color.BLACK,
        Color.BLUE,
        Color.RED,
        Color.parseColor("#058B47"), // Dark Green color
        Color.parseColor("#FFA500"), // Orange color
        Color.parseColor("#E75480") // Pink color
    )

    private var currentColorIndex = 0
    private var selectedColor: Int = Color.BLACK
    private lateinit var sharedPrefs: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        // Initialize SharedPreferences
        sharedPrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)

        // Check if the color is already stored in SharedPreferences
        if (!sharedPrefs.contains("color")) {
            // If color is not found, it means it's a new account, so set the default color to black and save it in SharedPreferences
            val editor = sharedPrefs.edit()
            editor.putInt("color", Color.BLACK)
            editor.apply()
        }

        // Retrieve the existing color value from SharedPreferences
        selectedColor = sharedPrefs.getInt("color", Color.BLACK)

        // Apply the retrieved color to the circularImageView
        binding.circularImageView.setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)


        binding.changeColorButton.setOnClickListener {
            changeColor()
        }

        getProfileData()

        binding.etBirthday.setOnClickListener{
            showCalendar()
        }

        binding.btnSave.setOnClickListener {
            updateProfile()
            isColorChanged = true // Set the flag to true when the Save button is clicked
            var goToProfile = Intent(this, ProfileActivity::class.java)
            context.startActivity(goToProfile)
        }

        binding.btnCancel.setOnClickListener {
            var goToProfile = Intent(this, ProfileActivity::class.java)
            context.startActivity(goToProfile)
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_profile)

    }

    override fun onBackPressed() {
        if (isColorChanged) {
            resetColorAndNavigateBack()
        } else {
            super.onBackPressed()
        }
    }
    private fun resetColorAndNavigateBack() {
        // Reset the color to black
        selectedColor = Color.BLACK
        binding.circularImageView.setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)

        // Store the color value in SharedPreferences
        val editor = sharedPrefs.edit()
        editor.putInt("color", selectedColor)
        editor.apply()

        // Navigate back to the previous activity
        super.onBackPressed()
    }
    fun getProfileData() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener { documentSnapshot ->
            var child = documentSnapshot.toObject<Users>()
            if (child?.firstName != null)
                binding.etFirstName.setText(child?.firstName.toString())
            if (child?.lastName != null)
                binding.etLastName.setText(child?.lastName.toString())
            if (child?.birthday != null)
                binding.etBirthday.setText(SimpleDateFormat("MM/dd/yyyy").format(child?.birthday!!.toDate()))
            if (child?.number!= null)
                binding.etContactNumber.setText(child.number.toString())

            // Retrieve the color value from the "ColorPreferences" collection
            val colorPreferencesRef = firestore.collection("ColorPreferences").document(currentUser)
            colorPreferencesRef.get().addOnSuccessListener { documentSnapshot ->
                val colorData = documentSnapshot.data
                if (colorData != null && colorData.containsKey("color")) {
                    val color = colorData["color"] as Long
                    selectedColor = color.toInt()
                    binding.circularImageView.setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
                }
            }
        }
    }
    private fun changeColor() {
        selectedColor = colors[(currentColorIndex + 1) % colors.size]
        binding.circularImageView.setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
        currentColorIndex = (currentColorIndex + 1) % colors.size

        // Store the color value in SharedPreferences
        val editor = sharedPrefs.edit()
        editor.putInt("color", selectedColor)
        editor.apply()
    }


    private fun updateProfile() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val birthday = SimpleDateFormat("MM/dd/yyyy").parse(binding.etBirthday.text.toString())
        val number = binding.etContactNumber.text.toString()


        // Store the color value in the separate "ColorPreferences" collection
        val colorPreferencesRef = firestore.collection("ColorPreferences").document(currentUser)
        colorPreferencesRef.set(mapOf("color" to selectedColor), SetOptions.merge())

        firestore.collection("Users").document(currentUser).update("firstName", firstName, "lastName", lastName, "birthday", birthday, "number", number).addOnSuccessListener {
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT)


            // Store the color value in shared preferences
            val sharedPrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putInt("color", selectedColor)
            editor.apply()
            sharedPrefs.edit().putInt("color", selectedColor).apply()
            finish()
            val intent = Intent (this, ProfileActivity::class.java)
            startActivity (intent)
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