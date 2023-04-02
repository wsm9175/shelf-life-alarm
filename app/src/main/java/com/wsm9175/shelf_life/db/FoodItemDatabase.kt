package com.wsm9175.shelf_life.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wsm9175.shelf_life.db.dao.FoodEntityDAO
import com.wsm9175.shelf_life.db.entity.Converters
import com.wsm9175.shelf_life.db.entity.FoodEntity

@Database(entities = [FoodEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class FoodItemDatabase : RoomDatabase() {
    abstract fun foodEntityDAO() : FoodEntityDAO

    companion object{
        @Volatile
        private var INSTANCE : FoodItemDatabase? =null

        fun getDatabase(context:Context) : FoodItemDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodItemDatabase::class.java,
                    "food_item_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}