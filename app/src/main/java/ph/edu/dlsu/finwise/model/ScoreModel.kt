package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class ScoreModel (
    var childID:String?=null,
    var score:Float?=null,
    var type:String?=null,
    var dateRecorded:Timestamp?=null
) {
}