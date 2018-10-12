package aldwin.tablante.com.appblock.Activity

import aldwin.tablante.com.appblock.R
import aldwin.tablante.com.appblock.Service.TrackerService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
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
    var aID = ""
    var sID= ""
    var anim : AnimationDrawable? = null

    var bmap : Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val permissions = arrayOf(android.Manifest.permission.CAMERA
        )
        ActivityCompat.requestPermissions(this, permissions,0)

        cameraView.facing = CameraKit.Constants.FACING_FRONT

        cameraView.start()
        logoSplash.setBackgroundResource(R.drawable.loading)




        var serial = intent.getStringExtra("serial")
        var db = FirebaseFirestore.getInstance()
        var accID = ""



        capture.setOnClickListener {
            cameraView.captureImage()
           capture.visibility = View.GONE
            splashScreen.visibility = View.VISIBLE
            anim = logoSplash.background as AnimationDrawable
            anim!!.start()
            //loader().execute()
           //this.moveTaskToBack(true)


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


               val imageName = "ChildApp.png"
                val f = File(videoUri, imageName)
                f.createNewFile()

                val bos = ByteArrayOutputStream()

                bmap = p0!!.bitmap
                p0!!.bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)

                val bitmapdata = bos.toByteArray()
                Thread.sleep(2000)
                val fos = FileOutputStream(f)

                fos.write(bitmapdata)
                fos.flush()
                fos.close()

              // aID = accID
             //   sID = serial
              //  loader().execute(videoUri)
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


        storageFire(file, accID, serial)


    }



    fun storageFire(file: File, accID: String, serial: String) {
        var timer =  serial + getCurrentTime().toGMTString().toString()
        var storage = FirebaseStorage.getInstance()
        var ref: StorageReference = storage.getReference("Images")
        ref.child(serial).child(timer).putFile(Uri.fromFile(file))
                .addOnSuccessListener {


                     ref.child(serial).child(timer).downloadUrl.addOnSuccessListener {v->

                         var fbase = FirebaseDatabase.getInstance()
                         var refbase = fbase.getReference()
                         var map: HashMap<String, Any?> = HashMap()
                         map.put("Serial", serial)
                         map.put("image", timer.toString())
                         map.put("url",v.toString())
                         refbase.child("Images").child(serial).child("ChildImage").setValue(map)


                     }


                          .addOnFailureListener { Toast.makeText(applicationContext,"fail",Toast.LENGTH_LONG).show() }
                    //databaseFire(accID, serial,url)




                    anim!!.stop()
                    cameraView.stop()
                   var intent = Intent(applicationContext,MainActivity::class.java)
                   startActivity(intent)

        }

    }


    override fun onResume() {
        cameraView.start()
        super.onResume()
    }

    override fun onDestroy() {
        cameraView.stop()
        super.onDestroy()

    }


    inner class loader : AsyncTask<File, Void, Void>() {
        override fun onPreExecute() {
            super.onPreExecute()



        }

        override fun doInBackground(vararg p0: File?): Void? {

          Thread.sleep(5000)
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(applicationContext,RequestPicture::class.java)
        intent.putExtra("id",intent.getStringExtra("id"))
        intent.putExtra("serial",intent.getStringExtra("serial"))
        startActivity(intent)
    }
}