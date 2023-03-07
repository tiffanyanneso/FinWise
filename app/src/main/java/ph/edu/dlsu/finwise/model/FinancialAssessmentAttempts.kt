package ph.edu.dlsu.finwise.model

import com.google.firebase.Timestamp

class FinancialAssessmentAttempts (
    var childID:String?=null,
    var assessmentID:String?=null,
    var dateTaken:Timestamp?=null,
    //number of questions in the assessment that was answered correctly
    var nAnsweredCorrectly:Int?=null,
    //number of questions that were in the assessment
    var nQuestions: Int?=null
){
}