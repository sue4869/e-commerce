package com.loopers.domain.point

interface PointRepository {
    fun findByUserId(userId: String): PointEntity?
    fun save(point: PointEntity)
}
