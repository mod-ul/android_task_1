package com.example.androidapptest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.androidapptest.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)

        lateinit var cameraLauncher: ActivityResultLauncher<Intent>
        var photoUri: Uri? = null
        var photoCaptured = false

        fun createImageFile(): File? {
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${System.currentTimeMillis()}_",
                ".jpg",
                storageDir
            )
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                view.imageView.setImageURI(photoUri)
                photoCaptured = true
            } else photoCaptured = false
        }

        view.btnCamera.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoFile = createImageFile()
            photoFile?.also {
                photoUri = FileProvider.getUriForFile(this, "your.package.name.fileprovider", it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                cameraLauncher.launch(takePictureIntent)
            }
        }

        view.btnShare.setOnClickListener {
            if (photoCaptured && photoUri != null) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, photoUri)
                }
                startActivity(Intent.createChooser(shareIntent, "Share Image"))
            } else {
                Toast.makeText(this, "Take a photo first, duh!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}