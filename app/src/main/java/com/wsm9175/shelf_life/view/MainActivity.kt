package com.wsm9175.shelf_life.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wsm9175.shelf_life.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding : ActivityMainBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var getTakePictureResult : ActivityResultLauncher<Intent>
    private lateinit var getFromGalleryResult : ActivityResultLauncher<Intent>
    private val viewModel : MainViewModel by viewModels()
    private lateinit var takePictureIntent : Intent
    private lateinit var getPictureIntent : Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.activity = this@MainActivity
        setContentView(binding.root)

        takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getPictureIntent = Intent(Intent.ACTION_PICK)
        getPictureIntent.type = "image/*"
        getPictureIntent.action = Intent.ACTION_GET_CONTENT

        //val chooseIntent = Intent.createChooser(intent, "이미지를 가져올 앱 선택")
        //chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))

        getTakePictureResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val imageBitmap = it.data?.extras?.get("data") as Bitmap
                Log.d(TAG, "get take picture success")
            }
        }

        getFromGalleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val imageBitmap = it.data?.extras?.get("data") as Bitmap
                Log.d(TAG, "get picture from gallery success")
            }
        }
    }

    fun showSelectDialog(){
        Log.d(TAG, "showSelectDialog")
        val dialog = SelectDialog(this)
        dialog.setOnTakePictureClickListener {
            Log.d(TAG, "click take picture")
            if(checkPermission()) getTakePictureResult.launch(takePictureIntent)
        }
        dialog.setOnGetFromGalleryClickListener {
            if(checkPermission()) getFromGalleryResult.launch(getPictureIntent)
            Log.d(TAG, "click get from gallery")
        }
        dialog.show()
    }

    private fun checkPermission() : Boolean{
        var permission = mutableMapOf<String, String>()
        permission["camera"] = Manifest.permission.CAMERA
        permission["storageRead"] = Manifest.permission.READ_MEDIA_IMAGES
        //permission["storageWrite"] =  Manifest.permission.WRITE_EXTERNAL_STORAGE
        var denied = permission.count {
            ContextCompat.checkSelfPermission(this,it.value) == PackageManager.PERMISSION_DENIED
        }
        if(denied > 0){
            requestPermissions(permission.values.toTypedArray(), REQUEST_IMAGE_CAPTURE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            var count = grantResults.count{it == PackageManager.PERMISSION_DENIED}

            if(count != 0){
                Log.d(TAG, "denied coint : " + count)
                Toast.makeText(applicationContext, "권한을 동의해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}