package kz.kazpost.driver.ui.gosnumber

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kz.kazpost.driver.data.network.RequestResult
import kz.kazpost.driver.data.network.api.IApi
import kz.kazpost.driver.ui.BaseViewModel
import kz.kazpost.driver.utils.EventWrapper
import kz.kazpost.driver.data.local.PreferenceHelper
import kz.kazpost.driver.data.local.SharedPrefCache
import kz.kazpost.driver.data.models.VPNData

class GosNumberViewModel(
    val context: Context,
    private val api: IApi,
    private val gosNumberDelegate: GosNumberRepositoryImpl = GosNumberRepositoryImpl(api),
    private val prefs: SharedPrefCache
) : BaseViewModel() {

    private val _responseMD: MutableLiveData<EventWrapper<Boolean>> = MutableLiveData()
    internal val responseMD: LiveData<EventWrapper<Boolean>>
        get() = _responseMD

    private val _responseStationData: MutableLiveData<EventWrapper<VPNData>> = MutableLiveData()
    internal val responseStationData: LiveData<EventWrapper<VPNData>>
        get() = _responseStationData

    private var isScannerActivated: ObservableBoolean = ObservableBoolean()

    init {
        isScannerActivated.set(false)
    }

    fun makeAuth(
        number: String?,
        isKazPost: Boolean
    ) {
        uiCaller.makeRequest({
            if (!isKazPost) {
                PreferenceHelper.saveGosNumber(
                    context,
                    number!!
                )
                gosNumberDelegate.authGosNumber(number)
            } else {
                gosNumberDelegate.authRouteNumber(number)
            }
        }) {
            when (it) {
                is RequestResult.Success -> {
                    _responseMD.postValue(
                        EventWrapper(true)
                    )
                    prefs.saveRoadIndex(it.result.index!!)
                    PreferenceHelper.saveTId(context, it.result.transportListId!!)
                    PreferenceHelper.saveRoads(context, it.result.road!!)
                    prefs.saveRoadOrGosNumber(number!!)
                    _responseStationData.postValue(EventWrapper(it.result))
                }
                is RequestResult.Error -> uiCaller.setError(it.error)
            }
        }
    }

    @Deprecated("временная реализация")
    fun isScannerVisible(): Boolean {
        return if (isScannerActivated.get()) {
            isScannerActivated.set(false)
            false
        } else {
            isScannerActivated.set(true)
            true
        }
    }
}