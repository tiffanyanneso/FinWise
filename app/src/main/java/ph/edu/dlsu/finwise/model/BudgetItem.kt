package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class BudgetItem (
    var budgetItemName:String?=null,
    var budgetItemNameOther:String?=null,
    var financialActivityID: String?=null,
    var amount:Float?=null,
    //var date:Timestamp?=null,
    //number of times the budget was updated
    //var nUpdate: Int?=null,
    var status:String?=null,
    //variable to track if the budget item was added before or after the done setting budget was clicked
    var whenAdded:String?=null,
    var createdBy:String?=null) {
}