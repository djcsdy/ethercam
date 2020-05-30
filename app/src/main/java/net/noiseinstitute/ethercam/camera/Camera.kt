package net.noiseinstitute.ethercam.camera

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat

internal class Camera(
    private val activity: Activity,
    private val requestPermission: () -> Unit
) {
    private var camera: android.hardware.Camera? = null
    private var surfaceHolder: SurfaceHolder? = null

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

    fun start(surfaceView: SurfaceView) {
        if (surfaceHolder == null) {
            surfaceHolder = surfaceView.holder
            surfaceHolder?.addCallback(surfaceHolderCallback)
        }
    }

    fun stop() {
        if (surfaceHolder != null) {
            stopCamera()
            surfaceHolder?.removeCallback(surfaceHolderCallback)
            surfaceHolder = null
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

        camera?.let {
            setFocusMode(it)
            setOrientation(activity, it)

            it.setPreviewDisplay(surfaceHolder)
            it.startPreview()
        }
    }

    private fun stopCamera() {
        camera?.release()
        camera = null
    }
}