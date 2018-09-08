package aldwin.tablante.com.appblock.Model

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class GetRunningApps {
    lateinit var  activityManager: ActivityManager
   lateinit var RAP : List<ActivityManager.RunningAppProcessInfo>


    fun sendData(context:Context,serial:String) : ArrayList<String> {
        var applist: ArrayList<String> = ArrayList()

        activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        RAP = activityManager.runningAppProcesses

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Devices")
        val userRef = ref.child(serial).child("RunningApps")
        for(processInfo in RAP){
            applist.add(processInfo.processName)
        }



        val db = FirebaseFirestore.getInstance()
        db.collection("Device")
                .document(serial)
                .update("Applications",applist)

        userRef.setValue(applist)

  return applist

    }

    fun terminate(app:String){}
}