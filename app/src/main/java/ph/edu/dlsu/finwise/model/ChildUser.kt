package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class ChildUser (
    var username:String?=null,
    var firstName:String?=null,
    var lastName:String?=null,
    var birthday: Timestamp?=null,
    var parentID:String?=null
){}