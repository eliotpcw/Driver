package kz.kazpost.driver.ui.endscan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_accept_scan.*
import kotlinx.android.synthetic.main.toast_red.view.*
import kz.kazpost.driver.ui.confirmitems.AcceptItemsActivity
import kz.kazpost.driver.R
import kz.kazpost.driver.ui.adapters.ItemAdapter
import kz.kazpost.driver.ui.adapters.OnItemClickListener
import kz.kazpost.driver.data.enums.ItemModel
import kz.kazpost.driver.data.enums.LabelsEnum
import kz.kazpost.driver.utils.toEditable
import kz.kazpost.driver.data.models.StationData
import kz.kazpost.driver.data.local.PreferenceHelper
import kz.kazpost.driver.data.local.SharedPrefCache
import kz.kazpost.driver.utils.toast
import org.koin.android.ext.android.inject

class AcceptScanActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var database: StationData
    private lateinit var scans: MutableList<ItemModel>
    private lateinit var scans2: MutableList<ItemModel>
    lateinit var alertDialogBuilder: AlertDialog.Builder

    private val prefs by inject<SharedPrefCache>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_scan)
        setSupportActionBar(ascan_toolbar)
        title = "Сканирование"
        alertDialogBuilder = AlertDialog.Builder(this@AcceptScanActivity)

        database = PreferenceHelper.getDB(this)
        scans = PreferenceHelper.getScans(this) ?: mutableListOf()
        scans2 = prefs.getScans2() ?: mutableListOf()

        ascan_recycler.layoutManager = LinearLayoutManager(this)
        ascan_recycler.adapter = ItemAdapter(scans2, this)

        ascan_added.text = "Просканировано: ${scans2.size}"

        ascan_end.setOnClickListener {
            savePreference()
            val intent = Intent(this, AcceptItemsActivity::class.java)
            startActivity(intent)
            finish()
        }

        ascan_textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!!.length == 13 && s.endsWith("KZ")){
                    addToDeliveryToDb(s.toString())
                } else if (s.startsWith("G") && s.length == 16) {
                    addToDeliveryToDb(s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString()
                if(str.length == 13 && str.endsWith("KZ")){
                    addToDeliveryToDb(str)
                } else if (str.startsWith("G") && str.length == 16) {
                    addToDeliveryToDb(str)
                }
            }
        })
    }

    private fun addToDeliveryToDb(s: String?){
        if (database.items!!.contains(s)) {
            lateinit var model: ItemModel
            for (labelItem in database.labelItems!!) {
                if (labelItem.labelListId == s) {
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
                }
            }

            for (mailItem in database.mailItems!!) {
                if (mailItem.mailId == s) {
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
                }
            }

            if (!scans2.contains(model)) {
                scans2.add(model)
                ascan_textInputEditText.text?.clear()
                ascan_recycler.adapter?.notifyDataSetChanged()

                Toast.makeText(
                    this@AcceptScanActivity,
                    "Успешно добавлено",
                    Toast.LENGTH_SHORT
                ).show()

                ascan_added.text = "Просканировано: ".plus(scans2.size)
            } else {
                ascan_textInputEditText.text?.clear()
                runRedToast("Данное ШПИ уже просканировано")
            }
        } else {
            toast("Данного РПО нет в списке")
            ascan_textInputEditText.text?.clear()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 &&  data != null) {
            val code = data.getStringExtra("CODE")
            if (code != null) {
                ascan_textInputEditText.text?.clear()
                ascan_textInputEditText.text = code.toEditable()
            }
        }
    }

    private fun savePreference() {
        PreferenceHelper.saveScans(this, scans2)
    }

    override fun onItemClicked(model: ItemModel) {}

    private fun runRedToast(text: String) {
        val toast = Toast(this)
        toast.duration = Toast.LENGTH_LONG
        val v = layoutInflater.inflate(R.layout.toast_red, null)
        v.red_text.text = text
        toast.view = v
        toast.show()
    }
}