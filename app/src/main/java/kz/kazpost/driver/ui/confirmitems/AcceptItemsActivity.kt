package kz.kazpost.driver.ui.confirmitems

import android.app.AlertDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_accept_items.*
import kotlinx.android.synthetic.main.progress.view.*
import kotlinx.android.synthetic.main.toast_green.view.*
import kz.kazpost.driver.R
import kz.kazpost.driver.ui.adapters.TransferInvoiceAdapter
import kz.kazpost.driver.data.enums.ItemModel
import kz.kazpost.driver.data.enums.LabelsModel
import kz.kazpost.driver.data.enums.LabelsModel2
import kz.kazpost.driver.data.models.RequestItems
import kz.kazpost.driver.data.models.StationData
import kz.kazpost.driver.ui.station.MainActivity
import kz.kazpost.driver.utils.HTTPHelper
import kz.kazpost.driver.data.local.PreferenceHelper
import kz.kazpost.driver.data.local.SharedPrefCache
import kz.kazpost.driver.data.models.ItemMap
import kz.kazpost.driver.data.models.LabelDataListItem
import kz.kazpost.driver.ui.adapters.AcceptInvoiceAdapter
import okhttp3.ResponseBody
import org.koin.android.ext.android.inject
import retrofit2.Response
import java.lang.ref.WeakReference

class AcceptItemsActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog
    private lateinit var database: StationData
    private lateinit var scans: MutableList<ItemModel>
    private var slabels: MutableList<LabelsModel> = mutableListOf()

    private var acceptItemsList: MutableList<LabelsModel> = mutableListOf()
    private var acceptItemsList2: MutableList<LabelsModel2> = mutableListOf()

    private var mapOfLabels: HashMap<String, Int> = hashMapOf()

    private val prefs by inject<SharedPrefCache>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_items)

        setSupportActionBar(abg_toolbar)
        title = "Список просканированных"

        database = PreferenceHelper.getDB(this)
        scans = PreferenceHelper.getScans(this) ?: mutableListOf()

        slabels = scansToLabelsList(
            scans,
            database.labelDataList,
            acceptItemsList
        )

        abg_recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TransferInvoiceAdapter(slabels)
        }

        abg_labels_bottom_plan.text = planCount(slabels)
        abg_labels_bottom_fact.text = factCount(slabels)

        abg_accept.setOnClickListener {
            val sendData = RequestItems(
                prefs.getToDep2()!!,
                database.fromDep,
                database.transportListId,
                database.index,
                sendListOfLabelsCount(slabels, mapOfLabels),
                scansToLabelList2(scans),
                transfersToItems(scans),
                true,
                "accept"
            )

            SendDataAsync(
                this
            ).execute(sendData)
        }
    }

    companion object {

        class SendDataAsync internal constructor(context: AcceptItemsActivity) :
            AsyncTask<RequestItems, Void, Response<ResponseBody>>() {

            private lateinit var prefs: SharedPrefCache
            private val activityReference: WeakReference<AcceptItemsActivity> =
                WeakReference(context)

            override fun doInBackground(vararg params: RequestItems?): Response<ResponseBody>? {
                return params[0]?.let {
                    HTTPHelper.postVerifyItems().post(it).execute()
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
                        prefs = SharedPrefCache(context)
                        val toast = Toast(context)
                        toast.duration = Toast.LENGTH_LONG
                        val v = context.layoutInflater.inflate(R.layout.toast_green, null)
                        v.green_text.text = "Данные отправлены успешно!"
                        toast.view = v
                        toast.show()

                        val endConfirmItems = Intent(context, MainActivity::class.java) //pzdc
                        context.startActivity(endConfirmItems)
                        context.finish()
                    }
                }
            }
        }
    }

    private fun planCount(
        slabels: MutableList<LabelsModel>
    ): String {
        var count = 0
        for (slabel in slabels) {
            count += slabel.plan
        }
        return count.toString()
    }

    private fun factCount(
        slabels: MutableList<LabelsModel>
    ): String {
        var count = 0
        for (slabel in slabels) {
            count += slabel.fact
        }
        return count.toString()
    }

    private fun scansToLabelsList(
        transfers: MutableList<ItemModel>,
        labelDataList: List<LabelDataListItem?>?,
        acceptItemsList: MutableList<LabelsModel>
    ): MutableList<LabelsModel> {

        labelDataList?.forEach { labelData ->
            acceptItemsList.add(
                LabelsModel(
                    labelData!!.name,
                    labelData.count,
                    getFactCountsOfModels(
                        labelData,
                        transfers
                    ),
                    labelData.labelType
                )
            )
        }
        return acceptItemsList
    }

    private fun scansToLabelList2(
        transfers: MutableList<ItemModel>
    ): MutableList<LabelsModel2>{
        transfers.forEach { transfer ->
            acceptItemsList2.add(
                LabelsModel2(
                    transfer.typeName,
                    transfer.type,
                    transfer.to,
                    transfer.shpi
                )
            )
        }
        return acceptItemsList2
    }

    private fun sendListOfLabelsCount(
        acceptItemsList2: MutableList<LabelsModel>,
        mapOfLabels: HashMap<String, Int>
    ): HashMap<String, Int>{
        acceptItemsList2.forEach { labelData ->
            acceptItemsList.forEach { _ ->
                if (labelData.fact > 0){
                    mapOfLabels[labelData.type] = labelData.fact
                }
            }
        }
        return mapOfLabels
    }

    private fun getFactCountsOfModels(
        labelDataList: LabelDataListItem,
        transfers: MutableList<ItemModel>
        ): Int{
        return transfers.filter {
            it.type == labelDataList.labelType
        }.count()
    }

    private fun transfersToItems(transfers: MutableList<ItemModel>): MutableList<String> {
        val l = mutableListOf<String>()
        for (transfer in transfers) {
            l.add(transfer.shpi)
        }
        return l
    }
}