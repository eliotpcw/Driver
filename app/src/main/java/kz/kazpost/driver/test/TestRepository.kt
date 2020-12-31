package kz.kazpost.driver.test

import kz.kazpost.driver.data.models.RequestAuthorisation
import kz.kazpost.driver.data.network.ApiCaller
import kz.kazpost.driver.data.network.CoroutineCaller
import kz.kazpost.driver.data.network.api.IApi

public class TestRepository(
    private val api: IApi
)
    : CoroutineCaller by ApiCaller {

    suspend fun getLabelType(
        tId: String,
        indexOfStation: Int
    ) = apiCall {
        api.getTestLabelType(tId, indexOfStation)
    }
}