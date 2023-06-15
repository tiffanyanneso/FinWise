package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityParentEditProfileBinding
import ph.edu.dlsu.finwise.model.Users
import java.text.SimpleDateFormat

class ParentEditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentEditProfileBinding
    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var context: Context

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentEditProfileBinding.inflate(layoutInflater)
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

        NavbarParentFirst(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_first_profile)

        binding.btnSave.setOnClickListener{
            updateProfile()
            isColorChanged = true // Set the flag to true when the Save button is clicked
        }

        binding.btnCancel.setOnClickListener {
            var goToProfile = Intent(this, ParentProfileActivity::class.java)
            context.startActivity(goToProfile)
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToProfile = Intent(applicationContext, ParentProfileActivity::class.java)
            this.startActivity(goToProfile)
        }
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
            var parent = documentSnapshot.toObject<Users>()
            if (parent?.firstName != null)
                binding.etFirstName.setText(parent?.firstName.toString())
            if (parent?.lastName != null)
                binding.etLastName.setText(parent?.lastName.toString())
            if (parent?.number!= null)
                binding.etContactNumber.setText(parent.number.toString())

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
        val number = binding.etContactNumber.text.toString()
        binding.containerFirstName.helperText = ""
        binding.containerLastName.helperText = ""
        binding.containerNumber.helperText = ""

        if (firstName.isNotEmpty() && lastName.isNotEmpty() && number.isNotEmpty()) {
            firestore.collection("Users").document(currentUser)
                .update("firstName", firstName, "lastName", lastName, "number", number)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT)

                    // Store the color value in shared preferences
                    val sharedPrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.putInt("color", selectedColor)
                    editor.apply()
                    sharedPrefs.edit().putInt("color", selectedColor).apply()
                    finish()

                    val intent = Intent(this, ParentProfileActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT)
            }
        } else {
            if (firstName.isEmpty())
                binding.containerFirstName.helperText = "Input first name"
            if (lastName.isEmpty())
                binding.containerLastName.helperText = "Input last name"
            if (number.isEmpty())
                binding.containerNumber.helperText = "Input contact number"
        }
    }
}