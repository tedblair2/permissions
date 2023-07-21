package com.example.permissions

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.permissions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val readExternal=READ_EXTERNAL_STORAGE
    private val readVideo=READ_MEDIA_VIDEO
    private val readImages=READ_MEDIA_IMAGES
    private val permissions= arrayOf(
        readVideo,readImages
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            requestPermissions()
        }

    }
    private fun requestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val notGrantedPermissions=permissions.filterNot { permission->
                ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED
            }
            if (notGrantedPermissions.isNotEmpty()){
                val showRationale=notGrantedPermissions.any { permission->
                    shouldShowRequestPermissionRationale(permission)
                }
                if (showRationale){
                    AlertDialog.Builder(this)
                        .setTitle("Storage Permission")
                        .setMessage("Storage permission is needed in order to show images and videos")
                        .setNegativeButton("Cancel"){dialog,_->
                            Toast.makeText(this, "Read media storage permission denied!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .setPositiveButton("OK"){_,_->
                            videoImagesPermission.launch(notGrantedPermissions.toTypedArray())
                        }
                        .show()
                }else{
                    videoImagesPermission.launch(notGrantedPermissions.toTypedArray())
                }
            }else{
                Toast.makeText(this, "Read media storage permission granted", Toast.LENGTH_SHORT).show()
            }
        }else{
            if (ContextCompat.checkSelfPermission(this,readExternal) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Read external storage permission granted", Toast.LENGTH_SHORT).show()
            }else{
                if (shouldShowRequestPermissionRationale(readExternal)){
                    AlertDialog.Builder(this)
                        .setTitle("Storage Permission")
                        .setMessage("Storage permission is needed in order to show images and video")
                        .setNegativeButton("Cancel"){dialog,_->
                            Toast.makeText(this, "Read external storage permission denied!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .setPositiveButton("OK"){_,_->
                            readExternalPermission.launch(readExternal)
                        }
                        .show()
                }else{
                    readExternalPermission.launch(readExternal)
                }
            }
        }
    }
    private val videoImagesPermission=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissionMap->
        if (permissionMap.all { it.value }){
            Toast.makeText(this, "Media permissions granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Media permissions not granted!", Toast.LENGTH_SHORT).show()
        }
    }
    private val readExternalPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted->
        if (isGranted){
            Toast.makeText(this, "Read external storage permission granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Read external storage permission denied!", Toast.LENGTH_SHORT).show()
        }
    }
}