package net.noiseinstitute.ethercam

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.noiseinstitute.ethercam.http_server.HttpServer

const val PERMISSIONS_REQUEST_CAMERA = 0

class CameraActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    override fun onStart() {
        super.onStart()

        HttpServer().start()
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSIONS_REQUEST_CAMERA
            )
        }
    }

    override fun onPause() {
        super.onPause()

        camera?.release()
        camera = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            }
        }
    }

    private fun startCamera() {
        if (camera == null) {
            camera = Camera.open()
        }

        // TODO camera parameters
        // TODO camera orientation
        camera?.setPreviewDisplay(findViewById<SurfaceView>(R.id.previewSurfaceView).holder)
        camera?.startPreview()
    }
}
