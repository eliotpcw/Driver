package kz.kazpost.driver.ui.gosnumber

import kz.kazpost.driver.data.models.StationData
import kz.kazpost.driver.data.network.ApiCaller
import kz.kazpost.driver.data.network.ApiCallerInterface
import kz.kazpost.driver.data.network.RequestResult
import kz.kazpost.driver.data.network.api.IApi
import kz.kazpost.driver.data.models.VPNData

interface GosNumberRepository {
    suspend fun authGosNumber(gosNumber: String?): RequestResult<VPNData>
    suspend fun authRouteNumber(routeNumber: String?): RequestResult<VPNData>
}

class GosNumberRepositoryImpl(
   private val api: IApi
): ApiCallerInterface by ApiCaller, GosNumberRepository{

    override suspend fun authGosNumber(gosNumber: String?) = apiCall {
        api.authGosNumber(gosNumber)
    }

    override suspend fun authRouteNumber(routeNumber: String?) = apiCall {
        api.authRouteNumber(routeNumber)
    }
}