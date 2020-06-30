package net.noiseinstitute.ethercam.camera.pipe

import android.content.Context
import android.net.LocalServerSocket
import android.net.LocalSocket
import android.net.LocalSocketAddress
import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import java.io.Closeable
import java.io.IOException
import java.util.*
import kotlin.collections.HashSet

internal class Pipe private constructor(
    private val server: LocalSocket,
    private val client: LocalSocket,
    private val sinks: MutableSet<Sink>
) : Closeable {
    val fileDescriptor = client.fileDescriptor

    companion object {
        @UiThread
        fun open(context: Context): Pipe {
            val name = context
                .getFileStreamPath("Pipe-" + UUID.randomUUID())
                .absolutePath
            val address = LocalSocketAddress(name, LocalSocketAddress.Namespace.FILESYSTEM)
            val server = LocalSocket(LocalSocket.SOCKET_STREAM)
            server.bind(address)
            val serverWrapper = LocalServerSocket(server.fileDescriptor)

            val client = LocalSocket(LocalSocket.SOCKET_STREAM)
            client.connect(address)

            val sinks = Collections.synchronizedSet(HashSet<Sink>())

            Thread {
                serverWrapper.use { server ->
                    val receiver = server.accept()
                    val inputStream = receiver.inputStream
                    val buffer = ByteArray(188)

                    var byteCount = inputStream.read(buffer)
                    while (byteCount != -1) {
                        val bytes = buffer.sliceArray(0 until byteCount)
                        val iterator = sinks.iterator()
                        for (sink in iterator) {
                            if (!sink.receive(bytes)) {
                                iterator.remove()
                            }
                        }
                        byteCount = inputStream.read(buffer)
                    }
                }
            }.start()

            return Pipe(server, client, sinks)
        }
    }

    @AnyThread
    fun pipe(sink: Sink) {
        sinks.add(sink)
    }

    @UiThread
    override fun close() {
        server.use {
            try {
                server.shutdownInput()
            } catch (ignore: IOException) {
            }
            try {
                server.shutdownOutput()
            } catch (ignore: IOException) {
            }
            try {
                client.shutdownOutput()
            } catch (ignore: IOException) {
            }
            try {
                client.shutdownInput()
            } catch (ignore: IOException) {
            }
            client.close()
        }
    }
}