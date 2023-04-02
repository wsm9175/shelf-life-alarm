package com.wsm9175.shelf_life.view.main

import androidx.lifecycle.*
import com.wsm9175.shelf_life.db.entity.FoodEntity
import com.wsm9175.shelf_life.repository.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel : ViewModel(){
    private val repository = DBRepository()
    val foodItemList : LiveData<List<FoodEntity>> = repository.getAllFoodData().asLiveData()

    fun deleteEntity(foodEntity: FoodEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteFoodData(foodEntity)
    }
}