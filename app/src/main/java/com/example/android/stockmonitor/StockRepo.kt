package com.example.android.stockmonitor

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import javax.net.ssl.HttpsURLConnection

class StockRepo {
    private val TAG = javaClass.simpleName
    private val csv_filename = "sectors_combined_20200710_close.csv"
    private val baseUrl = "https://www.stockmonitor.com/sector/"
    private val mSectors = ArrayList<String>()
    private var mTickerDict: Hashtable<String, StockModel>? = null
    private val mParseCSV = ParseCSV()

    //    private DownloadTask mDownloadTask;
    private val mStockList = ArrayList<StockModel>()
    private val mFilteredStockList =
        ArrayList<StockModel>()

    //    private boolean mDataReady = false;
    private val mMutableLiveData =
        MutableLiveData<List<StockModel>>()
    val selectedStock = MutableLiveData<StockModel>()

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun loadQuizList(): MutableLiveData<List<StockModel>> {
//        final MutableLiveData<List<StockModel>> mutableLiveData = new MutableLiveData<>();
        val csvList: ArrayList<String>
        val stockModelList: ArrayList<StockModel>
        try {
            csvList = mParseCSV.ReadFileFromExternalStorage(csv_filename)
            stockModelList = mParseCSV.csvToQuiz(csvList)
            mTickerDict = mParseCSV.ListToDict(stockModelList)
            mSectors.add("technology")
            mSectors.add("communication-services")
            mSectors.add("consumer-cyclical")
            mSectors.add("consumer-defensive")
            mSectors.add("energy")
            mSectors.add("utilities")
            mSectors.add("industrials")
            mSectors.add("financial-services")
            //            mSectors.add("healthcare");
            updateStocks(mSectors)
            mMutableLiveData.value = stockModelList
            if (stockModelList.size > 0) {
                selectedStock.value = stockModelList[0]
            }

//            mutableLiveData.setValue(mStockList);
        } catch (t2: Throwable) {
            Log.e(TAG, "Error in loadQuizList()")
        }
        return mMutableLiveData
    }

    fun updateStocks(sectors: ArrayList<String>) {
        Thread(Runnable {
            var htmlString: String? = ""
            val sector_size = sectors.size
            var urlString: String
            for (i in 0 until sector_size) {
                urlString = baseUrl + sectors[i]
                Log.d(TAG, urlString)
                try {
                    val url = URL(urlString)
                    htmlString = downloadUrl(url)
                    Log.d(TAG, urlString)
                } catch (e: Exception) {
                    Log.d(TAG, "IOException")
                }
                mStockList.addAll(htmlToList(htmlString))
            }
            for (i in mStockList.indices) {
                val currentStock = mStockList[i]
                val dayChange = currentStock.dayPctChange
                val totalChange = currentStock.totalPctChange
                if (dayChange < -5 && totalChange < dayChange) {
                    mFilteredStockList.add(currentStock)
                }
            }
            //                Collections.sort(mStockList, StockModel.RankTotalPctChange);
//            Collections.sort(mFilteredStockList, StockModel.RankDayPctChange)
            mMutableLiveData.postValue(mFilteredStockList)
            if (mFilteredStockList.size > 0) {
                selectedStock.postValue(mFilteredStockList[0])
            }
        }).start()
    }

    fun SortByTotalPctChange(): MutableLiveData<List<StockModel>> {
//        Collections.sort(mFilteredStockList, StockModel.RankTotalPctChange)
//        mMutableLiveData.postValue(mFilteredStockList)
        return mMutableLiveData
    }

    private fun htmlToList(htmlString: String?): ArrayList<StockModel> {
        var buffer: String
        val stockList = ArrayList<StockModel>()
        buffer = htmlString!!.replace("<table", "\n<table")
        buffer = buffer.replace("<TABLE", "\n<TABLE")
        buffer = mParseCSV.getHtmlTables(buffer)
        buffer = buffer.replace("<tr", "\n<tr")
        buffer = buffer.replace("<TR", "\n<TR")
        buffer = buffer.replace(",", "`")
        buffer = buffer.replace("<thead>", "")
        buffer = buffer.replace("<THEAD", "")
        buffer = buffer.replace("</thead>", "")
        buffer = buffer.replace("</THEAD", "")
        buffer = buffer.replace("</th", ",")
        buffer = buffer.replace("</TH", ",")
        buffer = buffer.replace("</td>", ",")
        buffer = buffer.replace("</TD>", ",")
        buffer = mParseCSV.removeHtmlTags(buffer)
        buffer = mParseCSV.trimWhitespace(buffer)
        val csvList = buffer.split("\\n".toRegex()).toTypedArray()
        Log.d(TAG, buffer)
        var ticker: String
        var stockInfo: StockModel?
        for (s in csvList) {
            ticker = mParseCSV.get_field(s, 2, ",")
            stockInfo = mTickerDict!![ticker]
            if (stockInfo != null) {
                var dayPctChangeString = mParseCSV.get_field(s, 1, ",")
                dayPctChangeString = dayPctChangeString.replace("&#x25B2;", "")
                dayPctChangeString = dayPctChangeString.replace("&#x25BC;", "")
                dayPctChangeString = dayPctChangeString.replace("%", "")
                dayPctChangeString = dayPctChangeString.replace("`", "")
                val dayPctChange = java.lang.Float.valueOf(dayPctChangeString)
                stockInfo.dayPctChange = dayPctChange
                var priceString = mParseCSV.get_field(s, 4, ",")
                priceString = priceString.replace("`", "")
                val currentPrice = java.lang.Float.valueOf(priceString)
                stockInfo.currentPrice = currentPrice
                val basePrice = stockInfo.basePrice
                var totalPctChange = (currentPrice - basePrice) / basePrice * 100
                totalPctChange = Math.round(totalPctChange * 1000.0) / 1000.toFloat()
                stockInfo.totalPctChange = totalPctChange
                stockList.add(stockInfo)
            }
        }
        return stockList
    }

    @Throws(IOException::class)
    private fun downloadUrl(url: URL): String? {
        var stream: InputStream? = null
        var connection: HttpsURLConnection? = null
        var result: String? = null
        try {
            connection = url.openConnection() as HttpsURLConnection
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.readTimeout = 3000
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection!!.connectTimeout = 3000
            // For this use case, set HTTP method to GET.
            connection.requestMethod = "GET"
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.doInput = true
            // Open communications link (network traffic occurs here).
            connection.connect()
            //                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
            val responseCode = connection.responseCode
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw IOException("HTTP error code: $responseCode")
            }
            // Retrieve the response body as an InputStream.
            stream = connection.inputStream
            //                publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = StreamToString(stream)
                val str_length = result.length
                Log.d(TAG, "string length: $str_length")
                //                    result = readStream(stream, 500);
//                    publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS, 0);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            stream?.close()
            connection?.disconnect()
        }
        return result
    }

    @Throws(IOException::class)
    private fun StreamToString(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null) {
            val inputStreamReader =
                InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }

    fun getStockAt(index: Int?): MutableLiveData<StockModel> {
        val currentStock = mStockList[index!!]
        selectedStock.value = currentStock
        return selectedStock
    }
}