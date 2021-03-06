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
import net.noiseinstitute.ethercam.camera.parameters.calculateAspectRatio
import net.noiseinstitute.ethercam.camera.parameters.calculateDisplayOrientation
import net.noiseinstitute.ethercam.camera.parameters.setAspectRatio
import net.noiseinstitute.ethercam.camera.parameters.setFocusMode
import net.noiseinstitute.ethercam.camera.pipe.Pipe
import net.noiseinstitute.ethercam.camera.pipe.Sink
import java.io.Closeable

internal class Camera private constructor(
    private val pipe: Pipe,
    private val activity: Activity,
    private val requestPermission: () -> Unit
) : Closeable {
    companion object {
        fun open(activity: Activity, requestPermission: () -> Unit): Camera {
            val pipe = Pipe.open(activity)
            return Camera(pipe, activity, requestPermission)
        }
    }

    private var camera: android.hardware.Camera? = null
    private var layout: ConstraintLayout? = null
    private var previewSurfaceView: SurfaceView? = null

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

    private val previewSurfaceCallback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            startPreview()
        }
    }

    fun start(layout: ConstraintLayout, surfaceView: SurfaceView) {
        stop()

        this.layout = layout
        this.previewSurfaceView = surfaceView

        val displayManager = ActivityCompat.getSystemService(activity, DisplayManager::class.java)
        displayManager?.registerDisplayListener(displayListener, Handler())

        startCamera()
    }

    fun stop() {
        stopCamera()

        val displayManager = ActivityCompat.getSystemService(activity, DisplayManager::class.java)
        displayManager?.unregisterDisplayListener(displayListener)

        layout = null
        previewSurfaceView = null
    }

    fun pipe(sink: Sink) {
        pipe.pipe(sink)
    }

    override fun close() {
        pipe.use {
            stop()
        }
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
        camera?.let {
            setFocusMode(
                it
            )
        }
        startPreview()

        previewSurfaceView?.holder?.addCallback(previewSurfaceCallback)
    }

    private fun stopCamera() {
        previewSurfaceView?.holder?.removeCallback(previewSurfaceCallback)

        camera?.release()
        camera = null
    }

    private fun updateOrientation() {
        camera?.let { camera ->
            val displayOrientation =
                calculateDisplayOrientation(
                    activity
                )
            camera.setDisplayOrientation(displayOrientation)
            val previewSize = camera.parameters.previewSize
            layout?.let { layout ->
                previewSurfaceView?.let { surfaceView ->
                    val aspectRatio =
                        calculateAspectRatio(
                            previewSize,
                            displayOrientation
                        )
                    setAspectRatio(
                        layout,
                        surfaceView,
                        aspectRatio
                    )
                }
            }
        }
    }

    private fun startPreview() {
        camera?.let { camera ->
            previewSurfaceView?.let { camera.setPreviewDisplay(it.holder) }
            camera.startPreview()
        }
    }
}