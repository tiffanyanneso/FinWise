package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class ParentUser (
    //val email:String?=null,
    var firstName:String?=null,
    var lastName:String?=null,
    val number:String?=null,
    //val childID:String?=null
    var lastLogin:Timestamp?=null
) {}