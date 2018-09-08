package aldwin.tablante.com.appblock.Model

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*

class GetCurrentLocations {


    fun requestLocationUpdates(context: Context, id: String) {
        val request = LocationRequest()
        request.interval = 10000
        request.fastestInterval = 5000

        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val blue: BluetoothAdapter
        blue = BluetoothAdapter.getDefaultAdapter()
        val bluetoothName = blue.name
        val client = LocationServices.getFusedLocationProviderClient(context)
        val model = android.os.Build.MODEL
        val serial = android.os.Build.SERIAL
        var database: FirebaseDatabase
        var dataref: DatabaseReference
        database = FirebaseDatabase.getInstance()
        var device = childDevice(serial, bluetoothName)

            dataref = database.getReference("Devices").child(serial)

            dataref.addValueEventListener(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {
                      Log.d("Error","Cancelled")
                 }

                override fun onDataChange(p0: DataSnapshot?) {
                    if(!p0!!.hasChild("ParentList")) {
                        dataref.setValue(device)
                    }
                }
                })



        val permission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {

            request.setFastestInterval(5000)
                    .setInterval(10000)
            request.fastestInterval = 5000
            request.interval = 10000

    Toast.makeText(context.applicationContext,id,Toast.LENGTH_SHORT).show()
            client.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    val location = locationResult!!.lastLocation

                    if (location != null) {
                        var mmap : HashMap<String,Any?> = HashMap()
                        mmap.put("Locations",location)
                        dataref.updateChildren(mmap)



                    }

                }
            }

                    , null)
        } else {
            Toast.makeText(context, " Not Found", Toast.LENGTH_LONG).show()

        }
        
    }
}