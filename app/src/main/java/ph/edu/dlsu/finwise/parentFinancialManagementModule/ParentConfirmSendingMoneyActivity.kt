package ph.edu.dlsu.finwise.parentFinancialManagementModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMaya
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityMayaQrConfirmPaymentBinding
import ph.edu.dlsu.finwise.databinding.ActivityParentConfirmSendingMoneyBinding

class ParentConfirmSendingMoneyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParentConfirmSendingMoneyBinding
    private var firestore = Firebase.firestore
    var bundle: Bundle? = null
    lateinit var name : String
    lateinit var category : String
    lateinit var amount : String
    lateinit var merchant : String
    var balance = 0.00f
    lateinit var phone : String
    lateinit var date : String

    // For Pay With PayMaya API: This is instantiating the Pay With PayMaya API.
    private val payWithPayMayaClient = PayWithPayMaya.newBuilder()
        .clientPublicKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.ERROR)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentConfirmSendingMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
        /*setText()
        payMaya()
        cancel()*/

    }
}