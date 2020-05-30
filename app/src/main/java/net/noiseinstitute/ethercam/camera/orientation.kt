package net.noiseinstitute.ethercam.camera

import android.app.Activity
import android.hardware.Camera
import android.view.Surface

internal fun calculateDisplayOrientation(activity: Activity, camera: Camera): Int {
    val info = Camera.CameraInfo()
    Camera.getCameraInfo(0, info)

    val displayRotation = when (activity.windowManager.defaultDisplay.rotation) {
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> 0
    }

    return when (info.facing) {
        Camera.CameraInfo.CAMERA_FACING_FRONT -> (720 - info.orientation - displayRotation) % 360
        else -> (360 + info.orientation - displayRotation) % 360
    }
}