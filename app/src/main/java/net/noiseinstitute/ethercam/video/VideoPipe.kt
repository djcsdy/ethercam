package net.noiseinstitute.ethercam.video

import android.content.Context
import android.net.LocalSocket
import android.net.LocalSocketAddress
import java.io.FileDescriptor
import java.util.*

class VideoPipe private constructor (public val fileDescriptor: FileDescriptor) {
    companion object {
        public fun open(context: Context): VideoPipe {
            val name = context.getFileStreamPath("VideoPipe" + UUID.randomUUID()).name
            val address = LocalSocketAddress(name, LocalSocketAddress.Namespace.FILESYSTEM)

            val receiver = LocalSocket(LocalSocket.SOCKET_STREAM)
            receiver.bind(address)

            val sender = LocalSocket(LocalSocket.SOCKET_STREAM)
            sender.connect(address)

            return VideoPipe(sender.fileDescriptor)
        }
    }
}