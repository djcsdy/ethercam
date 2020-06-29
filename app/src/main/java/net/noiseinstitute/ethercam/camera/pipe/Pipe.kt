package net.noiseinstitute.ethercam.camera.pipe

import android.content.Context
import android.net.LocalSocket
import android.net.LocalSocketAddress
import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import java.io.Closeable
import java.util.*
import kotlin.collections.HashSet

internal class Pipe private constructor(
    private val sender: LocalSocket,
    private val receiver: LocalSocket,
    private val sinks: MutableSet<Sink>
): Closeable {
    val fileDescriptor = sender.fileDescriptor

    companion object {
        @UiThread
        fun open(context: Context): Pipe {
            val name = context.getFileStreamPath("VideoPipe" + UUID.randomUUID()).absolutePath
            val address = LocalSocketAddress(name, LocalSocketAddress.Namespace.FILESYSTEM)

            val receiver = LocalSocket(LocalSocket.SOCKET_STREAM)
            receiver.bind(address)

            val sender = LocalSocket(LocalSocket.SOCKET_STREAM)
            sender.connect(address)

            val sinks = Collections.synchronizedSet(HashSet<Sink>())

            Thread {
                val inputStream = receiver.inputStream.buffered()

                while (!receiver.isClosed) {
                    val bytes = inputStream.readBytes()
                    val iterator = sinks.iterator()
                    for (sink in iterator) {
                        if (!sink.receive(bytes)) {
                            iterator.remove()
                        }
                    }
                }
            }.start()

            return Pipe(sender, receiver, sinks)
        }
    }

    @AnyThread
    fun pipe(sink: Sink) {
        sinks.add(sink)
    }

    @UiThread
    override fun close() {
        receiver.use {
            sender.close()
        }
    }
}