package kz.kazpost.driver.data.models

data class MailItem(
    val mailId: String,
    val mailType:String,
    val name: String,
    val fromDepartment: String,
    val toDepartment: String,
    val hasAct: Boolean
)