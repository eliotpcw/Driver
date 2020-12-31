package kz.kazpost.driver.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kz.kazpost.driver.data.models.Fio
import kz.kazpost.driver.data.network.RequestResult
import kz.kazpost.driver.ui.BaseViewModel
import kz.kazpost.driver.utils.EventWrapper

class TestViewModel(
    private val repository: TestRepository
) : BaseViewModel(){

    private val _responseLabelType: MutableLiveData<EventWrapper<List<LabelDataListItem>>> = MutableLiveData()
    internal val responseLabelType: LiveData<EventWrapper<List<LabelDataListItem>>>
        get() = _responseLabelType

    fun getLabelType(
        tId:String,
        indexOfStation: Int
    ){
        uiCaller.makeRequest({
            repository.getLabelType(tId, indexOfStation)
        }){
            when(it){
                is RequestResult.Success -> _responseLabelType.postValue(EventWrapper(it.result.labelDataList))
                is RequestResult.Error -> uiCaller.setError(it.error)
            }
        }
    }

}