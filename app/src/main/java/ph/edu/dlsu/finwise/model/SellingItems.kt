package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class SellingItems (
    var itemName:String?=null,
    var amount:Float?=null,
    var date:Timestamp?=null,
    var childID:String?=null,
    var savingActivityID:String?=null,
    var depositTo:String?=null
        ) {
}