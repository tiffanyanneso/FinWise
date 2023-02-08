package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class Transactions(
    var childID:String?=null,
    var transactionType:String?=null,
    var transactionName:String?=null,
    var amount:Float?=null,
    var category:String?=null,
    var financialGoalID:String?=null,
    var goal:String?=null,
    //var decisionMakingActivityID:String?=null,
    var date:Timestamp?=null
){}