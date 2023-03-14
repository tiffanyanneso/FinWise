package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class Users (
    var username:String?=null,
    var firstName:String?=null,
    var lastName:String?=null,
    var birthday: Timestamp?=null,
    var parentID:String?=null,
    val number:String?=null,
    var userType:String?=null,
    var lastLogin: Timestamp?=null
    var assessmentPerformance:Double?=null
    ) {
}