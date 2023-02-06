package ph.edu.dlsu.finwise.model

class AssessmentQuestions(
    var assessmentID:String?=null,
    var question:String?=null,
    var answerAccuracy:Float?=null,
    var dateCreated:String?=null,
    var dateModified:String?=null,
    var createdBy:String?=null,
    var modifiedBy:String?=null,
    var isUsed:Boolean?=null
) {}