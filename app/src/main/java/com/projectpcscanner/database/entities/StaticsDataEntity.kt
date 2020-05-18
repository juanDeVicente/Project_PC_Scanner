package com.projectpcscanner.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "statics_data")
data class StaticsDataEntity (
    @PrimaryKey
    @ColumnInfo(name="name") val name: String,
    @ColumnInfo(name="max_value") val maxValue: Float?,
    @ColumnInfo(name="min_value")val minValue: Float?,
    @ColumnInfo(name="scaling_factor")val scalingFactor: Float?
)