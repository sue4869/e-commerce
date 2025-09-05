package com.loopers.domain.event

import mu.KotlinLogging
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.stereotype.Component
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<Any, Any>,
) {
    private val log = KotlinLogging.logger {}

    /**
     * 카프카 producer sync 방식
     */
    fun send(message: Message<*>) {
        val future = kafkaTemplate.send(message)
        try {
            future.get()
        } catch (e: Exception) {
            log.error {
                """
                [send] kafka producer failed. 
                topic[${message.headers[KafkaHeaders.TOPIC]}] 
                payload[${message.payload}]
                error[${ExceptionUtils.getMessage(e)}]
            """.trimIndent()
            }
            throw e
        }
    }

    /**
     * 카프카 producer async 방식
     */
    fun sendAsync(message: Message<*>) {
        val future = kafkaTemplate.send(message)

        future.whenComplete { metadata, exception ->
            if (exception == null)
                log.info { "Message sent successfully to topic ${metadata.recordMetadata.topic()} at partition ${metadata.recordMetadata.partition()}, offset ${metadata.recordMetadata.offset()}" }
            else
                log.error {
                    """
                    [sendAsync] kafka producer failed. 
                    topic[${message.headers[KafkaHeaders.TOPIC]}] 
                    payload[${message.payload}]
                    error[${ExceptionUtils.getMessage(exception)}]
                """.trimIndent()
                }
        }
    }

    fun sendAsync(topic: String, key: Any, data: Any) {
        val future = kafkaTemplate.send(topic, key, data)
        future.whenComplete { metadata, exception ->
            if (exception == null)
                log.info { "Message sent successfully to topic ${metadata.recordMetadata.topic()} at partition ${metadata.recordMetadata.partition()}, offset ${metadata.recordMetadata.offset()}" }
            else
                log.error {
                    """
                    [sendAsync] kafka producer failed. 
                    topic[${topic}] 
                    key[${key}]
                    data[${data}]
                    error[${ExceptionUtils.getMessage(exception)}]
                """.trimIndent()
                }
        }
    }
}
