package com.projectpcscanner.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.projectpcscanner.database.entities.StaticsValueEntity

@Dao
interface DAOStaticsValue {

    @Query("SELECT * FROM statics_value")
    fun selectAll(): List<StaticsValueEntity>

    @Insert
    fun insertAll(vararg staticsValue: StaticsValueEntity)

    @Delete
    fun delete(staticsValueEntity: StaticsValueEntity)

    @Query("DELETE FROM statics_value")
    fun deleteAll()
}