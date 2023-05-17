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

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var context: Context
    lateinit var birthday: Date


    private val colors = arrayOf(
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.CYAN,
        Color.MAGENTA
    )

    private var currentColorIndex = 0

    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(binding.root)
        context= this

        binding.changeColorButton.setOnClickListener {
            changeColor()
        }

        getProfileData()

        binding.etBirthday.setOnClickListener{
            showCalendar()
        }

        binding.btnSave.setOnClickListener{
            updateProfile()
            var goToProfile = Intent(this, ProfileActivity::class.java)
            context.startActivity(goToProfile)
        }

        binding.btnCancel.setOnClickListener {
            var goToProfile = Intent(this, ProfileActivity::class.java)
            context.startActivity(goToProfile)
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToProfile = Intent(applicationContext, ProfileActivity::class.java)
            this.startActivity(goToProfile)
        }

        // Initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_profile)

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
        }
    }
    private fun changeColor() {
        binding.circularImageView.setColorFilter(colors[currentColorIndex])
        currentColorIndex = (currentColorIndex + 1) % colors.size
    }
/*
    private fun createCircleDrawable(color: Int): Drawable {
        val shapeDrawable = ShapeDrawable(OvalShape())
        shapeDrawable.paint.color = color
        val strokeColor = Color.BLACK
        val strokeWidth = 8
        shapeDrawable.paint.style = Paint.Style.STROKE
        shapeDrawable.paint.strokeWidth = strokeWidth.toFloat()
        shapeDrawable.paint.setShadowLayer(8f, 0f, 0f, strokeColor)
        shapeDrawable.paint.setShadowLayer(8f, 0f, 0f, color)
        val layerDrawable = LayerDrawable(arrayOf(shapeDrawable))
        layerDrawable.setLayerInset(0, strokeWidth, strokeWidth, strokeWidth, strokeWidth)
        return layerDrawable
    }
*/

    private fun updateProfile() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val birthday = SimpleDateFormat("MM/dd/yyyy").parse(binding.etBirthday.text.toString())
        val number = binding.etContactNumber.text.toString()

        firestore.collection("Users").document(currentUser).update("firstName", firstName, "lastName", lastName, "birthday", birthday, "number", number).addOnSuccessListener {
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT)
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