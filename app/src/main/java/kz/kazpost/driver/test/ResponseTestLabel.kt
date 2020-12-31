package kz.kazpost.driver.test

import com.squareup.moshi.Json

data class ResponseTestLabel(
	@Json(name="labelDataList")
	val labelDataList: List<LabelDataListItem>
)

data class LabelDataListItem(
	@Json(name="labelType")
	val labelType: String,
	@Json(name="name")
	val name: String,
	@Json(name="count")
	val count: Int,
	@Json(name="lableType")
	val lableType: String
)
