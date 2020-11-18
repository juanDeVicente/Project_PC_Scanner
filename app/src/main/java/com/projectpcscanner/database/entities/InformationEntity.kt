package com.projectpcscanner.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "information")
data class InformationEntity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name="name") val name: String,
    @ColumnInfo(name="ip") val ip: String,
    @ColumnInfo(name="port") val port: String,
    @ColumnInfo(name = "mac") val mac: String
)