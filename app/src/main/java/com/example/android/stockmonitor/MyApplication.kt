package com.example.android.stockmonitor

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * Created by Kashif on 9/27/2019.
 */
class MyApplication : Application() {
    private lateinit var mInstance: MyApplication
    private val retrofit: Retrofit? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    @Synchronized
    fun getInstance(): MyApplication? {
        return mInstance
    }

    val isNetworkAvailable: Boolean
        get() {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var activeNetworkInfo: NetworkInfo? = null
            if (connectivityManager != null) {
                activeNetworkInfo = connectivityManager.activeNetworkInfo
            }
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

//    companion object {
//        //    final String TAG = getClass().getSimpleName();
//        @get:Synchronized
//        var instance: MyApplication? = null
//            private set
//        private var retrofit: Retrofit? = null
//
//        val retrofitClient: Retrofit?
//            get() {
//                if (retrofit == null) {
//                    val client: okhttp3.OkHttpClient = Builder().build()
//                    retrofit = Builder()
//                        .client(client)
//                        .addConverterFactory(ScalarsConverterFactory.create())
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .baseUrl(Constants.BASE_URL)
//                        .build()
//                }
//                return retrofit
//            }
//    }
}