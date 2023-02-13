package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class AssessmentQuestions(
    var assessmentID:String?=null,
    var question:String?=null,
    var answerAccuracy:Float?=null,
    var dateCreated:Timestamp?=null,
    var dateModified:Timestamp?=null,
    var createdBy:String?=null,
    var modifiedBy:String?=null,
    var isUsed:Boolean?=null
) {}