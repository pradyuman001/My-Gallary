package com.example.mygallaryslide.screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import android.widget.ViewSwitcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mygallaryslide.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var position: Int = 0
    private var i: Int = 0
    private lateinit var binding: ActivityMainBinding
    private val imageList = arrayListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

        binding.imageSwitcher.setFactory(ViewSwitcher.ViewFactory { ImageView(applicationContext) })

        next()

        previous()
    }

    private fun previous() {
        binding.previous.setOnClickListener {
            if (position > 0) {
                // decrease the position by 1
                position--
                binding.imageSwitcher.setImageURI(imageList[position])

            }
        }
    }

    fun next() {
        binding.next.setOnClickListener {
            if (position < imageList.size - 1) {

                position++
                binding.imageSwitcher.setImageURI(imageList[position])

            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Last Image Already Shown",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val permission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

            if (permission == 0) {

                selectImage()
            } else {

                requestPermission()
            }
        }
    }

    private fun selectImage() {
        // For latest versions API LEVEL 19+
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 2)
    }

    private fun requestPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )

            } else {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            1 -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                if ((ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED)
                ) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    selectImage()

                }
            } else {

                requestPermission()

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 2) {

            // if multiple images are selected
            if (data?.clipData != null) {
                val count = data.clipData?.itemCount

                while (i < count!!) {

                    imageList.add(data.clipData?.getItemAt(i)!!.uri)
                    i++
                }

                binding.imageSwitcher.setImageURI(imageList[0])
                position = 0

            } else if (data?.data != null) {

                imageList.add(data.data!!)
                binding.imageSwitcher.setImageURI(imageList[0])
                position = 0

            }

        }
    }
}