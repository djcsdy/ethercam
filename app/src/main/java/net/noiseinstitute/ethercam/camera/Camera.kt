package net.noiseinstitute.ethercam.camera

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.os.Handler
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal class Camera(
    private val activity: Activity,
    private val requestPermission: () -> Unit
) {
    private var camera: android.hardware.Camera? = null
    private var view: CameraView? = null

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

    private val surfaceHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(
            surfaceHolder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
        }

        override fun surfaceDestroyed(surfaceHolder: SurfaceHolder?) {
            this@Camera.stopCamera()
        }

        override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
            this@Camera.startCamera()
        }
    }

    fun start(layout: ConstraintLayout, viewId: Int) {
        val displayManager = ActivityCompat.getSystemService(activity, DisplayManager::class.java)
        displayManager?.registerDisplayListener(displayListener, Handler())

        if (view == null) {
            val surfaceView = layout.findViewById<SurfaceView>(viewId)
            val surfaceHolder = surfaceView.holder
            surfaceHolder.addCallback(surfaceHolderCallback)

            view = object :
                CameraView {
                override val layout = layout
                override val viewId = viewId
                override val surfaceHolder = surfaceHolder
            }
        }
    }

    fun stop() {
        val displayManager = ActivityCompat.getSystemService(activity, DisplayManager::class.java)
        displayManager?.unregisterDisplayListener(displayListener)

        if (view != null) {
            stopCamera()
            view?.surfaceHolder?.removeCallback(surfaceHolderCallback)
            view = null
        }
    }

    private fun startCamera() {
        if (
            camera == null
            && ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
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
            view?.let { view ->
                camera.setPreviewDisplay(view.surfaceHolder)
                camera.startPreview()
            }
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
            view?.let { view ->
                val previewSize = camera.parameters.previewSize
                val aspectRatio = calculateAspectRatio(previewSize, displayOrientation)
                setAspectRatio(view, aspectRatio)
            }
        }
    }
}