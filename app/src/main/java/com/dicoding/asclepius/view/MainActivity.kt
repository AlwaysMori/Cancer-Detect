package com.dicoding.asclepius.view

import android.content.Intent
import android.util.Log
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.R
import java.io.File
import com.yalantis.ucrop.UCrop
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null
    private var croppedImageUri: Uri? = null
    private lateinit var bottomNavigationView: BottomNavigationView
    private var isImageSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationView = findViewById(R.id.menuBar)
        bottomNavigationView.setOnNavigationItemSelectedListener(onBottomNavItemSelectedListener)
        binding.galleryButton.setOnClickListener { handleGalleryButtonClick() }
        binding.analyzeButton.setOnClickListener { handleAnalyzeButtonClick() }
    }

    private val onBottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.news -> {
                startActivity(Intent(this, NewsActivity::class.java))
                true
            }
            R.id.history_menu -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            else -> false
        }
    }

    private fun handleGalleryButtonClick() {
        startGallery()
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            selectedImg?.let { uri ->
                currentImageUri = uri
                showImage()
                startUCrop(uri)
                isImageSelected = true
            } ?: showToast("Failed to get image URI")
        }
    }

    private fun startUCrop(sourceUri: Uri) {
        // TODO: Melakukan crop gambar.

        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, fileName))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO: Menangani hasil crop gambar.

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                showCroppedImage(resultUri)
            } ?: showToast("Gagal Crop Gambar")
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        }
    }

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.

        currentImageUri?.let { uri ->
            Log.d(TAG, "Gambar Ditampilkan: $uri")
            binding.previewImageView.setImageURI(uri)
        } ?: Log.d(TAG, "Tidak ada gambar ditampilkan")
    }

    private fun handleAnalyzeButtonClick() {
        // TODO: Memulai proses analisis gambar.

        if (isImageSelected) {
            analyzeImage()
            moveToResult()
        } else {
            showToast(getString(R.string.image_classifier_failed))
        }
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.

        val intent = Intent(this, ResultActivity::class.java)
        croppedImageUri?.let { uri ->
            intent.putExtra(ResultActivity.IMAGE_URI, uri.toString())
            startActivityForResult(intent, REQUEST_RESULT)
        } ?: showToast(getString(R.string.image_classifier_failed))
    }

    private fun moveToResult() {
        // TODO: Memindahkan ke ResultActivity.

        Log.d(TAG, "Moving to ResultActivity")
        val intent = Intent(this, ResultActivity::class.java)
        croppedImageUri?.let { uri ->
            intent.putExtra(ResultActivity.IMAGE_URI, uri.toString())
            startActivity(intent)
        } ?: showToast(getString(R.string.image_classifier_failed))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showCroppedImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
        croppedImageUri = uri
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    companion object {
        const val TAG = "ImagePicker"
        private const val REQUEST_RESULT = 1001
    }
}
