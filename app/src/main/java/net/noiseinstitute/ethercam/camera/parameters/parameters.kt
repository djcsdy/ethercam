package net.noiseinstitute.ethercam.camera.parameters

import android.hardware.Camera

internal sealed class TrySetParametersResult {
    object Success : TrySetParametersResult()
    data class Error(val error: RuntimeException) : TrySetParametersResult()
}

internal fun trySetParameters(
    camera: Camera,
    set: (parameters: Camera.Parameters) -> Unit
): TrySetParametersResult {
    val parameters = camera.parameters
    set(parameters)
    try {
        camera.parameters = parameters
    } catch (e: RuntimeException) {
        return TrySetParametersResult.Error(
            e
        )
    }
    return TrySetParametersResult.Success
}