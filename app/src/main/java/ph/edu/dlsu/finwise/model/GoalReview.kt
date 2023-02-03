package ph.edu.dlsu.finwise.model

class GoalReview (
    var parentID:String?=null,
    var childID:String?=null,
    var financialGoalID:String?=null,
    var specific:Float?=null,
    var measurable:Float?=null,
    var achievable:Float?=null,
    var relevant:Float?=null,
    var timeBound:Float?=null,
    var comment:String?=null,
    var overallRating:Float?=null
) { }