package com.loopers.infrastructure.log

import com.loopers.domain.log.AuditLogEntity
import com.loopers.domain.log.AuditLogRepository
import org.springframework.stereotype.Component

@Component
class AuditLogJpaRepositoryImpl(
    private val auditLogJpaRepository: AuditLogJpaRepository
): AuditLogRepository {

    override fun save(auditLogEntity: AuditLogEntity) {
        auditLogJpaRepository.save(auditLogEntity)
    }

    override fun existsByMessage(messageId: String): Boolean {
        return auditLogJpaRepository.existsByMessageId(messageId)
    }
}
