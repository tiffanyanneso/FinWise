package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class Transactions(
    var userID:String?=null,
    var childID:String?=null,
    var transactionType:String?=null,
    var transactionName:String?=null,
    var amount:Float?=null,
    var category:String?=null,
    var financialActivityID:String?=null,
    var budgetItemID:String?=null,
    var merchant:String? = null,
    //var decisionMakingActivityID:String?=null,
    var date:Timestamp?=null,
    var paymentType:String?=null // cash or maya
){}