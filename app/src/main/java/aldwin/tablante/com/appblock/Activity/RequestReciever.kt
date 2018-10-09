package aldwin.tablante.com.appblock.Activity

import aldwin.tablante.com.appblock.R
import aldwin.tablante.com.appblock.Service.TrackerService
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_request.*

class RequestReciever : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)


        var serial = intent.getStringExtra("serial")
        var requestid = intent.getStringExtra("requestid")
        var name = intent.getStringExtra("name")
        textView4.setText(name.toString())
        accept.setOnClickListener {


            var db = FirebaseFirestore.getInstance()
            db.collection("Requests")
                    .document(requestid + "+" + serial)
                    .delete()

            addDevice(requestid,serial,name)


        }
        decline.setOnClickListener {


            var db = FirebaseFirestore.getInstance()
            db.collection("Requests")
                    .document(requestid + "+" + serial)
                    .delete()
        }

    }

    fun addDevice(accID: String, serial: String, name: String) {
        val blue = BluetoothAdapter.getDefaultAdapter()
        val bluetoothName = blue.name
        var data = FirebaseDatabase.getInstance()
        var ref = data.getReference("Accounts").child(accID).child("Devices").child(serial)
        var devref = data.getReference("Devices").child(serial).child("ParentList").child(accID).child("Connection")

        var mmap: HashMap<String, Any?> = HashMap()

        mmap.put("id", serial)
        mmap.put("myId", accID)
        mmap.put("myName", name)
        mmap.put("name", bluetoothName)
        ref.setValue(mmap)


        devref.setValue("Paired")

        this.moveTaskToBack(true)
        root.visibility = View.GONE
        var ser = Intent(applicationContext, TrackerService::class.java)
        startService(ser)


    }
}