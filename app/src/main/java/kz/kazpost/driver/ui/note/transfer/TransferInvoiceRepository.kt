package kz.kazpost.driver.ui.note.transfer

import kz.kazpost.driver.data.network.ApiCaller
import kz.kazpost.driver.data.network.ApiCallerInterface
import kz.kazpost.driver.data.network.api.IApi

class TransferInvoiceRepository(private val api: IApi) : ApiCallerInterface by ApiCaller {

    suspend fun getTransferItems(
        tid: String,
        index: String,
        toDep: String
    ) = apiCall {
        api.getTransferItems(tid, index, toDep)
    }

}