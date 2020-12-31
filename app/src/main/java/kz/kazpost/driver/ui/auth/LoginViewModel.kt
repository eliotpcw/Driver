package kz.kazpost.driver.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kz.kazpost.driver.data.local.SharedPrefCache
import kz.kazpost.driver.data.models.Fio
import kz.kazpost.driver.data.models.RequestAuthorisation
import kz.kazpost.driver.data.network.RequestResult
import kz.kazpost.driver.ui.BaseViewModel
import kz.kazpost.driver.ui.Status
import kz.kazpost.driver.utils.EventWrapper

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val prefs: SharedPrefCache
): BaseViewModel(){

    private val _responseWithLgnPwd: MutableLiveData<EventWrapper<Boolean>> = MutableLiveData()
    internal val responseWithLgnPwd: LiveData<EventWrapper<Boolean>>
        get() = _responseWithLgnPwd

    private val _responseIIN: MutableLiveData<EventWrapper<Fio>> = MutableLiveData()
    internal val responseIIN: LiveData<EventWrapper<Fio>>
        get() = _responseIIN


    fun authWithLgnPwd(userModel: RequestAuthorisation){
        uiCaller.makeRequest({
            loginRepository.makeAuthWithLgnPwd(
                userModel
            )
        }) {
            when (it) {
                is RequestResult.Success -> {
                    prefs.saveUserLogin(userModel.login)
                    prefs.saveToDep2(userModel.login)
                    _responseWithLgnPwd.postValue(EventWrapper(true))
                }
                is RequestResult.Error -> uiCaller.setError(it.error)
            }
        }
    }

    fun authWithIIN(iin: String){
        uiCaller.makeRequest({
            loginRepository.makeAuthWithIIN(iin)
        }){
            when (it) {
                is RequestResult.Success -> {
                    prefs.saveToDep2(iin)
                    _responseIIN.postValue(EventWrapper(it.result))
                }
                is RequestResult.Error -> {
                    uiCaller.setError(it.error)
                }
            }
        }
    }
}