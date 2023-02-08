package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class BudgetCategory (
    var budgetCategory:String?=null,
    var financialActivityID: String?=null,
    var amount:String?=null,
    var date:Timestamp?=null) {
}