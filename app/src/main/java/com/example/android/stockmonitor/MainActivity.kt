package com.example.android.stockmonitor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.stockmonitor.databinding.ActivityHolidayBinding

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName
//    var  binding: ActivityHolidayBinding? = null
    var adapter: StockAdapter? = null

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityHolidayBinding = DataBindingUtil.setContentView(this, R.layout.activity_holiday)

//        initUI()
        binding.rvHolidayList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        binding.rvHolidayList.setLayoutManager(layoutManager)

//        adapter = new HolidayAdapter();
        adapter = StockAdapter()
        binding.rvHolidayList.setAdapter(adapter)
        val mDividerItemDecoration = DividerItemDecoration(binding.rvHolidayList.getContext(),
            layoutManager.orientation)
        binding.rvHolidayList.addItemDecoration(mDividerItemDecoration)

        val stockViewModel = StockViewModel()
        binding.tvHeaderTotalPctChange.setOnClickListener(View.OnClickListener {
            Log.v(TAG, "TotalPctChange clicked")
            stockViewModel.SortStockListByTotalPctChange()
            Toast.makeText(this@MainActivity, "Total Change Clicked", Toast.LENGTH_LONG).show()
        })
//        if (MyApplication.getInstance().isNetworkAvailable()) {
        if (isNetworkAvailable()) {
            binding.progressBar.setVisibility(View.VISIBLE)
            stockViewModel.getQuizList().observe(this, object : Observer<List<StockModel?>?> {
                override fun onChanged(currencyPojos: List<StockModel?>?) {
                    if (currencyPojos != null && !currencyPojos.isEmpty()) {
                        Log.e(TAG, "observe onChanged()=" + currencyPojos.size)
                        binding.progressBar.setVisibility(View.GONE)
                        adapter!!.addStockList(currencyPojos as List<StockModel>?)
                        adapter!!.notifyDataSetChanged()
                    }
                }
            })
        } else {
            Toast.makeText(this, "No Network Available", Toast.LENGTH_LONG).show()
        }
        stockViewModel.getCurrentStock().observe(this, Observer<Any?> {
            Log.d(TAG, "currentStock changed")
            val url = "https://finance.yahoo.com/quote/TSLA?p=TSLA"
        })
        askForReadExternalStoragePermission()
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.activeNetworkInfo
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

//    fun initUI() {
//        binding.rvHolidayList.setHasFixedSize(true)
//        val layoutManager = LinearLayoutManager(this)
//        binding.rvHolidayList.setLayoutManager(layoutManager)
//
////        adapter = new HolidayAdapter();
//        adapter = StockAdapter()
//        binding.rvHolidayList.setAdapter(adapter)
//        val mDividerItemDecoration = DividerItemDecoration(binding.rvHolidayList.getContext(),
//                layoutManager.orientation)
//        binding.rvHolidayList.addItemDecoration(mDividerItemDecoration)
//    }

    fun askForReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "READ_EXTERNAL_STORAGE permission granted")
        }
    }
}