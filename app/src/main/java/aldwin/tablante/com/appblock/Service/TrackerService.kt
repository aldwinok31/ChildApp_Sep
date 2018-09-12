package aldwin.tablante.com.appblock.Service

import aldwin.tablante.com.appblock.Activity.*
import aldwin.tablante.com.appblock.BlockingApps.BlockApplications
import aldwin.tablante.com.appblock.Commands.*
import aldwin.tablante.com.appblock.BlockingApps.DataBaseHelper
import aldwin.tablante.com.appblock.Model.*
import android.app.Service
import android.content.*
import android.os.*
import android.widget.Toast
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import android.os.PowerManager
import org.json.JSONArray
import org.json.JSONObject


class TrackerService : Service() {
    lateinit var db: DataBaseHelper
    val DATABASE_NAME = "Apps.db"
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag")
        wl.acquire()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var device = android.os.Build.SERIAL
        GetCurrentLocations().requestLocationUpdates(applicationContext, device)

        //GetParentDevices().fetchparent(device,applicationContext)
        var db = FirebaseFirestore.getInstance()
        var app: ArrayList<String> = ArrayList()
        var pairRequest: ArrayList<String> = ArrayList()
        var mmap: HashMap<String, Any?> = HashMap()
        db.collection("Devices").document(device).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot.exists()){ null}
            else{
                mmap.put("Serial", device)
                mmap.put("BootDevice", false)
                mmap.put("Screenshot", false)
                mmap.put("CaptureCam", false)
                mmap.put("TriggerAlarm", false)
                mmap.put("Messages", "")
                mmap.put("Applications", app)
                mmap.put("Request", pairRequest)
                mmap.put("AppPermit", false)
                mmap.put("KillApp", "")
                db.collection("Devices")
                        .document(device)
                        .set(mmap)

            }


        }


        db.collection("Devices")
                .whereEqualTo("Serial", device)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                        for (doc in p0!!.documents) {
                            var devicet = doc.toObject(ConsoleCommand::class.java)

                            if (devicet.CaptureCam) {
                                db.collection("Devices").document(doc.id).update("CaptureCam", false)
                                //CaptureCam().openFrontCamera(doc.id,device,applicationContext)

                                var intent = Intent(applicationContext,RequestPicture::class.java)
                                intent.putExtra("id",doc.id)
                                intent.putExtra("serial",device)
                                startActivity(intent)
                            }

                            if (devicet.Screenshot) {
                                db.collection("Devices").document(doc.id).update("Screenshot", false)

                                ScreenShot().doshot(doc.id, applicationContext)

                            }

                            if (devicet.TriggerAlarm) {

                                TriggerAlarm().playAlarm(applicationContext)
                                db.collection("Devices").document(doc.id).update("TriggerAlarm", false)
                            }
                            if (!devicet.Messages.equals("")) {

                               // NotifyMsg().alertMsg(applicationContext, doc.id, devicet.Messages)

                                var intent = Intent(applicationContext,MessageReciever::class.java)
                                intent.putExtra("id",doc.id)
                                intent.putExtra("msg",devicet.Messages)
                                startActivity(intent)

                            }

                            if (devicet.AppPermit) {
                                var id = doc.id
                                var applist = GetRunningApps().sendData(applicationContext, device)
                                db.collection("Devices").document(id).update("AppPermit", false
                                )
                                var d = FirebaseFirestore.getInstance()
                                d.collection("Devices")
                                        .document(id)
                                        .update("Applications", applist)


                            }



                        }
                    }
                })

        var rmap: HashMap<String, Any?> = HashMap()
        rmap.put("ID", "")
        rmap.put("Name", "")

        // Request Pairing
        db.collection("Requests")
                .whereEqualTo("ID", device)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {

                    override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                        for (doc in p0!!.documents) {
                            var dev = doc.toObject(Requests::class.java)
                         //  NotifyMsg().alertPairing(applicationContext, device, dev.Name, dev.RequestID)
                            var intent = Intent(this@TrackerService,RequestReciever::class.java)
                            intent.putExtra("serial",device)
                            intent.putExtra("name",dev.Name)
                            intent.putExtra("requestid",dev.RequestID)
                            startActivity(intent)

                        }
                    }
                })


        db.collection("Timers")
                .whereEqualTo("ID", device)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                        if (!p0!!.isEmpty) {
                            for (doc in p0!!.documents) {

                                var hour = doc.get("Hour").toString().toInt()
                                var min = doc.get("Minute").toString().toInt()

                                if (hour != 0 || min != 0) {

                                    Timer().setTimer(applicationContext,hour,min)
                                }
                            }

                        }
                    }
                })


        //Request Image
        db.collection("RequestImage")
                .whereEqualTo("Serial", device)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {

                    override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                        for (doc in p0!!.documents) {
                            var dev = doc.toObject(Requests::class.java)
                            CaptureCam().openFrontCamera(dev.RequestID,device,applicationContext)

                        }
                    }
                })

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        var intent = Intent("com.android.ServiceStopped")
        sendBroadcast(intent)
        var intent3 = Intent(this@TrackerService,MainActivity::class.java)

        startActivity(intent3)

        super.onTaskRemoved(rootIntent)
    }


    override fun onDestroy() {
        super.onDestroy()
        var intent2 = Intent(this@TrackerService, TrackerService::class.java)
                .setAction("enable_capture")


        startService(intent2)

    }

    fun saveData(){
        var blockList: ArrayList<String> = arrayListOf()

        db = DataBaseHelper(this, DATABASE_NAME, null, 1)

        var ser = Build.SERIAL
        var database = FirebaseFirestore.getInstance()
        database.collection("Devices")
                .whereEqualTo("Serial", ser)
                .addSnapshotListener { p0, p1 ->
                    if (p0!!.isEmpty) {
                        Toast.makeText(applicationContext, "Device is Restarting the Connector", Toast.LENGTH_SHORT).show()
                    } else {
                        for (doc in p0!!.documents) {
                            var result: Boolean = false
                            var app: BlockApplications = doc.toObject(BlockApplications::class.java)
                            var list = app.BlockApplications
                            if (list.isNotEmpty()) {
                                val json = JSONObject()
                                json.put("Apps", JSONArray(list))
                                val arrayList = json.toString()
                                db.insertData(arrayList)
                                break
                            }
                        }
                    }
                }

    }

}


