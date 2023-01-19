package ph.edu.dlsu.finwise.model

class Questions(
    var category:String?=null,
    var question:String?=null,
    var answerAccracy:Float?=null,
    var dateCreated:String?=null,
    var dateModified:String?=null,
    var createdBy:String?=null,
    var modifiedBy:String?=null,
    var isUsed:Boolean?=null
) {}