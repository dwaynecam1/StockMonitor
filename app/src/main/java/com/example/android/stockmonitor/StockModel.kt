package com.example.android.stockmonitor

import java.util.*

class StockModel(
    rnk: Int,
    sp: String?,
    eng: String?,
    val ticker: String,
    val company: String,
    val sector: String,
    val industry: String,
    var basePrice: Float
) {
    var currentPrice: Float = 0.toFloat()
    var totalPctChange: Float = 0.toFloat()
//        set(val) {
//            field = `val`
//        }
    var dayPctChange: Float = 0.toFloat()
//        set(val) {
//            field = `val`
//        }

//    companion object {
//        var RankTotalPctChange: Comparator<StockModel> =
//            object : Comparator<StockModel?> {
//                override fun compare(left: StockModel, right: StockModel): Int {
//                    val diff = left.totalPctChange - right.totalPctChange
//                    return if (diff > 0) {
//                        1
//                    } else if (diff < 0) {
//                        -1
//                    } else {
//                        0
//                    }
//                }
//            }
//        var RankDayPctChange: Comparator<StockModel> =
//            object : Comparator<StockModel?> {
//                override fun compare(left: StockModel, right: StockModel): Int {
//                    val diff = left.dayPctChange - right.dayPctChange
//                    return if (diff > 0) {
//                        1
//                    } else if (diff < 0) {
//                        -1
//                    } else {
//                        0
//                    }
//                }
//            } //    public static Comparator<StockModel> Field3Comparator = new Comparator<StockModel>() {
//        //        @Override
//        //        public int compare(StockModel left, StockModel right) {
//        //
//        //            return left.getField(3).compareTo(right.getField(3));
//        //        }
//        //    };
//        //
//        //    public static Comparator<StockModel> Field4Comparator = new Comparator<StockModel>() {
//        //        @Override
//        //        public int compare(StockModel left, StockModel right) {
//        //
//        //            return left.getField(4).compareTo(right.getField(4));
//        //        }
//        //    };
//    }

    init {
        currentPrice = 0.toFloat()
        totalPctChange = 0.toFloat()
        dayPctChange = 0.toFloat()
    }
}