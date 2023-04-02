package com.wsm9175.shelf_life.repository

import com.wsm9175.shelf_life.App
import com.wsm9175.shelf_life.db.FoodItemDatabase
import com.wsm9175.shelf_life.db.entity.FoodEntity
import java.time.LocalDate

class DBRepository {
    val context = App.context()
    val db = FoodItemDatabase.getDatabase(context)

    fun getAllFoodData() = db.foodEntityDAO().getAllData()
    fun insertFoodData(foodEntity: FoodEntity) = db.foodEntityDAO().insert(foodEntity)
    fun updateFoodData(foodEntity: FoodEntity) = db.foodEntityDAO().update(foodEntity)
    fun deleteFoodData(foodEntity: FoodEntity) = db.foodEntityDAO().delete(foodEntity)
    fun getFoodDataByShelfLife(date : LocalDate) = db.foodEntityDAO().getFoodEntityByShelfLife(date)
    fun getFoodDataById(id : Long) = db.foodEntityDAO().getFoodEntityByID(id)
}