package net.noiseinstitute.ethercam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.noiseinstitute.ethercam.http_server.HttpServer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        HttpServer().start()
    }
}
