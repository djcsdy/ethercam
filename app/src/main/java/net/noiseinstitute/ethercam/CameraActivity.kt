package net.noiseinstitute.ethercam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.noiseinstitute.ethercam.http_server.HttpServer

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    override fun onStart() {
        super.onStart()

        HttpServer().start()
    }

}
