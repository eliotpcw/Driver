package kz.kazpost.driver.ui.transportdata

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_transport_data.*
import kz.kazpost.driver.R
import kz.kazpost.driver.data.enums.AccountType
import kz.kazpost.driver.data.local.PreferenceHelper
import kz.kazpost.driver.data.local.SharedPrefCache
import kz.kazpost.driver.ui.station.MainActivity
import kz.kazpost.driver.utils.ACCOUNT_TYPE
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class TransportData : AppCompatActivity() {

    private val prefs by inject<SharedPrefCache>()

    private lateinit var isKazpost :AccountType
    private var dataBundle = Bundle()

    private val time by lazy {
        val date = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale("RU"))
        sdf.format(date)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transport_data)

        val type: Boolean = intent.getBooleanExtra(ACCOUNT_TYPE, true)

        isKazpost = if(type){
            prefs.saveAccountType("Mercenary")
            AccountType.Mercenary
        } else {
            prefs.saveAccountType("Driver")
            AccountType.Driver
        }

        val roads = PreferenceHelper.getRoads(this)

        t_id.text = getString(R.string.marshrute_number) + ": ${PreferenceHelper.getTId(this)}"
        current_time.text = "Время начала работы: $time"
        for (r in roads) {
            if(r.index == prefs.getRoadIndex()){
                current_station.text = "Текущая станция: " + r.dept.name + " " + r.dept.nameRu
            }
        }

        start_flight.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            dataBundle.putSerializable(ACCOUNT_TYPE, isKazpost)
            intent.putExtras(dataBundle)
            startActivity(intent)
            finish()
        }
    }
}