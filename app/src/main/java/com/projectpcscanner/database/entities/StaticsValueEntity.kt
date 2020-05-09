package com.projectpcscanner.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "statics_value")
data class StaticsValueEntity (
    @PrimaryKey val id: Int,
    @ForeignKey(entity = StaticsDataEntity::class, parentColumns = ["id"], childColumns = ["statics_data_id"], onDelete = CASCADE, onUpdate = CASCADE)
    @ColumnInfo(name = "statics_data_id") val staticsDataId: Int,
    @ColumnInfo(name = "current_ value") val currentValue: Float,
    @ColumnInfo(name = "date", defaultValue = "CURRENT_TIMESTAMP") val date: Date
)