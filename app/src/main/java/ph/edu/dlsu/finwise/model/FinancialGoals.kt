package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp
import java.time.LocalDate

class FinancialGoals(
    var childID:String?=null,
    var goalName:String?=null,
    var dateCreated: Timestamp?=null,
    var createdBy:String?=null,
    var targetDate:Timestamp?=null,
    var goalLength:String?=null,
    var targetAmount:Float?=null,
    var financialActivity:String?=null,
    var status:String?=null,
    var goalIsForSelf:Boolean?=null,
    var dateCompleted:Timestamp?=null
){}