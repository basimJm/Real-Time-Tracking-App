package com.digitalcash.soarapoc.data.remote.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MapCoordinationDto")
data class MapCoordinationDto(
    val lat: Double,
    val lng: Double,
    @PrimaryKey(autoGenerate = true) val id: Int? = null
)