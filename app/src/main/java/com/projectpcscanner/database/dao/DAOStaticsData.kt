package com.projectpcscanner.database.dao

import androidx.room.Dao
import androidx.room.Query
import java.util.*

@Dao
interface DAOStaticsData {

    @Query("SELECT name, `current_ value`, date FROM statics_data JOIN statics_value ON statics_data_id=statics_value.statics_data_id")
    fun getAllData(): List<StaticsValueCollection>

    data class StaticsValueCollection(
        val name: String,
        val currentValue: Float,
        val date: Date
    )
}