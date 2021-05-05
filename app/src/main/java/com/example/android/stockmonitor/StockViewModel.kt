package com.example.android.stockmonitor

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StockViewModel : ViewModel() {
    //    private HolidayRepo holidayRepo;
    private val StockRepo: StockRepo
    private var mutableQuizLiveData: MutableLiveData<List<StockModel>>? = null
    private var currentStock: MutableLiveData<StockModel>? = null

//    @get:RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    val quizList: LiveData<List<StockModel>>
//        get() {
//            if (mutableQuizLiveData == null) {
//                mutableQuizLiveData = StockRepo.loadQuizList()
//            }
//            return mutableQuizLiveData!!
//        }
//
//    fun SortStockListByTotalPctChange(): LiveData<List<StockModel>> {
//        mutableQuizLiveData = StockRepo.SortByTotalPctChange()
//        return mutableQuizLiveData
//    }

    fun getQuizList(): LiveData<List<StockModel>> {
            if (mutableQuizLiveData == null) {
                mutableQuizLiveData = StockRepo.loadQuizList()
            }
            return mutableQuizLiveData!!
    }

    fun SortStockListByTotalPctChange(): LiveData<List<StockModel>> {
        mutableQuizLiveData = StockRepo.SortByTotalPctChange()
        return mutableQuizLiveData as MutableLiveData<List<StockModel>>
    }

    fun getCurrentStock(): MutableLiveData<StockModel> {
        return StockRepo.selectedStock
    }

    fun onItemClick(index: Int?) {
        currentStock = StockRepo.getStockAt(index)
    }

    init {
        StockRepo = StockRepo()
    }
}