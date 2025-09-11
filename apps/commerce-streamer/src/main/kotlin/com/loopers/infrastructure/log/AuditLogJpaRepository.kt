package com.loopers.infrastructure.log

import com.loopers.domain.log.AuditLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AuditLogJpaRepository : JpaRepository<AuditLogEntity, Long> {

    fun existsByMessageId(messageId: String): Boolean
}
