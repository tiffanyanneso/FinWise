package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class EarningActivityModel (
    var activityName:String?=null,
    var targetDate:Timestamp?=null,
    var requiredTime:Int?=null,
    var amount:Float?=null,
    var childID:String?=null,
    var savingActivityID:String?=null,
    var status:String?=null,
    //field to identify if the earning activity was made from pfm of goal
    var depositTo:String?=null,
    var dateAdded:Timestamp?=null,
    var dateCompleted: Timestamp?=null,
    var paymentType: String?=null,
    //date the money was sent to the kid
    var dateSent:Timestamp?=null
    ) {
}