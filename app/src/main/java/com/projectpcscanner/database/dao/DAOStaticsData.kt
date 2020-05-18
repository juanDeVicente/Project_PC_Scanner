package com.projectpcscanner.database.dao

import androidx.room.*
import com.projectpcscanner.database.entities.StaticsDataEntity
import com.projectpcscanner.database.entities.StaticsValueEntity

@Dao
interface DAOStaticsData {

    @Delete
    fun delete(staticsValueEntity: StaticsValueEntity)

    @Query("DELETE FROM statics_data")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg staticsDataEntity: StaticsDataEntity)

    @Query("SELECT * from statics_data WHERE name=:name")
    fun getData(name: String): StaticsDataEntity
}