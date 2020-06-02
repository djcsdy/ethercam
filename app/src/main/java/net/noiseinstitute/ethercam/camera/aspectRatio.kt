package net.noiseinstitute.ethercam.camera

import android.hardware.Camera
import android.view.SurfaceView
import androidx.constraintlayout.widget.ConstraintLayout
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

internal fun setAspectRatio(layout: ConstraintLayout, surfaceView: SurfaceView, ratio: Double) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(layout)
    constraintSet.setDimensionRatio(surfaceView.id, "v,$ratio")
    constraintSet.applyTo(layout)
}