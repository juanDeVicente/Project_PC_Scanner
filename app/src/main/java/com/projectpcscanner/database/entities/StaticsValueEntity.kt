package com.projectpcscanner.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "statics_value")
data class StaticsValueEntity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ForeignKey(entity = StaticsDataEntity::class, parentColumns = ["name"], childColumns = ["statics_name"], onDelete = CASCADE, onUpdate = CASCADE)
    @ColumnInfo(name = "statics_name") val staticsName: String,
    @ColumnInfo(name = "current_ value") val currentValue: Float,
    @ColumnInfo(name = "date") val date: Date?
)