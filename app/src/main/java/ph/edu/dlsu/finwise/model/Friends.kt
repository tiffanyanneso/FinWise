package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class Friends(
    //userID of the child who sent the request
    var senderID:String?=null,
    //userID of the child to whom the request was sent to
    var receiverID:String?=null,
    //accepted, pending, rejected
    var status:String?=null,
    var dateSent:Timestamp?=null
) {
}