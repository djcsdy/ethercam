package net.noiseinstitute.ethercam.video

import android.content.Context
import android.net.LocalSocket
import android.net.LocalSocketAddress
import java.io.Closeable
import java.util.*

class VideoPipe private constructor(
    private val sender: LocalSocket,
    private val receiver: LocalSocket
): Closeable {
    val fileDescriptor = sender.fileDescriptor

    companion object {
        fun open(context: Context): VideoPipe {
            val name = context.getFileStreamPath("VideoPipe" + UUID.randomUUID()).name
            val address = LocalSocketAddress(name, LocalSocketAddress.Namespace.FILESYSTEM)

            val receiver = LocalSocket(LocalSocket.SOCKET_STREAM)
            receiver.bind(address)

            val sender = LocalSocket(LocalSocket.SOCKET_STREAM)
            sender.connect(address)

            Thread {
                val inputStream = receiver.inputStream.buffered()

                while (!receiver.isClosed) {
                    inputStream.readBytes()
                }
            }.start()

            return VideoPipe(sender, receiver)
        }
    }

    override fun close() {
        receiver.use {
            sender.close()
        }
    }
}