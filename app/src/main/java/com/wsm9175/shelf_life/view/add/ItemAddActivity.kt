package com.wsm9175.shelf_life.view.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import com.wsm9175.shelf_life.databinding.ActivityItemAddBinding
import java.time.LocalDate

class ItemAddActivity : AppCompatActivity() {
    private val TAG = ItemAddActivity::class.java.simpleName
    private lateinit var binding : ActivityItemAddBinding
    private val viewModel : ItemAddViewModel by viewModels()

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var getTakePictureResult : ActivityResultLauncher<Intent>
    private lateinit var getFromGalleryResult : ActivityResultLauncher<Intent>
    private lateinit var takePictureIntent : Intent
    private lateinit var getPictureIntent : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemAddBinding.inflate(layoutInflater)
        binding.activity = this
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
                binding.image.setImageBitmap(imageBitmap)
            }
        }

        getFromGalleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val intent = it.data
                val uri = intent!!.data
                binding.image.setImageURI(uri)

            }
        }

        binding.edtStartDate.setOnFocusChangeListener(object : OnFocusChangeListener{
            override fun onFocusChange(p0: View?, hasFocus: Boolean) {
                if(!hasFocus){
                    var text = binding.edtStartDate.text.toString()
                    if(text.length> 7)
                        binding.edtStartDate.setText("${text.substring(0, 4)}-${text.substring(4, 6)}-${text.substring(6, 8)}")
                    else
                        binding.edtStartDate.setText("")
                }
            }
        })

        binding.edtEndDate.setOnFocusChangeListener(object : OnFocusChangeListener{
            override fun onFocusChange(p0: View?, hasFocus: Boolean) {
                if(!hasFocus){
                    var text = binding.edtEndDate.text.toString()
                    if(text.length> 7)
                        binding.edtEndDate.setText("${text.substring(0, 4)}-${text.substring(4, 6)}-${text.substring(6, 8)}")
                    else
                        binding.edtEndDate.setText("")
                }
            }
        })

        viewModel.save.observe(this, Observer {
            if(it.equals("done")) {
                Toast.makeText(applicationContext, "저장이 완료되었습니다", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    fun showSelectDialog(){
        Log.d(TAG, "showSelectDialog")
        val dialog = SelectDialog(this)
        dialog.setOnTakePictureClickListener {
            Log.d(TAG, "click take picture")
            if(checkPermission()) getTakePictureResult.launch(takePictureIntent)
            dialog.dismiss()
        }
        dialog.setOnGetFromGalleryClickListener {
            if(checkPermission()) getFromGalleryResult.launch(getPictureIntent)
            Log.d(TAG, "click get from gallery")
            dialog.dismiss()
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

    fun saveFoodItem(){
        try {
            val image = binding.image.drawable.toBitmap()
            val name = binding.edtName.text.toString()
            val count = binding.edtCount.text.toString().toLong()
            val startDate = LocalDate.parse(binding.edtStartDate.text.toString())
            val endDate = LocalDate.parse(binding.edtEndDate.text.toString())
            val note = binding.edtNote.text.toString()

            if(startDate.compareTo(endDate) > 0){
                Toast.makeText(this, "구매일이 유통기한보다 클 수 없습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.insertItem(image, name, count, startDate, endDate, note)
        }catch (e :java.lang.Exception){
            e.printStackTrace()
            Toast.makeText(this, "알맞은 형식을 입력해주세요", Toast.LENGTH_SHORT).show()
        }

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