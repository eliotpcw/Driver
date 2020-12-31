package kz.kazpost.driver.test

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_login.*
import kz.kazpost.driver.data.enums.AccountType
import kz.kazpost.driver.ui.Status
import kz.kazpost.driver.utils.EventObserver
import kz.kazpost.driver.utils.toEditable
import kz.kazpost.driver.utils.toast
import org.koin.android.viewmodel.ext.android.viewModel

class TestFragment :  AppCompatActivity(){

    private val viewModel: TestViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getLabelType("T202008190757612", 1)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.statusLiveData.observe(this, Observer { status ->
            when (status) {
                Status.SHOW_LOADING -> {
//                    alert(0, R.string.alert_dialog_message){
//                        create().show()
//                    }
                }
                Status.HIDE_LOADING -> {
//                    alert(0, R.string.alert_dialog_message){
//                        create().dismiss()
//                    }
                }
            }
        })
        viewModel.errorLiveData.observe(this, EventObserver { error ->
            toast(error)
        })
        viewModel.responseLabelType.observe(this, EventObserver {
            it.forEach {label->
                println("### test Labels: $label")
            }
        })
    }


}