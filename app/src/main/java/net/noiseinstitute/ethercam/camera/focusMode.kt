package net.noiseinstitute.ethercam.camera

import android.hardware.Camera
import android.util.Log

fun setFocusMode(camera: Camera) {
    val parameters = camera.parameters

    listOf(
        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
        Camera.Parameters.FOCUS_MODE_EDOF,
        Camera.Parameters.FOCUS_MODE_INFINITY
    )
        .firstOrNull { parameters.supportedFocusModes.contains(it) }
        ?.let { focusMode ->
            when (val result =
                trySetParameters(camera) {
                    it.focusMode = focusMode
                }) {
                is TrySetParametersResult.Error -> Log.w(
                    "Ethercam",
                    "Failed to set focus mode",
                    result.error
                )
                else -> null
            }
        }
}