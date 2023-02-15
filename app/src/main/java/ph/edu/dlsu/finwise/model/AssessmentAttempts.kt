package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class AssessmentAttempts (
    var childID:String?=null,
    var assessmentID:String?=null,
    var dateTaken:Timestamp?=null,
    var score:String?=null,
){
}