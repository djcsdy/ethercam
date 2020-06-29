package net.noiseinstitute.ethercam.camera.pipe

import androidx.annotation.WorkerThread

interface Sink {
    @WorkerThread
    fun receive(bytes: ByteArray): Boolean
}