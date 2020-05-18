package com.projectpcscanner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.projectpcscanner.database.dao.DAOStaticsData
import com.projectpcscanner.database.dao.DAOStaticsValue

import com.projectpcscanner.database.entities.StaticsDataEntity
import com.projectpcscanner.database.entities.StaticsValueEntity
import java.util.*

@Database(entities = [StaticsDataEntity::class, StaticsValueEntity::class], version = 5)
@TypeConverters(PCScannerDatabase.Converters::class)
abstract class PCScannerDatabase: RoomDatabase() {

    companion object {
        private var instance: PCScannerDatabase? = null

        fun getInstance(context: Context): PCScannerDatabase? {
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
    }

    fun clearDatabase(context: Context) {
        if (instance != null)
        {
            staticsDataDao().deleteAll()
            staticsValueDao().deleteAll()
        }
    }


    abstract fun staticsDataDao(): DAOStaticsData
    abstract fun staticsValueDao(): DAOStaticsValue

    class Converters {
        @TypeConverter
        fun fromTimestamp(value: Long?): Date? {
            return value?.let { Date(it) }
        }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? {
            return date?.time
        }
    }
}