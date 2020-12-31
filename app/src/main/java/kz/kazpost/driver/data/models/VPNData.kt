package kz.kazpost.driver.data.models

data class VPNData (
    val transportListId: String?,
    val status: String?,
    val index: Int?,
    val fromDep: String?,
    val nextDep: String?,
    val road: List<Road>?,
    val result: String,
    val resultInfo: String?,
    val state: String,
    val message: String?
)