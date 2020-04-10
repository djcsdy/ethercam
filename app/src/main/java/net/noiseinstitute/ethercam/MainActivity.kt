package net.noiseinstitute.ethercam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.*
import java.net.ServerSocket

class MainActivity : AppCompatActivity() {
    private val port = 8080

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread(Runnable {
            while (true) {
                try {
                    ServerSocket(port).use { socket ->
                        while (true) {
                            socket.accept().use { connection ->
                                val inputReader = BufferedReader(
                                    InputStreamReader(
                                        connection.getInputStream()
                                    )
                                )

                                val request = inputReader.readLine()
                                while (inputReader.readLine() != "") {
                                }

                                BufferedWriter(
                                    OutputStreamWriter(
                                        connection.getOutputStream(), "UTF-8"
                                    )
                                ).use { outputWriter ->
                                    if (request == null || !request.startsWith("GET ")
                                        || !(request.endsWith("HTTP/1.0")
                                                || request.endsWith("HTTP/1.1"))
                                    ) {
                                        outputWriter.write("HTTP/1.0 400 Bad Request\r\n\r\n")
                                    } else {
                                        outputWriter.write("HTTP/1.0 200 OK\r\n")
                                        outputWriter.write("Content-Type: text/plain\r\n")
                                        outputWriter.write("\r\n")
                                        outputWriter.write("Hello, World!")
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Ethercam", "Socket error", e)
                }
            }
        }).start()
    }
}
