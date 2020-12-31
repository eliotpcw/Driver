package kz.kazpost.driver.ui.note.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kz.kazpost.driver.data.models.ResponseTransferItems
import kz.kazpost.driver.data.models.VPNData
import kz.kazpost.driver.data.network.RequestResult
import kz.kazpost.driver.ui.BaseViewModel
import kz.kazpost.driver.utils.EventWrapper

class TransferInvoiceViewModel(
    private val repository: TransferInvoiceRepository
): BaseViewModel(){


    private val _responseTransferData: MutableLiveData<ResponseTransferItems> = MutableLiveData()
    internal val responseTransferData: LiveData<ResponseTransferItems>
        get() = _responseTransferData

    fun getTransferItems(
        tid: String,
        index: String,
        toDep: String
    ){
        uiCaller.makeRequest({
            repository.getTransferItems(tid, index, toDep)
        }){
            when(it){
                is RequestResult.Success ->_responseTransferData.postValue(it.result)
                is RequestResult.Error -> uiCaller.setError(it.error)
            }
        }
    }
}