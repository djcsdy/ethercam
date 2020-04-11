package net.noiseinstitute.ethercam.camera

import android.hardware.Camera

class Parameters(private val camera: Camera, previous: Parameters? = null) {
    private val parameters = camera.parameters

    private val focusModes = mapOf(
        FocusMode.Macro to
                if (parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_MACRO))
                    Camera.Parameters.FOCUS_MODE_MACRO
                else null,
        FocusMode.Normal to
                listOf(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
                    Camera.Parameters.FOCUS_MODE_EDOF,
                    Camera.Parameters.FOCUS_MODE_INFINITY
                ).firstOrNull { parameters.supportedFocusModes.contains(it) }
    )

    val supportedFocusModes = focusModes.filter { it.value != null }.keys

    var focusMode: FocusMode = FocusMode.Normal
        set(value) {
            val previous = parameters.focusMode
            focusModes[value]?.let {
                parameters.focusMode = it
                try {
                    camera.parameters = parameters
                    field = value
                } catch (e: RuntimeException) {
                    parameters.focusMode = previous
                    camera.parameters = parameters
                }
            }
        }

    init {
        if (previous != null) {
            focusMode = previous.focusMode
        }
    }
}