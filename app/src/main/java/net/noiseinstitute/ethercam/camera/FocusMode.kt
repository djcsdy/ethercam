package net.noiseinstitute.ethercam.camera

sealed class FocusMode {
    object Macro : FocusMode()
    object Normal : FocusMode()
}