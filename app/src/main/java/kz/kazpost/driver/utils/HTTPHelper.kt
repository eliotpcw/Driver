package kz.kazpost.driver.utils

import kz.kazpost.driver.data.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class HTTPHelper {

    interface DataForStation {
        @GET("driver-items/getDataForStation")
        fun get(@Query("tid") tid: String,
                @Query("index") index: Int): Call<StationData>
    }

    interface VerifyItems {
        @Headers("Content-Type: application/json")
        @POST("driver-items/verifyItemsForDriver")
        fun post(@Body body: RequestItems): Call<ResponseBody>
    }

    interface SendItems {
        @Headers("Content-Type: application/json")
        @POST("driver-items/sendInfoForDriver")
        fun post(@Body body: RequestItems): Call<ResponseBody>
    }

    companion object Factory {
        private val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(UnsafeOkHttpClient.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        fun getStationData() : DataForStation {
            return retrofit.create(DataForStation::class.java)
        }
        fun postVerifyItems() : VerifyItems {
            return retrofit.create(VerifyItems::class.java)
        }
        fun postSendItems() : SendItems {
            return retrofit.create(SendItems::class.java)
        }
    }

}