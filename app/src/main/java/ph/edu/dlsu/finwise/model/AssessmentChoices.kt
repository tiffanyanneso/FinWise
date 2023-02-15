package ph.edu.dlsu.finwise.model

class AssessmentChoices (
    var questionID:String?=null,
    var choice:String?=null,
    @field:JvmField
    var isCorrect: Boolean?=null)  {
}