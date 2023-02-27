package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class GoalRating (
    var parentID:String?=null,
    var childID:String?=null,
    var financialGoalID:String?=null,
    var specific:Float?=null,
    var measurable:Float?=null,
    var achievable:Float?=null,
    var relevant:Float?=null,
    var timeBound:Float?=null,
    var comment:String?=null,
    var overallRating:Float?=null,
    var lastUpdated: Timestamp?=null
) { }