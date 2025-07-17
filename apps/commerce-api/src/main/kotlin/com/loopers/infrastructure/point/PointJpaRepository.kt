package com.loopers.infrastructure.point

import com.loopers.domain.point.PointEntity
import com.loopers.domain.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PointJpaRepository : JpaRepository<PointEntity, Long> {

    fun findByUserId(userId: String): PointEntity?
}
