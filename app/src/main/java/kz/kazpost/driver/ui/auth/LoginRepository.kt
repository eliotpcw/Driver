package kz.kazpost.driver.ui.auth

import kz.kazpost.driver.data.models.RequestAuthorisation
import kz.kazpost.driver.data.network.ApiCaller
import kz.kazpost.driver.data.network.ApiCallerInterface
import kz.kazpost.driver.data.network.CoroutineCaller
import kz.kazpost.driver.data.network.api.IApi

class LoginRepository(
    private val api: IApi
) : ApiCallerInterface by ApiCaller {

    suspend fun makeAuthWithLgnPwd(
        authLgnPwd: RequestAuthorisation
    ) = apiCall {
        api.authWithLgnPwd(authLgnPwd)
    }

    suspend fun makeAuthWithIIN(
        iin: String?
    ) = apiCall {
        api.authWithIIN(iin)
    }
}