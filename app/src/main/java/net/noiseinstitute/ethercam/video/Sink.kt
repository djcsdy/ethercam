package net.noiseinstitute.ethercam.video

import androidx.annotation.WorkerThread

interface Sink {
    @WorkerThread
    fun receive(bytes: ByteArray): Boolean
}