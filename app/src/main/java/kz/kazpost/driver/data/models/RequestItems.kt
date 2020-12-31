package kz.kazpost.driver.data.models

import kz.kazpost.driver.data.enums.LabelsModel2

data class RequestItems(
    var login: String,
    var department: String,
    var transportListId: String,
    var index: Int,
    var labels: HashMap<String, Int>,
    var labelsData: MutableList<LabelsModel2>,
    var items: List<String>,
    var camera: Boolean,
    var state: String
)