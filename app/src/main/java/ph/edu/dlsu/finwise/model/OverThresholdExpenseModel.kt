package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class OverThresholdExpenseModel(
    var transactionID:String?=null,
    var childID:String?=null,
    var dateRecorded:Timestamp?=null
) {
}