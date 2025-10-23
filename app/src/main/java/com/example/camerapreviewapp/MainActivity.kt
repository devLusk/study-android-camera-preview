package com.example.camerapreviewapp

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.camerapreviewapp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val photoFile = File.createTempFile("photo_", ".jpg", cacheDir)
        val photoURI = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            photoFile
        )

        val takePicture = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success: Boolean ->
            if (success) {
                Log.i("CameraPreviewApp", "Image captured successfully.")
                binding.imagePreview.setImageURI(photoURI)
            } else {
                Log.w("CameraPreviewApp", "No image captured or operation canceled.")
            }
        }

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("CameraPreviewApp", "Camera permission granted.")
                takePicture.launch(photoURI)
            } else {
                Log.w("CameraPreviewApp", "Camera permission denied.")
            }
        }

        binding.btnOpenCamera.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.btnResetPreview.setOnClickListener {
            binding.imagePreview.setImageResource(R.drawable.ic_launcher_background)
        }
    }
}