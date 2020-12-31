package kz.kazpost.driver.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseTransferItems(

	@field:SerializedName("mailItems")
	val mailItems: List<MailItemsItem?>? = null,

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("labelDataList")
	val labelDataList: MutableList<LabelDataListItem>? = null,

	@field:SerializedName("transportListId")
	val transportListId: String? = null,

	@field:SerializedName("index")
	val index: Int? = null,

	@field:SerializedName("items")
	val items: List<String?>? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("itemCount")
	val itemCount: Int? = null
) : Parcelable

@Parcelize
data class MailItemsItem(

	@field:SerializedName("fromDepartment")
	val fromDepartment: String,

	@field:SerializedName("mailType")
	val mailType: String,

	@field:SerializedName("toDepartment")
	val toDepartment: String,

	@field:SerializedName("rpoId")
	val rpoId: String,

	@field:SerializedName("name")
	val name: String
) : Parcelable
