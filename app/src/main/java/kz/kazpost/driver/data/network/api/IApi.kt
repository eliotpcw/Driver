package kz.kazpost.driver.data.network.api

import kz.kazpost.driver.data.models.*
import kz.kazpost.driver.test.ResponseTestLabel
import okhttp3.ResponseBody
import retrofit2.http.*

interface IApi {
    @GET("driver-items/getDriverData")
    suspend fun authGosNumber(@Query("login") login: String?): VPNData

    @GET("driver-items/getDriverDataByTransport")
    suspend fun authRouteNumber(@Query("transportListId") transportListId: String?): VPNData

    @Headers("Content-Type: application/json")
    @POST("driver-items/verifyItemsForDriver")
    suspend fun sendAcceptParcel(@Body body: RequestItems): ResponseBody

    @GET("driver-items/getFioByIin")
    suspend fun authWithIIN(@Query("iin") iin: String?): Fio

    @Headers("Content-Type: application/json")
    @POST("security/login")
    suspend fun authWithLgnPwd(@Body authLgnPwdModel: RequestAuthorisation): Auth

    @Headers("Content-Type: application/json")
    @GET("driver-items/getInfoForTListDriver")
    suspend fun getTransferItems(
        @Query("tid") tid: String,
        @Query("index") index: String,
        @Query("toDep") toDep: String
        ): ResponseTransferItems

    @GET("driver-items/getDataForStation")
    suspend fun getTestLabelType(
        @Query("tid") tid: String,
        @Query("index") index: Int
    ): ResponseTestLabel
}