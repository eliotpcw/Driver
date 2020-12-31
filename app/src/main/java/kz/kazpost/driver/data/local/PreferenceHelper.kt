package kz.kazpost.driver.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kz.kazpost.driver.data.enums.ItemModel
import kz.kazpost.driver.data.enums.LabelsModel
import kz.kazpost.driver.data.models.Road
import kz.kazpost.driver.data.models.StationData

class PreferenceHelper {

    companion object Factory {
        private const val PREF_NAME = "preferences"
        private val gson = Gson()

        private const val CURRENT_STEP = "current_step"

        private const val IS_KAZPOST = "is_kazpost"
        private const val GOS_NUMBER = "gos_number"
        private const val TRANSPORT_ID = "transport_id"
        private const val ROAD_INDEX = "road_index"
        private const val ROADS = "roads"
        private const val DATABASE = "database"
        private const val LABELS = "labels"
        private const val ITEMS = "items"
        private const val SCANS = "scans"
        private const val SCANS2 = "scans2"
        private const val TRANSFERS = "transfers"
        private const val NAKLADNAYA_PRIEM_FACT_TOTAL_COUNT = "nakl_priem_fact_total_count"


        fun setIsKazpost(context: Context, isKazpost: Boolean) {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putBoolean(IS_KAZPOST, isKazpost)
            editor.apply()
        }

        fun isKazpost(context: Context): Boolean {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getBoolean(IS_KAZPOST, true)
        }

        fun saveGosNumber(context: Context, login: String) {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(GOS_NUMBER, login)
            editor.apply()
        }

        fun getGosNumber(context: Context): String {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getString(GOS_NUMBER, "") ?: ""
        }

        fun saveTId(context: Context, id: String) {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(TRANSPORT_ID, id)
            editor.apply()
        }

        fun getTId(context: Context): String {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getString(TRANSPORT_ID, "") ?: ""
        }

        fun saveRoads(context: Context, roads: List<Road>) {
            val json = gson.toJson(roads)
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(ROADS, json)
            editor.apply()
        }

        fun getRoads(context: Context): List<Road> {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = pref.getString(ROADS, "")
            val roadsType = object: TypeToken<List<Road>>(){}.type
            return gson.fromJson(json, roadsType)
        }

        fun saveDB(context: Context, database: StationData) {
            val json = gson.toJson(database)
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(DATABASE, json)
            editor.apply()
        }

        fun getDB(context: Context): StationData {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = pref.getString(DATABASE, "")
            return gson.fromJson(json, StationData::class.java)
        }

        fun saveLabels(context: Context, labels: MutableList<LabelsModel>) {
            val json = gson.toJson(labels)
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(LABELS, json)
            editor.apply()
        }

        fun getLabels(context: Context): MutableList<LabelsModel> {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = pref.getString(LABELS, "")
            val labelsType = object: TypeToken<MutableList<LabelsModel>>(){}.type
            return gson.fromJson(json, labelsType)
        }

        fun saveScans(context: Context, scans: MutableList<ItemModel>) {
            val json = gson.toJson(scans)
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(SCANS, json)
            editor.apply()
        }

        fun getScans(context: Context): MutableList<ItemModel>? {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = pref.getString(SCANS, "")
            return if (json == "") {
                null
            } else {
                val scanType = object: TypeToken<MutableList<ItemModel>>(){}.type
                gson.fromJson(json, scanType)
            }
        }

        fun saveTransfers(context: Context, transfers: MutableList<ItemModel>) {
            val json = gson.toJson(transfers)
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(TRANSFERS, json)
            editor.apply()
        }

        fun getTransfers(context: Context): MutableList<ItemModel>? {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = pref.getString(TRANSFERS, "")
            return if (json == "") {
                null
            } else {
                val transferType = object : TypeToken<MutableList<ItemModel>>() {}.type
                return gson.fromJson(json, transferType)
            }
        }

        fun clearPreference(context: Context) {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = pref.edit()
            //sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            editor.clear()
            editor.apply()
        }

        fun removeValue(context: Context, KEY_NAME: String) {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = pref.edit()
            editor.remove(KEY_NAME)
            editor.apply()
        }

        fun save_nakladnaya_priem_total_count(context: Context, index: Int) {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putInt(NAKLADNAYA_PRIEM_FACT_TOTAL_COUNT, index)
            editor.apply()
        }
    }
}