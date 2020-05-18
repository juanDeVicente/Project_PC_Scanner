package com.projectpcscanner.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.projectpcscanner.database.entities.StaticsValueEntity
import java.util.*

@Dao
interface DAOStaticsValue {

    @Query("SELECT * FROM statics_value")
    fun selectAll(): List<StaticsValueEntity>

    @Query("SELECT `current_ value` FROM statics_value WHERE date > :date and statics_name = :name ORDER BY date DESC LIMIT 20") //TODO habria que sacar este 20 a una variable global
    fun select(date: Date, name: String): MutableList<Float>

    @Insert
    fun insertAll(staticsValue: MutableList<StaticsValueEntity>)

    @Delete
    fun delete(staticsValueEntity: StaticsValueEntity)

    @Query("DELETE FROM statics_value")
    fun deleteAll()

    @Query("INSERT INTO statics_value (statics_name, `current_ value`) VALUES(:name, :currentValue)")
    fun insert(name: String, currentValue: Float)
}