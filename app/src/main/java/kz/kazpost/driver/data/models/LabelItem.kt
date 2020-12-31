package kz.kazpost.driver.data.models

data class LabelItem (
    val labelListId: String,
    val fromDepartment: String,
    val toDepartment: String,
    val labelType: String,
    val name: String,
    val hasAct: Boolean
)