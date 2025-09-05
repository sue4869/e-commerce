package com.loopers.domain.log

interface AuditLogRepository {

    fun save(auditLogEntity: AuditLogEntity)

    fun existsByMessage(messageId: String): Boolean
}
