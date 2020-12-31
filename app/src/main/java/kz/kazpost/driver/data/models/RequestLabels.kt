package kz.kazpost.driver.data.models

data class RequestLabels (
    var login: String,
    var department: String,
    var transportListId: String,
    var index: Int,
    var labels: Labels
)