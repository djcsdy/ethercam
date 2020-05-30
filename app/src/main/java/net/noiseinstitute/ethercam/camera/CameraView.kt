package net.noiseinstitute.ethercam.camera

import android.view.SurfaceHolder
import androidx.constraintlayout.widget.ConstraintLayout

internal interface CameraView {
    val layout: ConstraintLayout
    val viewId: Int
    val surfaceHolder: SurfaceHolder
}