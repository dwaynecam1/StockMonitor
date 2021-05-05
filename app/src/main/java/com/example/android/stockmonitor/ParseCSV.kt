package com.example.android.stockmonitor

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*

//import android.support.annotation.RequiresApi;
class ParseCSV {
    private val TAG = javaClass.simpleName

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun file2list(`in`: InputStream?): ArrayList<String> {
        val str_list = ArrayList<String>()
        try {
            if (`in` != null) {
//				InputStreamReader tmp=new InputStreamReader(in, "ISO-8859-1");
                val tmp =
                    InputStreamReader(`in`, StandardCharsets.UTF_8)
                val reader = BufferedReader(tmp)
                var str: String
                while (reader.readLine().also { str = it } != null) {
                    str_list.add(str)
                }
                `in`.close()
            }
        } catch (t: Throwable) {
//			Toast
//				.makeText(this, "Exception: "+t.toString(), 2000)
//				.show();
        }
        return str_list
    }

    fun csvToQuiz(csvList: ArrayList<String>): ArrayList<StockModel> {
        val StockModelList: ArrayList<StockModel> = ArrayList<StockModel>()
        var start_total_score = 0
        var total_lines = 0
        val it: Iterator<String> = csvList.iterator()
        val header = it.next()
        while (it.hasNext()) {
            val current_line = it.next()
            val localRank = 0
            start_total_score = start_total_score + localRank
            val field2 = get_field(current_line, 2, "~")
            val field3 = get_field(current_line, 3, "~")
            //            String field4 = get_field(current_line, 4, "~");
//            String field5 = get_field(current_line, 5, "~");
            val sector = get_field(current_line, 1, ",")
            val industry = get_field(current_line, 2, ",")
            val ticker = get_field(current_line, 4, ",")
            val company = get_field(current_line, 5, ",")
            val basePrice = java.lang.Float.valueOf(get_field(current_line, 6, ","))
            StockModelList.add(
                StockModel(
                    localRank,
                    field2,
                    field3,
                    ticker,
                    company,
                    sector,
                    industry,
                    basePrice
                )
            )
            total_lines++
            if (total_lines % 100 == 0) {
                Log.v("QuizFragment", "OnCreate: load loop")
            }
        }
        return StockModelList
    }

    fun ListToDict(StockModelList: ArrayList<StockModel>): Hashtable<String, StockModel> {
        val tickerDict: Hashtable<String, StockModel> =
            Hashtable<String, StockModel>()
        val it: Iterator<StockModel> = StockModelList.iterator()
        while (it.hasNext()) {
            val currentItem: StockModel = it.next()
//            tickerDict[currentItem.getTicker()] = currentItem
            tickerDict[currentItem.ticker] = currentItem
        }
        return tickerDict
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun WriteFileToExternalStorage(
        update_filename: String?,
        buffer: String?
    ) {
        val writeable = isExternalStorageWritable
        if (writeable) {
            val path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS
            )
            val file = File(path, update_filename)
            try {
                val os: OutputStream = FileOutputStream(file)
                //				OutputStreamWriter out = new OutputStreamWriter(os, "ISO-8859-1");
                val out =
                    OutputStreamWriter(os, StandardCharsets.UTF_8)
                out.write(buffer)
                out.close()
            } catch (t: Throwable) {
//				String exception = t.toString();
//				Toast.makeText(getActivity(), "Exception: " + exception, Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun ReadFileFromExternalStorage(update_filename: String?): ArrayList<String> {
        var `is`: InputStream? = null
        try {
//			boolean readable = isExternalStorageReadable();
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(path, update_filename)
            `is` = FileInputStream(file)
        } catch (t: Throwable) {
            Log.e(TAG, "Error in ReadFileFromExternalStorage()")
        }
        return file2list(`is`)
    }

    private val isExternalStorageWritable: Boolean
        private get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    //	private boolean isExternalStorageReadable() {
    //		String state = Environment.getExternalStorageState();
    //		return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    //	}
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun WriteFileToInternalStorage(
        context: Context,
        update_filename: String?,
        buffer: String?
    ) {
        val path = context.filesDir
        val file = File(path, update_filename)
        try {
            val os: OutputStream = FileOutputStream(file)
            val out =
                OutputStreamWriter(os, StandardCharsets.ISO_8859_1)
            out.write(buffer)
            out.close()
        } catch (t: Throwable) {
//				String exception = t.toString();
//				Toast.makeText(getActivity(), "Exception: " + exception, Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun ReadFileFromInternalStorage(
        context: Context,
        update_filename: String?
    ): ArrayList<String> {
        var `is`: InputStream? = null
        val path = context.filesDir
        val file = File(path, update_filename)
        try {
            `is` = FileInputStream(file)
        } catch (t: Throwable) {
            Log.e(TAG, "Error in ReadFileFromInternalStorage")
        }
        return file2list(`is`)
    }

    fun get_field(
        list_item: String,
        field_num: Int,
        separator: String?
    ): String {
        var pos_begin: Int
        var pos_end: Int
        var i: Int
        val target_field: String
        pos_begin = 0
        pos_end = 0
        i = 0
        while (i < field_num) {
            pos_end = list_item.indexOf(separator!!, pos_end + 1)
            if (i > 0) {
                pos_begin = list_item.indexOf(separator, pos_begin + 1)
                if (pos_begin < 0) {
                    return ""
                }
            }
            i++
        }
        if (pos_end < 0) {
            pos_end = list_item.length
        }
        if (pos_begin > 0) {
            pos_begin++
        }
        target_field = list_item.substring(pos_begin, pos_end) //pos_begin);

//		if(pos_begin < 0 || pos_end < 0){
//			target_field = "";
//		}
//		else {
//			target_field = list_item.substring(pos_begin, pos_end); //pos_begin);
//		}
        return target_field
    }

    fun getHtmlTables(htmlString: String): String {
        val htmlLength = htmlString.length
        val tableActive = false
        val beginTable = "<table"
        val endTable = "</table"
        val beginList = ArrayList<Int>()
        val endList = ArrayList<Int>()
        val newBuffer = StringBuffer("")
        var i = 0
        var j = 0
        while (i >= 0) {
            if (i == 0) {
                i = htmlString.indexOf(beginTable)
                j = htmlString.indexOf(endTable) + endTable.length
            } else {
                i = htmlString.indexOf(beginTable, i + 1)
                j = htmlString.indexOf(endTable, j + 1)
            }
            if (i != -1) {
                beginList.add(i)
            }
            if (j != -1) {
                endList.add(j + endTable.length)
            }
        }
        if (beginList.size == endList.size) {
            i = 0
            while (i < beginList.size) {
                newBuffer.append(htmlString.substring(beginList[i], endList[i]))
                i++
            }
        }
        return newBuffer.toString()
    }

    fun removeHtmlTags(htmlString: String): String {
        val newBuffer = StringBuffer("")
        val htmlSize = htmlString.length

//        for(int i=0; i<htmlSize; i++){
//            newBuffer.append(htmlString.charAt(i));
//        }
        return htmlString.replace("\\<.*?\\>".toRegex(), "")
    }

    fun trimWhitespace(inputString: String): String {

//        String outputString = inputString.replaceAll("\\s\\s\\s*"," ");
        return inputString.replace("[ \\t][ \\t]*".toRegex(), " ")
    }
}