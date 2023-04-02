package com.wsm9175.shelf_life.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wsm9175.shelf_life.db.entity.FoodEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.Date

@Dao
interface FoodEntityDAO {
    //get All Data
    @Query("SELECT * FROM food")
    fun getAllData() : Flow<List<FoodEntity>>

    //INSERT
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(foodEntity: FoodEntity)

    //Update
    @Update
    fun update(foodEntity: FoodEntity)

    //DELETE
    @Delete
    fun delete(foodEntity: FoodEntity)

    //get data by date
    @Query("SELECT * FROM food WHERE shelfLife =:date")
    fun getFoodEntityByShelfLife(date : LocalDate) : List<FoodEntity>

    //get data by id
    @Query("SELECT * FROM food WHERE id=:id")
    fun getFoodEntityByID(id : Long) : FoodEntity

}