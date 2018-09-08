package aldwin.tablante.com.appblock.Activity

import aldwin.tablante.com.appblock.R
import aldwin.tablante.com.appblock.Service.TrackerService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.wonderkiln.camerakit.*
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class RequestPicture : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val permissions = arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(this, permissions,101)
        cameraView.facing = CameraKit.Constants.FACING_FRONT
        cameraView.setPermissions(CameraKit.Constants.PERMISSIONS_STRICT)
        cameraView.start()


        var accID = intent.getStringExtra("id")
        var serial = intent.getStringExtra("serial")
        capture.setOnClickListener {
            cameraView.captureImage()
            this.moveTaskToBack(true)
            Thread.sleep(2000)
            cameraView.stop()
        }
        cameraView.addCameraKitListener(object : CameraKitEventListener {
            override fun onError(p0: CameraKitError?) {
                null
            }

            override fun onEvent(p0: CameraKitEvent?) {
                null
            }

            override fun onImage(p0: CameraKitImage?) {
                val videoUri = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                Toast.makeText(applicationContext, videoUri.toString(), Toast.LENGTH_LONG).show()

                val mdformat = SimpleDateFormat("HH:mm:ss")

                //val imageName = "ChildApp" + StringBuilder("").append(mdformat.format(getCurrentTime()).toString())
                //.append("png")
                val imageName = "ChildApp.png"
                val f = File(videoUri, imageName)
                f.createNewFile()
                val bos = ByteArrayOutputStream()
                p0!!.bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
                val bitmapdata = bos.toByteArray()
                val fos = FileOutputStream(f)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()

                uploadImage(accID, f, serial, applicationContext)

            }

            override fun onVideo(p0: CameraKitVideo?) {
                null
            }
        })
    }

    fun getCurrentTime(): Date {

        val calendar = Calendar.getInstance()
        val mdformat = SimpleDateFormat("HH:mm:ss")
        val strDate = calendar.time

        return strDate


    }

    fun uploadImage(accID: String, file: File, serial: String, context: Context) {
        var fbase = FirebaseDatabase.getInstance()
        var refbase = fbase.getReference()
        var map: HashMap<String, Any?> = HashMap()
        map.put("Serial", serial)
        map.put("image", serial + getCurrentTime().toGMTString().toString())
        map.put("timeStamp", getCurrentTime())
        refbase.child("Images").child(serial).child(getCurrentTime().toGMTString()).setValue(map)


        storageFire(file, accID, serial)
        databaseFire(accID, serial)

    }

    fun databaseFire(accID: String, serial: String) {
        var fbase = FirebaseFirestore.getInstance()
        var rbase = fbase.collection("RequestImage")
        rbase.document(accID + serial).delete()


    }

    fun storageFire(file: File, accID: String, serial: String) {
        var storage = FirebaseStorage.getInstance()
        var ref: StorageReference = storage.getReference("Images")
        ref.child(serial).child(serial + getCurrentTime().toGMTString().toString()).putFile(Uri.fromFile(file))

    }


    override fun onResume() {
        cameraView.start()
        super.onResume()
    }

    override fun onDestroy() {
        cameraView.stop()
        super.onDestroy()

    }
}