package ph.edu.dlsu.finwise.model

class SettingsModel(
    var childID:String?=null,
    var parentID:String?=null,
    var maxAmountActivities:Float?=null,
    var alertAmount:Float?=null,
    var buyingItem:Boolean?=null,
    var planingEvent:Boolean?=null,
    var emergencyFund:Boolean?=null,
    var donatingCharity:Boolean?=null,
    var situationalShopping:Boolean?=null,
    var pfmScore:String?=null,
    var finactScore:String?=null,
    var assessmentScore:String?=null
) {
}