package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class BudgetItem (
    var budgetItemName:String?=null,
    var financialActivityID: String?=null,
    var amount:Float?=null,
    //var date:Timestamp?=null,
    //number of times the budget was updated
    //var nUpdate: Int?=null,
    var status:String?=null,
    var createdBy:String?=null) {
}