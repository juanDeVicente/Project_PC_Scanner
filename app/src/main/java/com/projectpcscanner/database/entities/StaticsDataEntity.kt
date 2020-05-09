package com.projectpcscanner.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statics_data")
data class StaticsDataEntity (
    @PrimaryKey val id: Int,
    @ColumnInfo(name="name") val name: String?,
    @ColumnInfo(name="max_value") val mavValue: Float?
)