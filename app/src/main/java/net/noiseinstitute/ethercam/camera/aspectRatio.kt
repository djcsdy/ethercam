package net.noiseinstitute.ethercam.camera

import android.hardware.Camera
import androidx.constraintlayout.widget.ConstraintSet

internal fun calculateAspectRatio(
    previewSize: Camera.Size,
    displayOrientation: Int
): Double {
    return when (displayOrientation) {
        0, 180 -> previewSize.width.toDouble() / previewSize.height.toDouble()
        else -> previewSize.height.toDouble() / previewSize.width.toDouble()
    }
}

internal fun setAspectRatio(view: CameraView, ratio: Double) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(view.layout)
    constraintSet.setDimensionRatio(view.viewId, ratio.toString())
    constraintSet.applyTo(view.layout)
}