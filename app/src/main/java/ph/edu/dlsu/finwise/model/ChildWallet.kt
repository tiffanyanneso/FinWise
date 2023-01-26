package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class ChildWallet(
    var childID: String?=null,
    var currentBalance:Float?=null,
    var lastUpdated: Timestamp?=null
) {
}