package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class FinancialActivities(
    var financialGoalID:String?=null,
    var childID:String?=null,
    var financialActivityName:String?=null,
    var status:String?=null,
    var dateStarted:Timestamp?=null,
    var dateCompleted: Timestamp?=null
){}