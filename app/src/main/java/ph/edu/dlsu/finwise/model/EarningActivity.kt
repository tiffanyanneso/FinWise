package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class EarningActivity (
    var activityName:String?=null,
    var targetDate:Timestamp?=null,
    var requiredTime:Int?=null,
    var amount:Float?=null,
    var childID:String?=null,
    var savingActivityID:String?=null,
    var status:String?=null,
    var dateCompleted: Timestamp?=null
        ) {
}