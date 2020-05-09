package com.projectpcscanner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

import com.projectpcscanner.database.entities.StaticsDataEntity
import com.projectpcscanner.database.entities.StaticsValueEntity
import java.util.*

@Database(entities = [StaticsDataEntity::class, StaticsValueEntity::class], version = 1)
@TypeConverters(PCScannerDatabase.Converters::class)
abstract class PCScannerDatabase: RoomDatabase() {
    private var instance: PCScannerDatabase? = null

    open fun getInstance(context: Context): PCScannerDatabase? {
        if (instance == null) {
            synchronized(PCScannerDatabase::class.java) {
                if (instance == null) {
                    instance = databaseBuilder(
                        context.applicationContext,
                        PCScannerDatabase::class.java, "pcscanner-database"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                }
            }
        }
        return instance
    }

    class Converters {
        @TypeConverter
        fun fromTimestamp(value: Long?): Date? {
            return value?.let { Date(it) }
        }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? {
            return date?.time?.toLong()
        }
    }
}