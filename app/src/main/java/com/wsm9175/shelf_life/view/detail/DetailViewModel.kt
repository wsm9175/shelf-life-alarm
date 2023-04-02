package com.wsm9175.shelf_life.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wsm9175.shelf_life.db.entity.FoodEntity
import com.wsm9175.shelf_life.repository.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel : ViewModel() {
    private val db = DBRepository()
    private val _saved = MutableLiveData<String>()
    private val DELETE = "delete"
    private val _entity = MutableLiveData<FoodEntity>()
    val entity: LiveData<FoodEntity>
        get() = _entity

    private val SAVE = "save"
    val save: LiveData<String>
        get() = _saved


    fun updateEntity(foodEntity: FoodEntity) = viewModelScope.launch(Dispatchers.IO) {
        db.updateFoodData(foodEntity)
        withContext(Dispatchers.Main) {
            _saved.value = SAVE
        }
    }

    fun deleteEntity(foodEntity: FoodEntity) = viewModelScope.launch(Dispatchers.IO) {
        db.deleteFoodData(foodEntity)
        withContext(Dispatchers.Main) {
            _saved.value = DELETE
        }
    }

    fun getEntity(position: Long) = viewModelScope.launch(Dispatchers.IO) {
        val foodEntity = db.getFoodDataById(position)
        withContext(Dispatchers.Main) {
            _entity.value = foodEntity
        }
    }
}