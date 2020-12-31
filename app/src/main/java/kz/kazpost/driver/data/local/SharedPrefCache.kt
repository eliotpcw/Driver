package kz.kazpost.driver.data.local

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kz.kazpost.driver.data.enums.ItemModel
import kz.kazpost.driver.data.models.RequestAuthorisation
import kz.kazpost.driver.data.models.VPNData
import kz.kazpost.driver.utils.*
import java.lang.reflect.Type


class SharedPrefCache(var context: Context) {

    private val gson = Gson()
    private val str= ""

    private val prefs
        get() = PreferenceManager.getDefaultSharedPreferences(context)

    fun saveRoadOrGosNumber(data: String){
        prefs.edit().apply{
            putString(ROAD_OR_GOS_NUMBER, data)
        }.apply()
    }

    fun getRoadOrGosNumber(): String? {
        return prefs.getString(ROAD_OR_GOS_NUMBER, "")
    }

    fun saveAccountType(data: String) {
        prefs.edit().apply {
            putString(ACCOUNT_TYPE, data)
        }.apply()
    }

    fun getAccountType(): String? {
        return prefs.getString(ACCOUNT_TYPE, "")
    }

    fun saveRoadIndex(roadIndex: Int) {
        prefs.edit().apply {
            putInt(ROAD_INDEX, roadIndex)
        }.apply()
    }

    fun getRoadIndex(): Int {
        return prefs.getInt(ROAD_INDEX, 0)
    }

    fun clearRoadIndex(){
        prefs.edit().remove(ROAD_INDEX).apply()
    }

    fun saveToDep(toDep: String){
        prefs.edit().apply{
            putString(TO_DEP, toDep)
        }.apply()
    }

    fun getToDep(): String? {
        return prefs.getString(TO_DEP, "")
    }

    fun saveToDep2(toDep: String){
        prefs.edit().apply{
            putString(TO_DEP2, toDep)
        }.apply()
    }

    fun getToDep2(): String? {
        return prefs.getString(TO_DEP2, "")
    }

    fun saveUserLogin(login: String){
        prefs.edit().apply{
            putString(USER_LOGIN, login)
        }.apply()
    }

    fun getUserLogin(): String? {
        return prefs.getString(USER_LOGIN, "")
    }

    fun saveUserLoginIIN(login: String){
        prefs.edit().apply{
            putString(USER_LOGIN_IIN, login)
        }.apply()
    }

    fun getUserLoginIIN(): String? {
        return prefs.getString(USER_LOGIN_IIN, "")
    }

    fun saveScans2(list: MutableList<ItemModel>) {
        val json = gson.toJson(list)
        set(SCANS2, json)
    }

    fun getScans2(): MutableList<ItemModel>? {
        val serializedObject: String? = prefs.getString(SCANS2, null)
        return if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<MutableList<ItemModel?>?>() {}.type
            gson.fromJson<MutableList<ItemModel>>(serializedObject, type)
        } else null
    }

    fun removeScans2(){
        prefs.edit().remove(SCANS2).apply()
    }

    operator fun set(key: String?, value: String?) {
        prefs.edit().apply{
            putString(key, value)
        }.apply()
    }
}