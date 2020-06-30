package net.noiseinstitute.ethercam.camera

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import net.noiseinstitute.ethercam.R

class CameraActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private companion object {
        private const val PERMISSIONS_REQUEST_CAMERA = 0
    }

    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (camera == null) {
            camera = Camera.open(this) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSIONS_REQUEST_CAMERA
                )
            }
        }

        setContentView(R.layout.activity_camera)
    }

    override fun onDestroy() {
        super.onDestroy()

        camera?.close()
        camera = null
    }

    override fun onResume() {
        super.onResume()

        camera?.start(findViewById(R.id.constraintLayout), findViewById(R.id.previewSurfaceView))
    }

    override fun onPause() {
        super.onPause()

        camera?.stop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            camera?.start(findViewById(R.id.constraintLayout), findViewById(R.id.previewSurfaceView))
        }
    }
}
