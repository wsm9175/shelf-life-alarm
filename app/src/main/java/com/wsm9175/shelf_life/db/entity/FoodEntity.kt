package com.wsm9175.shelf_life.db.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneId.systemDefault

@Entity(tableName = "food")
data class FoodEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var name: String,
    var image: Bitmap?,
    var count: Long,
    var buyDate: LocalDate,
    var shelfLife: LocalDate,
    var note: String,
)

class Converters{
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        val localDateTime : LocalDateTime = date!!.atStartOfDay()
        return localDateTime.atZone(systemDefault()).toInstant().toEpochMilli()
    }
    @TypeConverter
    fun toByteArray(bitmap: Bitmap) : ByteArray{
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(bytes : ByteArray) : Bitmap{
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
