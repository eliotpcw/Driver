package kz.kazpost.driver.ui.scanneritems

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_scanner.*
import kz.kazpost.driver.R
import kz.kazpost.driver.data.enums.ItemModel
import kz.kazpost.driver.data.enums.LabelsEnum
import kz.kazpost.driver.data.models.StationData
import kz.kazpost.driver.ui.endscan.AcceptScanActivity
import kz.kazpost.driver.utils.Permission
import kz.kazpost.driver.data.local.PreferenceHelper
import kz.kazpost.driver.data.local.SharedPrefCache
import kz.kazpost.driver.data.models.LabelDataListItem
import kz.kazpost.driver.utils.Utils
import kz.kazpost.driver.utils.toast
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.ext.android.inject
import kotlin.collections.ArrayList

class ScannerActivity
    : AppCompatActivity(),
    ZXingScannerView.ResultHandler {

    private val bareCodeList: List<String> = arrayListOf()
    private lateinit var database: StationData

    private val permission by inject<Permission>()
    private val prefs by inject<SharedPrefCache>()

    private var currentScannedList: ArrayList<String> = ArrayList()
    private var itemListToDb: MutableList<ItemModel> = mutableListOf()

    private var count = 0

    lateinit var alertDialogBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        database = PreferenceHelper.getDB(this)

        prefs.removeScans2()
        if (currentScannedList.isNotEmpty()) {
            currentScannedList.clear()
        }

        alertDialogBuilder = AlertDialog.Builder(this@ScannerActivity)
        if (!permission.askPermission(this)) {
            startScanner()
        }

        title = "Сканер"

        close_scanner_btn.setOnClickListener {
            closeScanner()
        }
    }

    override fun handleResult(rawResult: Result) {
        if (currentScannedList.contains(rawResult.text)) {
            toast("Уже добавлен")
        } else {
            if (database.items!!.contains(rawResult.text)) {
                saveItemToDb(rawResult.text)
                toast("${rawResult.text} сохранен")
                addItemToCurrentScannedList(rawResult)
            } else {
                toast("Данного РПО/МЕШКА нету в списке!")
            }
        }
        resumeScanner()
    }

    private fun addItemToCurrentScannedList(shpi: Result){
        currentScannedList.add(shpi.text)
        count += 1
        scanner_count.text = getString(R.string.scanned_count, count)
    }

    private fun closeScanner(){
        prefs.saveScans2(itemListToDb)
        val intent = Intent(this, AcceptScanActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveItemToDb(bareCode: String) {
        lateinit var model: ItemModel

        for (labelItem in database.labelItems!!) {
            if (labelItem.labelListId == bareCode) {
                val typeName = labelItem.name

                val fromName = database.road.filter { road ->
                    road.dept.name == labelItem.fromDepartment
                }[0].dept.nameRu

                val toName = database.road.filter { road ->
                    road.dept.name == labelItem.toDepartment
                }[0].dept.nameRu

                model = ItemModel(
                    labelItem.labelType,
                    typeName,
                    labelItem.labelListId,
                    labelItem.fromDepartment,
                    labelItem.toDepartment,
                    fromName,
                    toName,
                    labelItem.hasAct
                )
                itemListToDb.add(model)
            }
        }

        for (mailItem in database.mailItems!!) {
            if (mailItem.mailId == bareCode) {
                val typeName = mailItem.name

                val fromName = database.road.filter { road ->
                    road.dept.name == mailItem.fromDepartment
                }[0].dept.nameRu

                val toName = database.road.filter { road ->
                    road.dept.name == mailItem.toDepartment
                }[0].dept.nameRu

                model = ItemModel(
                    mailItem.mailType,
                    typeName,
                    mailItem.mailId,
                    mailItem.fromDepartment,
                    mailItem.toDepartment,
                    fromName,
                    toName,
                    mailItem.hasAct
                )
                itemListToDb.add(model)
            }
        }
    }

    private fun startScanner() {
        zxingScannerView.setResultHandler(this)
        zxingScannerView.startCamera()
    }

    override fun onResume() {
        super.onResume()
        startScanner()
    }

    private fun resumeScanner() {
        zxingScannerView.resumeCameraPreview(this)
    }

    override fun onPause() {
        super.onPause()
        zxingScannerView.stopCamera()
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
        } else finish()
    }
}