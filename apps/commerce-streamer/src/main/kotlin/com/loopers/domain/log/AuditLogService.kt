package com.loopers.domain.log

import org.springframework.stereotype.Service

@Service
class AuditLogService(
    private val auditLogRepository: AuditLogRepository
) {

    fun save(auditLogEntity: AuditLogEntity) {
        auditLogRepository.save(auditLogEntity)
    }

    fun isMessageProcessed(messageId: String): Boolean {
        return auditLogRepository.existsByMessage(messageId)
    }
}
