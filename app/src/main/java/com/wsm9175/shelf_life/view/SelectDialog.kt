package com.wsm9175.shelf_life.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.wsm9175.shelf_life.databinding.DialogSelectCamModeBinding

class SelectDialog(context: Context): Dialog(context) {
    private lateinit var binding : DialogSelectCamModeBinding
    private lateinit var takePictureClickListener : TakePictureCLickListener
    private lateinit var getFromGalleryClickListener : GetFromGalleryClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSelectCamModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding){
        //window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        takePicture.setOnClickListener{
            Log.d("dialog", "click take picture")
            takePictureClickListener.onClick()
        }

        getFromGallery.setOnClickListener {
            Log.d("dialog", "click get from gallery")
            getFromGalleryClickListener.onClick()
        }
    }

    fun setOnTakePictureClickListener(listener: () -> Unit){
        this.takePictureClickListener = object : TakePictureCLickListener{
            override fun onClick() {
                listener()
            }
        }
    }

    fun setOnGetFromGalleryClickListener(listener: () -> Unit){
        this.getFromGalleryClickListener = object : GetFromGalleryClickListener{
            override fun onClick() {
                listener()
            }
        }
    }

    interface TakePictureCLickListener{
        fun onClick()
    }

    interface GetFromGalleryClickListener{
        fun onClick()
    }
}