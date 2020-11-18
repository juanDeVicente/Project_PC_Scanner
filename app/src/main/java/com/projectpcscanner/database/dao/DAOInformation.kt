package com.projectpcscanner.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.projectpcscanner.database.entities.InformationEntity

@Dao
interface DAOInformation {

    @Delete
    fun delete(informationEntity: InformationEntity)

    @Query("SELECT * FROM information")
    fun selectAll(): InformationEntity

    @Query("SELECT * FROM information WHERE id=:id")
    fun select(id: Int): InformationEntity

    @Query("DELETE FROM information")
    fun deleteAll()
}