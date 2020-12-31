package kz.kazpost.driver.di

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import kz.kazpost.driver.data.local.SharedPrefCache
import kz.kazpost.driver.data.network.api.IApi
import kz.kazpost.driver.test.TestRepository
import kz.kazpost.driver.test.TestViewModel
import kz.kazpost.driver.ui.auth.LoginRepository
import kz.kazpost.driver.ui.auth.LoginViewModel
import kz.kazpost.driver.ui.gosnumber.GosNumberRepository
import kz.kazpost.driver.ui.gosnumber.GosNumberRepositoryImpl
import kz.kazpost.driver.ui.gosnumber.GosNumberViewModel
import kz.kazpost.driver.ui.note.transfer.TransferInvoiceRepository
import kz.kazpost.driver.ui.note.transfer.TransferInvoiceViewModel
import kz.kazpost.driver.utils.BASE_URL
import kz.kazpost.driver.utils.Permission
import kz.kazpost.driver.utils.Utils
import me.dm7.barcodescanner.zxing.ZXingScannerView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.coroutines.CoroutineContext

val appModule = module{
    single { ZXingScannerView(get()) }
    single { Permission() }
    single { Utils() }
    single { SharedPrefCache(context = androidContext()) }
    single { AlertDialog.Builder(androidContext()) }

    single<CoroutineContext>(named(NAME_IO)) { Dispatchers.IO }
    single<CoroutineContext>(named(NAME_MAIN)) { Dispatchers.Main }

    single<GosNumberRepository> { GosNumberRepositoryImpl(api = get()) }
    single{ LoginRepository(api = get()) }
    single { TransferInvoiceRepository(api = get()) }
    single{ TestRepository(api = get()) }

    viewModel { GosNumberViewModel(context = androidContext(), api = get(), prefs = get()) }
    viewModel { LoginViewModel(loginRepository = get(), prefs = get ()) }
    viewModel { TransferInvoiceViewModel(repository = get()) }
    viewModel { TestViewModel(repository = get()) }
}

val networkModule = module {

    factory { provideDefaultOkHttpClient() }

    single { retrofit(client = get()) }

    single{ get<Retrofit>().create(IApi::class.java) }
}

private fun retrofit(client: OkHttpClient) = Retrofit.Builder()
    .client(client)
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()

private fun provideDefaultOkHttpClient(): OkHttpClient = try {

    /*
    Create a trust manager that does not validate certificate chains
    * Install the all-trusting trust manager
    * Create an ssl socket factory with our all-trusting manager
    * okHttpClient.connectTimeoutMillis();
    */

    val trustAllCerts = arrayOf<TrustManager>(
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {}
            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    )

    // Install the all-trusting trust manager
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())

    // Create an ssl socket factory with our all-trusting manager
    val sslSocketFactory = sslContext.socketFactory
    val builder = OkHttpClient.Builder()
    builder.sslSocketFactory(
        sslSocketFactory,
        trustAllCerts[0] as X509TrustManager
    )
    builder.hostnameVerifier(HostnameVerifier { _, _ -> true })

    val interceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger{
        override fun log(message: String) {
            Log.d("okHttp3: ", message)
        }
    })

    interceptor.level = HttpLoggingInterceptor.Level.BODY

    builder
        .addInterceptor(interceptor)
        .build()

} catch (e: Exception) {
    throw RuntimeException(e)
}

const val NAME_IO = "io"
const val NAME_MAIN = "main"