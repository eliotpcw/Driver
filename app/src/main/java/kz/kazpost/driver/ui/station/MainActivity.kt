package kz.kazpost.driver.ui.station

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kz.kazpost.driver.R
import kz.kazpost.driver.data.local.PreferenceHelper
import kz.kazpost.driver.data.local.SharedPrefCache
import kz.kazpost.driver.ui.gosnumber.GosNumberViewModel
import kz.kazpost.driver.ui.note.accept.AcceptInvoiceActivity
import kz.kazpost.driver.ui.note.transfer.TransferInvoiceActivity
import kz.kazpost.driver.utils.EventObserver
import kz.kazpost.driver.utils.SPINNER_DEFAULT_NUMBER
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog
    private var depts: MutableList<String> = mutableListOf()

    private val viewModel: GosNumberViewModel by viewModel()
    private lateinit var spinnerArrayAdapter: ArrayAdapter<String>

    private val prefs by inject<SharedPrefCache>()

    private var isKazPost by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(amain_toolbar)
        title = "Главная страница"

        isKazPost = when(prefs.getAccountType()){
            "Mercenary" -> true
            else -> false
        }

        viewModel.makeAuth(prefs.getRoadOrGosNumber(), isKazPost)

        val roads = PreferenceHelper.getRoads(this)
        for (r in roads) {
            depts.add(r.dept.name + " " + r.dept.nameRu)
        }

        amain_accept.setOnClickListener {
            val intent = Intent(this, AcceptInvoiceActivity::class.java)
            intent.putExtra("index", amain_spinner.selectedItemPosition + 1)
            startActivity(intent)
        }
        amain_transfer.setOnClickListener {
            val intent = Intent(this, TransferInvoiceActivity::class.java)
            intent.putExtra("index", amain_spinner.selectedItemPosition + 2)
            startActivity(intent)
        }

        initViewModel()
    }

    private fun initViewModel(){
        viewModel.responseStationData.observe(this, EventObserver {
            when (it.status) {
                "Departed" -> {
                    amain_accept.isEnabled = false
                    amain_transfer.isEnabled = false
                    status_tv.text = getString(R.string.departed)
                }
                "Arrived" -> {
                    val res: Resources = resources
                    it.apply {
                        val text: String = java.lang.String.format(
                            res.getString(R.string.await),
                            road!![index!! - 1].dept.nameRu
                        )
                        selectionValueForBtn(state, text)
                        prefs.saveRoadIndex(index)
                        prefs.saveToDep(it.nextDep!!)
                        PreferenceHelper.saveRoads(this@MainActivity, it.road!!)
                        setIndexOfSpinnerStation(index - SPINNER_DEFAULT_NUMBER)
                    }
                    PreferenceHelper.saveTId(this, it.transportListId!!)
                }
                "Archived" -> {
                    amain_accept.isEnabled = false
                    amain_transfer.isEnabled = false
                    status_tv.text = getString(R.string.archived)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.makeAuth(prefs.getRoadOrGosNumber(), isKazPost)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setIndexOfSpinnerStation(indexOfStation: Int) {
        spinnerArrayAdapter = object : ArrayAdapter<String>(
            this,
            R.layout.item_spin,
            depts
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position == indexOfStation
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                tv.apply {
                    if (position == indexOfStation){
                        setTextColor(Color.BLACK)
                    } else setTextColor(Color.GRAY)
                }
                return view
            }
        }
        spinnerArrayAdapter.setDropDownViewResource(R.layout.item_spin)
        amain_spinner.adapter = spinnerArrayAdapter
        amain_spinner.setSelection(indexOfStation)
    }

    private fun selectionValueForBtn(
        state: String,
        awaitAlert: String
    ) {
        when (state) {
            "accept" -> {
                setEnabledBtn(acceptValue = true, transferValue = false)
                status_tv.visibility = View.GONE
            }
            "transfer" -> {
                setEnabledBtn(acceptValue = false, transferValue = true)
                status_tv.visibility = View.GONE
            }
            "await" -> {
                setEnabledBtn(acceptValue = false, transferValue = false)
                status_tv.visibility = View.VISIBLE
                status_tv.text = awaitAlert
            }
            "end" -> {
                setEnabledBtn(acceptValue = false, transferValue = false)
                status_tv.visibility = View.VISIBLE
                status_tv.text = "Маршрут окончен"
            }
        }
    }

    private fun setEnabledBtn(
        acceptValue: Boolean,
        transferValue: Boolean
        ){
        amain_accept.isEnabled = acceptValue
        amain_transfer.isEnabled = transferValue
    }

    override fun onDestroy() {
        super.onDestroy()
        amain_spinner.adapter = null
    }
}