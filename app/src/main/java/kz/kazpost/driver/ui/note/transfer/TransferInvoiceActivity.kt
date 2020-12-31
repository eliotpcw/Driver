package kz.kazpost.driver.ui.note.transfer

import android.app.AlertDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_transfer_invoice.*
import kotlinx.android.synthetic.main.progress.view.*
import kz.kazpost.driver.R
import kz.kazpost.driver.data.enums.LabelsModel2
import kz.kazpost.driver.data.models.RequestItems
import kz.kazpost.driver.data.models.StationData
import kz.kazpost.driver.utils.HTTPHelper
import kz.kazpost.driver.data.local.PreferenceHelper
import kz.kazpost.driver.data.local.SharedPrefCache
import kz.kazpost.driver.data.models.LabelDataListItem
import kz.kazpost.driver.data.models.Road
import kz.kazpost.driver.ui.Status
import kz.kazpost.driver.ui.adapters.AcceptInvoiceAdapter
import org.koin.android.viewmodel.ext.android.viewModel
import kz.kazpost.driver.ui.station.MainActivity
import kz.kazpost.driver.utils.toast
import okhttp3.ResponseBody
import org.koin.android.ext.android.inject
import retrofit2.Response
import java.lang.ref.WeakReference

class TransferInvoiceActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog
    private lateinit var database: List<Road>
    private var index = 2

    private var labelModelsFromTransfer = mutableListOf<LabelsModel2>()
    private var mapOfLabels: HashMap<String, Int> = hashMapOf()

    private val transferAdapter = AcceptInvoiceAdapter()

    private val prefs by inject<SharedPrefCache>()
    private val viewModel: TransferInvoiceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_invoice)

        setSupportActionBar(atransfer_toolbar)
        title = "Накладная (передача)"

        database = PreferenceHelper.getRoads(this)
        index = intent.getIntExtra("index", 2)

        atransfer_station.text =
            "Станция: ".plus(database.filter { road -> road.index == index - 1 }[0].dept.nameRu)
        val indexTech = database.filter { road -> road.index == index - 1 }[0].dept.name

        viewModel.getTransferItems(
            prefs.getRoadOrGosNumber()!!,
            (index - 1).toString(),
            indexTech
        )

        initViewModel()

        ainvoice_recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transferAdapter
        }

        atransfer_accept.setOnClickListener {

            val sendData = RequestItems(
                prefs.getToDep2()!!,
                prefs.getToDep()!!,
                prefs.getRoadOrGosNumber()!!,
                index - 1,
                sendListOfTransferCount(transferAdapter.getLabelDataList(), mapOfLabels),
                labelModelsFromTransfer,
                transfersToItems(labelModelsFromTransfer),
                true,
                "transfer"
            )

            TransferDataAsync(
                this
            ).execute(sendData)

        }
    }

    private fun initViewModel() {
        viewModel.statusLiveData.observe(this, Observer { status ->
            when (status) {
                Status.SHOW_LOADING -> transfer_pb.visibility = View.VISIBLE
                Status.HIDE_LOADING -> transfer_pb.visibility = View.GONE
            }
        })
        viewModel.errorLiveData.observe(this, Observer {
            toast("Ошибка загрузки данных")
        })

        viewModel.responseTransferData.observe(this, Observer {
            transferAdapter.setLabelDataList(it.labelDataList!!)
            atransfer_labels_bottom_fact.text = it.itemCount.toString()

            it.mailItems!!.forEach { labelData ->
                labelModelsFromTransfer.add(
                    LabelsModel2(
                        labelData!!.name,
                        labelData.mailType,
                        labelData.toDepartment,
                        labelData.rpoId
                    )
                )
            }
            atransfer_accept.isEnabled = true
        })
    }

    companion object {

        class TransferDataAsync internal constructor(context: TransferInvoiceActivity) :
            AsyncTask<RequestItems, Void, Response<ResponseBody>>() {

            private val activityReference: WeakReference<TransferInvoiceActivity> =
                WeakReference(context)

            override fun doInBackground(vararg params: RequestItems?): Response<ResponseBody>? {
                return params[0]?.let {
                    HTTPHelper.postSendItems().post(it).execute()
                }
            }

            override fun onPreExecute() {
                activityReference.get()?.let { context ->
                    if (context.isFinishing) return
                    val builder = AlertDialog.Builder(context)
                    val view = context.layoutInflater.inflate(R.layout.progress, null)
                    view.progress_text.text = "Отправка данных..."
                    builder.setView(view)
                    context.dialog = builder.create()
                    context.dialog.setCancelable(false)
                    context.dialog.setCanceledOnTouchOutside(false)
                    context.dialog.show()
                }
            }

            override fun onPostExecute(result: Response<ResponseBody>?) {
                activityReference.get()?.let { context ->
                    if (context.isFinishing) return
                    context.dialog.dismiss()
                    result?.body()?.let {
                        val endConfirmItems2 = Intent(context, MainActivity::class.java) //pzdc
                        context.startActivity(endConfirmItems2)
                        context.finish()
                    }
                }
            }
        }
    }

    private fun sendListOfTransferCount(
        acceptItemsList: MutableList<LabelDataListItem?>,
        mapOfLabels: HashMap<String, Int>
    ): HashMap<String, Int> {
        acceptItemsList.forEach { labelData ->
            mapOfLabels[labelData!!.labelType] = labelData.count
        }
        return mapOfLabels
    }

    private fun transfersToItems(
        transfers: MutableList<LabelsModel2>
    ): MutableList<String> {
        val l = mutableListOf<String>()
        for (transfer in transfers) {
            l.add(transfer.rpo)
        }
        return l
    }
}