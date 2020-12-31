package kz.kazpost.driver.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_login.*
import kz.kazpost.driver.R
import kz.kazpost.driver.data.enums.AccountType
import kz.kazpost.driver.data.local.SharedPrefCache
import kz.kazpost.driver.data.models.RequestAuthorisation
import kz.kazpost.driver.ui.Status
import kz.kazpost.driver.ui.gosnumber.GosNumberActivity
import kz.kazpost.driver.utils.ACCOUNT_TYPE
import kz.kazpost.driver.utils.EventObserver
import kz.kazpost.driver.utils.toEditable
import kz.kazpost.driver.utils.toast
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {

    private lateinit var userModel: RequestAuthorisation
    private var dataBundle = Bundle()

    private var isKazPost by Delegates.notNull<Boolean>()

    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private var dialog: AlertDialog? = null

    private val prefs by inject<SharedPrefCache>()
    private val viewModel: LoginViewModel by viewModel()

    private val dialogView: View by lazy {
        LayoutInflater.from(this@LoginActivity).inflate(R.layout.progress, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        alertDialogBuilder = AlertDialog.Builder(this@LoginActivity).setView(dialogView)
        dialog = alertDialogBuilder
            .setMessage(R.string.alert_dialog_message)
            .setCancelable(false)
            .create()

        title = "Водитель"

        initViewModel()

        changeUser(0)

        alogin_signIn.setOnClickListener {
            if (alogin_login.text!!.isNotEmpty() && alogin_password.text!!.isNotEmpty()) {
                userModel = RequestAuthorisation(
                    alogin_login.text.toString(),
                    alogin_password.text.toString()
                )
                viewModel.authWithLgnPwd(userModel)
            } else {
                checkViewForError(
                    mapOf(
                        alogin_login to alogin_tlogin,
                        alogin_password to alogin_tpassword
                    )
                )
            }
        }

        alogin_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                changeUser(position)
            }
        }

        alogin_login.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isKazPost && s!!.length == 12){
                    viewModel.authWithIIN(s.toString())
                }
            }
        })
    }

    private fun checkViewForError(
        mapOfView: Map<TextInputEditText, TextInputLayout>
    ) {
        mapOfView.forEach {(key, value)->
            if (key.text.isNullOrEmpty()) {
                value.error = "Пустое поле"
                value.isErrorEnabled = true
            }
        }
    }

    private fun initViewModel() {
        viewModel.statusLiveData.observe(this, Observer { status ->
            when (status) {
                Status.SHOW_LOADING -> {
                    dialog?.show()
//                    alert(0, R.string.alert_dialog_message){
//                        create().show()
//                    }
                }
                Status.HIDE_LOADING -> {
                    dialog?.dismiss()
//                    alert(0, R.string.alert_dialog_message){
//                        create().dismiss()
//                    }
                }
            }
        })
        viewModel.errorLiveData.observe(this, EventObserver { error ->
            toast(error)
        })
        viewModel.responseWithLgnPwd.observe(this, EventObserver {
            if (it) {
                gosNumberIntent(AccountType.Driver)
            }
        })
        viewModel.responseIIN.observe(this, EventObserver{
            alogin_password.text = it.fio?.toEditable()
            gosNumberIntent(AccountType.Mercenary)
        })
    }

    private fun gosNumberIntent(accountType: AccountType){
        val intent = Intent(this, GosNumberActivity::class.java)
        dataBundle.putSerializable(ACCOUNT_TYPE, accountType)
        intent.putExtras(dataBundle)
        startActivity(intent)
    }

    @Deprecated("изменить реализацию")
    private fun changeUser(index: Int) {
        alogin_login.text?.clear()
        alogin_password.text?.clear()
        alogin_signIn.isEnabled = index == 0
        when(index){
            0 ->{
                alogin_tlogin.apply{
                    isErrorEnabled = false
                    hint = "Логин"
                }
                alogin_tpassword.apply {
                    isErrorEnabled = false
                    hint = "Пароль"
                }
                alogin_login.text = prefs.getUserLogin()!!.toEditable()
                isKazPost = true
            }
            1 -> {
                alogin_tlogin.apply{
                    isErrorEnabled = false
                    hint = "ИИН"
                }
                alogin_tpassword.apply {
                    isErrorEnabled = false
                    hint = "ФИО"
                }
                isKazPost = false
            }
        }
    }
}