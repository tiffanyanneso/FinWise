package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class AssessmentDetails(
    var assessmentName:String?=null,
    var assessmentCategory:String?=null,
    var assessmentType:String?=null,
    var description:String?=null,
    var createdOn:Timestamp?=null,
    var createdBy:String?=null,
    var nTakes:Int?=null,
    var nQuestionsInAssessment:Int?=null
) { }