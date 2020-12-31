package kz.kazpost.driver.ui.note.accept

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager.FEATURE_CAMERA
import android.os.AsyncTask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_accept_invoice.*
import kotlinx.android.synthetic.main.progress.view.*
import kotlinx.android.synthetic.main.toast_green.view.*
import kz.kazpost.driver.R
import kz.kazpost.driver.ui.scanneritems.ScannerActivity
import kz.kazpost.driver.ui.adapters.AcceptInvoiceAdapter
import kz.kazpost.driver.ui.adapters.AcceptInvoiceAdapterInterface
import kz.kazpost.driver.data.models.StationData
import kz.kazpost.driver.ui.endscan.AcceptScanActivity
import kz.kazpost.driver.utils.HTTPHelper
import kz.kazpost.driver.data.local.PreferenceHelper
import retrofit2.Response
import java.lang.ref.WeakReference

data class LoadDataModel(
    var tid: String,
    var index: Int
)

class AcceptInvoiceActivity
    : AppCompatActivity(),
    AcceptInvoiceAdapterInterface{

    private lateinit var dialog: AlertDialog
    private lateinit var database: StationData
    private var tid = ""
    private var index = 1
    private val acceptAdapter = AcceptInvoiceAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_invoice)
        val isCameraAvailable = packageManager.hasSystemFeature(FEATURE_CAMERA)

        setSupportActionBar(ainvoice_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Накладная (прием)"

        ainvoice_recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = acceptAdapter
        }

        tid = PreferenceHelper.getTId(this)
        index = intent.getIntExtra("index", 1)

        LoadDataAsync(
            this
        ).execute(LoadDataModel(tid, index))

        ainvoice_accept.setOnClickListener {
            if (isCameraAvailable) {
                val scannerIntent =
                    Intent(this, ScannerActivity::class.java)
                startActivity(scannerIntent)
            } else {
                val skipScannerIntent =
                    Intent(this, AcceptScanActivity::class.java) //another activity
                startActivity(skipScannerIntent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_accept_invoice, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_refresh -> {
                val loadData =
                    LoadDataModel(tid, index)
                LoadDataAsync(
                    this
                ).execute(loadData)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        class LoadDataAsync internal constructor(context: AcceptInvoiceActivity) :
            AsyncTask<LoadDataModel, Void, Response<StationData>>() {
            private val activityReference: WeakReference<AcceptInvoiceActivity> =
                WeakReference(context)

            override fun doInBackground(vararg params: LoadDataModel?): Response<StationData>? {
                return params[0]?.let {
                    HTTPHelper.getStationData().get(it.tid, it.index).execute()
                }
            }

            override fun onPreExecute() {
                activityReference.get()?.let { context ->
                    if (context.isFinishing) return
                    val builder = AlertDialog.Builder(context)
                    val view = context.layoutInflater.inflate(R.layout.progress, null)
                    view.progress_text.text = "Подождите. Загрузка данных..."
                    builder.setView(view)
                    context.dialog = builder.create()
                    context.dialog.setCancelable(false)
                    context.dialog.setCanceledOnTouchOutside(false)
                    context.dialog.show()
                }
            }

            override fun onPostExecute(result: Response<StationData>?) {
                activityReference.get()?.let { context ->
                    if (context.isFinishing) return
                    context.dialog.dismiss()
                    result?.body()?.let { it ->
                        if (it.result == "success") {
                            PreferenceHelper.saveDB(context, it)

                            context.database = it

                            context.ainvoice_station.apply {    //station name
                                text = "Станция: ".plus(it.road.filter { road ->
                                    road.index == context.index }[0].dept.nameRu)
                            }
                            context.acceptAdapter.setLabelDataList((it.labelDataList!!))
                            context.ainvoice_labels_bottom_plan.text = it.items?.size.toString()

                            val toast = Toast(context)
                            toast.duration = Toast.LENGTH_LONG
                            val v = context.layoutInflater.inflate(R.layout.toast_green, null)
                            v.green_text.text = "Данные загружены успешно!"
                            toast.view = v
                            toast.show()
                        }
                    }
                }
            }
        }
    }

    override fun onFactChanged(sum: Int, isSavable: Boolean) {
        ainvoice_accept.isEnabled = isSavable
        PreferenceHelper.save_nakladnaya_priem_total_count(this, sum)
    }

}