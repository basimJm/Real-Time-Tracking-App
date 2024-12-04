package com.digitalcash.soarapoc.data.mapper

import com.digitalcash.soarapoc.data.remote.model.MapCoordinationDto
import com.digitalcash.soarapoc.domain.entity.MapCoordination


fun MapCoordination.toMapCoordinationDto(): MapCoordinationDto {
    return MapCoordinationDto(id = id, lat = lat, lng = lng)
}

fun MapCoordinationDto.toMapCoordination(): MapCoordination {
    return MapCoordination(id = id, lat = lat, lng = lng)
}