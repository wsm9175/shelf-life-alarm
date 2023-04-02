package com.wsm9175.shelf_life.view.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import com.wsm9175.shelf_life.databinding.ActivityDetailBinding
import com.wsm9175.shelf_life.db.entity.FoodEntity
import com.wsm9175.shelf_life.view.add.SelectDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var foodEntity: FoodEntity

    private val SAVE = "save"
    private val DELETE = "delete"
    private val ENTITY = "entity"

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var getTakePictureResult: ActivityResultLauncher<Intent>
    private lateinit var getFromGalleryResult: ActivityResultLauncher<Intent>
    private lateinit var takePictureIntent: Intent
    private lateinit var getPictureIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        binding.activity = this
        setContentView(binding.root)

        takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getPictureIntent = Intent(Intent.ACTION_PICK)
        getPictureIntent.type = "image/*"
        getPictureIntent.action = Intent.ACTION_GET_CONTENT


        viewModel.save.observe(this, Observer {
            if (it.equals(SAVE)) {
                Toast.makeText(applicationContext, "수정 완료", Toast.LENGTH_SHORT).show()
                finish()
            } else if (it.equals(DELETE)) {
                Toast.makeText(applicationContext, "삭제 완료", Toast.LENGTH_SHORT).show()
                finish()
            }
        })

        getTakePictureResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val imageBitmap = it.data?.extras?.get("data") as Bitmap
                    binding.image.setImageBitmap(imageBitmap)
                }
            }

        getFromGalleryResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val intent = it.data
                    val uri = intent!!.data
                    binding.image.setImageURI(uri)
                }
            }
        val position = intent.getLongExtra(ENTITY, -99)
        viewModel.getEntity(position)
        viewModel.entity.observe(this, Observer {
            foodEntity = it
            settingView()
        })
    }

    private fun settingView() {
        binding.image.setImageBitmap(foodEntity.image)
        binding.edtName.setText(foodEntity.name)
        binding.edtCount.setText(foodEntity.count.toString())
        binding.edtStartDate.setText(foodEntity.buyDate.format(formatter))
        binding.edtEndDate.setText(foodEntity.shelfLife.format(formatter))
        binding.edtNote.setText(foodEntity.note)
    }

    fun updateFoodEntity() {
        try{
            if(LocalDate.parse(binding.edtStartDate.text.toString()).compareTo( LocalDate.parse(binding.edtEndDate.text.toString())) > 0){
                Toast.makeText(this, "구매일이 유통기한보다 클 수 없습니다.", Toast.LENGTH_SHORT).show()
            }else{
                foodEntity.image = binding.image.drawable.toBitmap()
                foodEntity.name = binding.edtName.text.toString()
                foodEntity.count = binding.edtCount.text.toString().toLong()
                foodEntity.buyDate = LocalDate.parse(binding.edtStartDate.text.toString())
                foodEntity.shelfLife = LocalDate.parse(binding.edtEndDate.text.toString())
                foodEntity.note = binding.edtNote.text.toString()
                viewModel.updateEntity(foodEntity)
            }
        }catch (e : java.lang.Exception){
            Toast.makeText(this, "알맞은 형식을 입력해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteFoodEntity() {
        viewModel.deleteEntity(foodEntity)
    }


    fun showSelectDialog() {
        val dialog = SelectDialog(this)
        dialog.setOnTakePictureClickListener {
            if (checkPermission()) getTakePictureResult.launch(takePictureIntent)
            dialog.dismiss()
        }
        dialog.setOnGetFromGalleryClickListener {
            if (checkPermission()) getFromGalleryResult.launch(getPictureIntent)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun checkPermission(): Boolean {
        var permission = mutableMapOf<String, String>()
        permission["camera"] = Manifest.permission.CAMERA
        permission["storageRead"] = Manifest.permission.READ_MEDIA_IMAGES
        //permission["storageWrite"] =  Manifest.permission.WRITE_EXTERNAL_STORAGE
        var denied = permission.count {
            ContextCompat.checkSelfPermission(this, it.value) == PackageManager.PERMISSION_DENIED
        }
        if (denied > 0) {
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
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            var count = grantResults.count { it == PackageManager.PERMISSION_DENIED }

            if (count != 0) {
                Toast.makeText(applicationContext, "권한을 동의해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}