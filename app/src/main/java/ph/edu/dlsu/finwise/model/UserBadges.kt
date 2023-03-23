package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class UserBadges(
    var childID:String?=null,
    var badgeName:String?=null,
    var budgetType:String?=null,
    var badgeDescription:String?=null,
    var badgeScore: Double?=null,
    var dateEarned:String?=null
) {
}