package net.noiseinstitute.ethercam.camera

import android.hardware.Camera
import android.util.Log
import java.lang.RuntimeException

fun setParameters(camera: Camera): Unit {
    val parameters = camera.parameters

    val defaultFocusMode = parameters.focusMode

    listOf(
        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
        Camera.Parameters.FOCUS_MODE_EDOF,
        Camera.Parameters.FOCUS_MODE_INFINITY
    )
        .firstOrNull { parameters.supportedFocusModes.contains(it) }
        ?.let {
            parameters.focusMode = it

            try {
                camera.parameters = parameters
            } catch (e: RuntimeException) {
                Log.w("Ethercam", "Failed to set focus mode", e)
                parameters.focusMode = defaultFocusMode
            }
        }
}