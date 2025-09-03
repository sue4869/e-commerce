package com.loopers.support.aop

data class TransactionTraceInfo(
    val methodName: String,
    val arguments: Array<Any>,
    val rollbackOnly: Boolean,
    val exceptionOccurred: Boolean,
)

object TransactionTraceHolder {
    private val holder = ThreadLocal<TransactionTraceInfo>()

    fun set(info: TransactionTraceInfo) = holder.set(info)

    fun get(): TransactionTraceInfo = holder.get()

    fun clear() = holder.remove()
}
