package net.noiseinstitute.ethercam.camera

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.os.Handler
import android.view.SurfaceView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal class Camera(
    private val activity: Activity,
    private val requestPermission: () -> Unit
) {
    private var camera: android.hardware.Camera? = null
    private var layout: ConstraintLayout? = null
    private var surfaceView: SurfaceView? = null

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayChanged(displayId: Int) {
            if (displayId == activity.windowManager.defaultDisplay.displayId) {
                updateOrientation()
            }
        }

        override fun onDisplayAdded(displayId: Int) {
        }

        override fun onDisplayRemoved(displayId: Int) {
        }
    }

    fun start(layout: ConstraintLayout, surfaceView: SurfaceView) {
        stop()

        this.layout = layout
        this.surfaceView = surfaceView

        val displayManager = ActivityCompat.getSystemService(activity, DisplayManager::class.java)
        displayManager?.registerDisplayListener(displayListener, Handler())

        startCamera()
    }

    fun stop() {
        layout = null
        surfaceView = null

        val displayManager = ActivityCompat.getSystemService(activity, DisplayManager::class.java)
        displayManager?.unregisterDisplayListener(displayListener)
        stopCamera()
    }

    private fun startCamera() {
        if (
            camera == null
            && ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            stop()
        } else {
            startCameraWithPermission()
        }
    }

    private fun startCameraWithPermission() {
        if (camera == null) {
            camera = android.hardware.Camera.open()
        }

        updateOrientation()

        camera?.let { camera ->
            setFocusMode(camera)
            surfaceView?.let { camera.setPreviewDisplay(it.holder) }
            camera.startPreview()
        }
    }

    private fun stopCamera() {
        camera?.release()
        camera = null
    }

    private fun updateOrientation() {
        camera?.let { camera ->
            val displayOrientation = calculateDisplayOrientation(activity)
            camera.setDisplayOrientation(displayOrientation)
            val previewSize = camera.parameters.previewSize
            layout?.let {layout ->
                surfaceView?.let {surfaceView ->
                    val aspectRatio = calculateAspectRatio(previewSize, displayOrientation)
                    setAspectRatio(layout, surfaceView, aspectRatio)
                }
            }
        }
    }
}