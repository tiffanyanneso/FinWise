package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class BudgetExpense(
    var budgetCategoryID:String?=null,
    var expenseName:String?=null,
    var amount: Float?=null,
    var date:Timestamp?=null
) {
}