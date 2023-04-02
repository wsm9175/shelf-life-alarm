package com.wsm9175.shelf_life.view.add

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wsm9175.shelf_life.db.entity.FoodEntity
import com.wsm9175.shelf_life.repository.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class ItemAddViewModel : ViewModel(){
    private val repository = DBRepository()
    private val _saved = MutableLiveData<String>()
    val save : LiveData<String>
        get() = _saved

    fun insertItem(image : Bitmap, name : String, count : Long, startDate : LocalDate, endDate : LocalDate, note : String) = viewModelScope.launch(Dispatchers.IO){
        val foodEntity = FoodEntity(0, name, image, count, startDate, endDate, note)
        foodEntity.let {
            repository.insertFoodData(it)
        }

        withContext(Dispatchers.Main){
            _saved.value = "done"
        }
    }
}