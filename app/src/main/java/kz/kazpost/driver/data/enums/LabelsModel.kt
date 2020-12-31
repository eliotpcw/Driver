package kz.kazpost.driver.data.enums

data class LabelsModel (
    var name: String,
    var plan: Int,
    var fact: Int,
    var type: String
)

data class LabelsModel2 (
    var name: String,
    var type: String,
    var toDepartamen: String,
    var rpo: String
)