package kz.kazpost.driver.ui.gosnumber

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_gos_number.*
import kz.kazpost.driver.R
import kz.kazpost.driver.data.enums.AccountType
import kz.kazpost.driver.ui.Status
import kz.kazpost.driver.ui.transportdata.TransportData
import kz.kazpost.driver.utils.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class GosNumberActivity
    : AppCompatActivity(),
    ZXingScannerView.ResultHandler{

    private var isKazpost by Delegates.notNull<Boolean>()
    private lateinit var gosNumberStr: String
    private lateinit var marshNumberStr: String

    private val utils by inject<Utils>()
    private val permission by inject<Permission>()

    private val viewModel: GosNumberViewModel by viewModel()

    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private var dialog: AlertDialog? = null

    private var dataBundle = Bundle()

    private val dialogView: View by lazy {
        LayoutInflater
            .from(this@GosNumberActivity)
            .inflate(R.layout.progress, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gos_number)
        title = "Получение маршрута"
        gosNumberStr = getString(R.string.gos_number)
        marshNumberStr = getString(R.string.marshrute_number)

        val type: AccountType = intent.getSerializableExtra(ACCOUNT_TYPE) as AccountType
        isKazpost = when (type) {
            is AccountType.Mercenary -> true
            is AccountType.Driver -> false
        }

        alertDialogBuilder = AlertDialog.Builder(this@GosNumberActivity).setView(dialogView)
        dialog = alertDialogBuilder
            .setMessage(R.string.alert_dialog_message)
            .setCancelable(false)
            .create()

        initViewModel()

        gos_number_scanner_btn.visibility.apply {
            if (isKazpost) View.GONE else View.VISIBLE
        }

        visibleListOfTView(isKazpost)

        gos_number_scanner_btn.setOnClickListener {
            utils.viewVisibility(
                listOf(
                    gos_number_scanner_tv,
                    gos_number_scanner_view
                ),
                viewModel.isScannerVisible(),
                { startScanner() },
                { stopCamera() }
            )
        }

        agos_signIn.setOnClickListener {
            if (agos_enter.text!!.isNotEmpty()) {
                viewModel.makeAuth(
                    agos_enter.text.toString(),
                    isKazpost
                )
            } else {
                agos_tenter.error = "Пустое поле"
                agos_tenter.isErrorEnabled = true
                agos_enter.requestFocus()
            }
        }
    }

    private fun initViewModel() {
        viewModel.statusLiveData.observe(this, Observer { status ->
            when (status) {
                Status.SHOW_LOADING -> {
                    dialog?.show()
                }
                Status.HIDE_LOADING -> {
                    dialog?.dismiss()
                }
            }
        })
        viewModel.errorLiveData.observe(this, EventObserver { error ->
            toast(error)
        })
        viewModel.responseMD.observe(this, EventObserver {
            if (it) {
                val intent = Intent(this, TransportData::class.java)
                dataBundle.putBoolean(ACCOUNT_TYPE, isKazpost)
                intent.putExtras(dataBundle)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun visibleListOfTView(isKazPost: Boolean) {
        if (!isKazPost){
            agos_tenter.hint = gosNumberStr
            gos_number_scanner_tv.text = "Введите $gosNumberStr"
            gos_number_scanner_btn.visibility = View.GONE
        } else {
            agos_tenter.hint = marshNumberStr
            gos_number_scanner_tv.text = getString(R.string.scanned_text, marshNumberStr)
            gos_number_scanner_btn.visibility = View.VISIBLE
        }
    }

    private fun startScanner() {
        if (!permission.askPermission(this)) {
            if(isKazpost){
                gos_number_scanner_view.setResultHandler(this)
                gos_number_scanner_view.visibility = View.VISIBLE
                gos_number_scanner_view.startCamera()
            }
        }
    }

    private fun stopCamera(){
        gos_number_scanner_view.visibility = View.GONE
        gos_number_scanner_view.stopCamera()
    }

    override fun onResume() {
        super.onResume()
        startScanner()
    }

    override fun onPause() {
        super.onPause()
        stopCamera()
    }

    override fun handleResult(rawResult: Result?) {
        viewModel.makeAuth(
            rawResult.toString(),
            isKazpost
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        if (permission.handlePermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        ) {
            startScanner()
        } else gos_number_scanner_view.visibility = View.GONE
    }
}