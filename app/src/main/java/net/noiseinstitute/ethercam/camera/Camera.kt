package net.noiseinstitute.ethercam.camera

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

internal class Camera(
    private val activity: Activity,
    private val requestPermission: () -> Unit
) {
    private var camera: android.hardware.Camera? = null
    private var view: CameraView? = null

    private val surfaceHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(
            surfaceHolder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            this@Camera.stopCamera()
            if (surfaceHolder != null) {
                this@Camera.startCamera()
            }
        }

        override fun surfaceDestroyed(surfaceHolder: SurfaceHolder?) {
            this@Camera.stopCamera()
        }

        override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
            if (surfaceHolder != null) {
                this@Camera.startCamera()
            }
        }
    }

    fun start(layout: ConstraintLayout, viewId: Int) {
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

        camera?.let { camera ->
            setFocusMode(camera)
            val displayOrientation = calculateDisplayOrientation(activity)
            camera.setDisplayOrientation(displayOrientation)
            view?.let { view ->
                val previewSize = camera.parameters.previewSize
                val aspectRatio = calculateAspectRatio(previewSize, displayOrientation)
                setAspectRatio(view, aspectRatio)
                camera.setPreviewDisplay(view.surfaceHolder)
                camera.startPreview()
            }
        }
    }

    private fun stopCamera() {
        camera?.release()
        camera = null
    }
}