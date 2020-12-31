package kz.kazpost.driver.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class StationData (
    val transportListId: String,
    val status: String,
    val index: Int,
    val fromDep: String,
    val nextDep: String,
    val road: List<Road>,
    val labels: Labels,
    @field:SerializedName("labelDataList")
    val labelDataList: MutableList<LabelDataListItem>? = null,
    val labelItems: List<LabelItem>?,
    val mailItems: List<MailItem>?,
    val items: List<String>?,
    val result: String
)

@Parcelize
data class LabelDataListItem(
    @field:SerializedName("labelType")
    val labelType: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("count")
    val count: Int
): Parcelable