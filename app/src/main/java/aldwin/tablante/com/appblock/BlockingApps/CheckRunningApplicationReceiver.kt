package aldwin.tablante.com.appblock.BlockingApps

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import org.json.JSONObject


class CheckRunningApplicationReceiver : BroadcastReceiver() {

    val TAG = "CRAR" // CheckRunningApplicationReceiver
    val DATABASE_NAME = "Apps.db"
    val TABLE_NAME = "Apps_table"
    var id =""
    var context: CheckRunningApplicationReceiver = this
    var BlockList: ArrayList<String> = arrayListOf()
    var apps: ArrayList<String> = arrayListOf()
    var ser = Build.SERIAL

    override fun onReceive(aContext: Context, anIntent: Intent) {
        var db = DataBaseHelper(aContext, DATABASE_NAME, null, 1)

        try {
//            var res: Cursor = db.getAllData()
//            if (res!=null && res.count>0){
//                while (res.moveToNext()){
//                    stringBuffer.append("Name"+ res.getString(1))
//                }
//                apps.add(stringBuffer.toString())
//            }
//            var database = FirebaseFirestore.getInstance()
//            database.collection("Devices")
//                    .whereEqualTo("Serial", ser)
//                    .addSnapshotListener { p0, p1 ->
//                        if (p0!!.isEmpty) {
//                            Toast.makeText(aContext, "Device is Restarting the Connector", Toast.LENGTH_SHORT).show()
//                        } else {
//                            for (doc in p0!!.documents) {
//                                var app: BlockApplications = doc.toObject(BlockApplications::class.java)
//                                var list = app.BlockApplications
//                                for (i in 0 until list.size) {
//                                    apps.add(list[i])
//                                }
//                            }
//                        }
//                    }
            var res = db.getAllData()

            var jsonString = ""
            var stringBuilder = StringBuilder()
            if (res!= null && res.count > 0 ){
                while (res.moveToNext()){
                    stringBuilder.append(res.getString(1))
                }
                val jsonObj = JSONObject(stringBuilder.toString())
                var jsonArray = jsonObj.getJSONArray("Apps")
                if (jsonArray != null){
                    for (i in 0 until jsonArray.length()){
                        BlockList.add(jsonArray.getString(i))
                    }
                }
            }


//            val json = JSONObject("Apps.db")
//            val items = json.optJSONArray("Apps")
//
//            if (items != null){
//                for (i in 0 until items.length()){
//                    BlockList.add(items.getString(i))
//                }
//            }

//            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
//            val gson = Gson()
//            val json = prefs.getString(key, null)
//            val type = object : TypeToken<ArrayList<String>>() {
//
//            }.type
//            gson.fromJson(json, type)
//            val prefs : SharedPreferences = aContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
//            val gson: Gson = Gson()
//            var json = prefs.getString("key", null)
//            val type: Type = TypeToken<ArrayList<BlockApss>>{}.type

//            val json = JSONObject("Contacs")
//            val items = json.optJSONArray("Apps")
//            for (i in 0 until items.length()){
//                BlockList.add(items.getString(i))
//            }

            val am = aContext
                    .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            val alltasks         = am.runningAppProcesses
            //
           var current = alltasks[0].processName

            for (i in 0 until BlockList.size) {
                var item = BlockList[i].replace(" ", "").toLowerCase()
                if (current.contains(item)) {
                    var intent = Intent(aContext, BlockActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    aContext.startActivity(intent)
                }
            }
                // Used to check for CALL screen
//                if (current == "com.android.contacts") {
//                    // When user on call screen show a alert message
//                    var intent = Intent(aContext, Main3Activity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    aContext.startActivity(intent)
//                    Toast.makeText(aContext, "Phone Call Screen.", Toast.LENGTH_LONG).show()
//                }
//
//                // Used to check for SMS screen
//
//                else if (current == "com.android.mms" || current == "com.android.mms") {
//                    // When user on Send SMS screen show a alert message
//                    Toast.makeText(aContext, "Send SMS Screen.", Toast.LENGTH_LONG).show()
//                }


                // Used to check for CURRENT example main screen

                val packageName = "com.example.checkcurrentrunningapplication"

                if (current== "$packageName.Main") {
                    Toast.makeText(aContext, "Current Example Screen.", Toast.LENGTH_LONG).show()
                }


                // These are showing current running activity in logcat with
                // the use of different methods

                Log.i(TAG, "===============================")
                Log.i(TAG, "aTask.baseActivity: " + current)
                Log.i(TAG, "===============================")

        } catch (t: Throwable) {
            Log.i(TAG, "Throwable caught: " + t.message, t)
        }

    }



}
