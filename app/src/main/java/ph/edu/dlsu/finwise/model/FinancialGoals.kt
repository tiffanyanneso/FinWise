package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp
import java.time.LocalDate

class FinancialGoals(
    var childID:String?=null,
    var goalName:String?=null,
    var dateCreated: Timestamp?=null,
    var createdBy:String?=null,
    var targetDate:Timestamp?=null,
    var targetAmount:Float?=null,
    var currentAmount:Float?=null,
    var financialActivity:String?=null,
    var decisionMakingActivities:ArrayList<DecisionMakingActivities>?=null,
    var lastUpdated:Timestamp?=null,
    var status:String?=null
){}