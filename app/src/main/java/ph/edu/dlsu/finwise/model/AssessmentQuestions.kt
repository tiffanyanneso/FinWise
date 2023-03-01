package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class AssessmentQuestions(
    var assessmentID:String?=null,
    var question:String?=null,
    var difficulty:String?=null,
    var dateCreated:Timestamp?=null,
    var createdBy:String?=null,
    @field:JvmField
    var isUsed:Boolean?=null,
    //values below will be used to compute correctness %
    //number of times the question appeared in assessments
    var nAssessments:Int?=null,
    //number of times the question was answered correctly
    var nAnsweredCorrectly:Int?=null
) {}