package kz.kazpost.driver.utils

import android.app.Application
import android.content.ContentResolver
import kz.kazpost.driver.data.network.receiver.ConnectionReceiver
import kz.kazpost.driver.di.appModule
import kz.kazpost.driver.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level

class App: Application(){

    companion object {
        @get:Synchronized
        var appController: App? = null
            private set
        @get:Synchronized
        var globalContentResolver: ContentResolver? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appController = this
        globalContentResolver = contentResolver

        startKoin{
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(appModule, networkModule))
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }

    fun setConnectionListener(listener: ConnectionReceiver.ConnectionReceiverListener){
        ConnectionReceiver.connectionReceiverListener = listener
    }

}