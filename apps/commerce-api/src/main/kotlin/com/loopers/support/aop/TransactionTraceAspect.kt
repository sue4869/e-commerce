package com.loopers.support.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.transaction.interceptor.TransactionAspectSupport

@Aspect
@Component
class TransactionTraceAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    fun traceTransaction(joinPoint: ProceedingJoinPoint): Any? {
        var rollbackOnly = false
        var exceptionOccurred = false
        val methodName = joinPoint.signature.toShortString()
        val args = joinPoint.args

        try {
            val result = joinPoint.proceed()
            rollbackOnly = currentTransactionRollbackStatus()
            return result
        } catch (ex: Throwable) {
            exceptionOccurred = true
            rollbackOnly = currentTransactionRollbackStatus()
            throw ex
        } finally {
            val traceInfo = TransactionTraceInfo(
                methodName = methodName,
                arguments = args,
                rollbackOnly = rollbackOnly,
                exceptionOccurred = exceptionOccurred,
            )
            TransactionTraceHolder.set(traceInfo)
        }
    }

    private fun currentTransactionRollbackStatus(): Boolean {
        return try {
            TransactionAspectSupport.currentTransactionStatus().isRollbackOnly
        } catch (e: Exception) {
            false // 트랜잭션이 없거나 접근 불가한 경우
        }
    }
}
