package net.noiseinstitute.ethercam.camera

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import net.noiseinstitute.ethercam.R

private const val PERMISSIONS_REQUEST_CAMERA = 0

class CameraActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private val camera = Camera(this) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSIONS_REQUEST_CAMERA
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    override fun onResume() {
        super.onResume()

        camera.start(findViewById(R.id.previewSurfaceView))
    }

    override fun onPause() {
        super.onPause()

        camera.stop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            camera.start(findViewById(R.id.previewSurfaceView))
        }
    }
}
