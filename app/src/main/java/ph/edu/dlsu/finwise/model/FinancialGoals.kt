package ph.edu.dlsu.finwise.model

class FinancialGoals(
    var childID:String?=null,
    var goalName:String?=null,
    var dateCreated:String?=null,
    var createdBy:String?=null,
    var targetDate:String?=null,
    var targetAmount:Float?=null,
    var currentAmount:Float?=null,
    var financialActivity:String?=null,
    var decisionMakingActivities:ArrayList<DecisionMakingActivities>?=null,
    var lastUpdated:String?=null,
    var status:String?=null
){}