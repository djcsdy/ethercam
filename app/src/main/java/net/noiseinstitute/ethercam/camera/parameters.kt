package net.noiseinstitute.ethercam.camera

import android.hardware.Camera

sealed class TrySetParametersResult {
    object Success : TrySetParametersResult()
    data class Error(val error: RuntimeException) : TrySetParametersResult()
}

fun trySetParameters(
    camera: Camera,
    set: (parameters: Camera.Parameters) -> Unit
): TrySetParametersResult {
    val parameters = camera.parameters
    set(parameters)
    try {
        camera.parameters = parameters
    } catch (e: RuntimeException) {
        return TrySetParametersResult.Error(e)
    }
    return TrySetParametersResult.Success
}