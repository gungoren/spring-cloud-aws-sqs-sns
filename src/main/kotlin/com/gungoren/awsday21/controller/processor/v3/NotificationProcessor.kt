package com.gungoren.awsday21.controller.processor.v3

import com.gungoren.awsday21.service.WebhookNotificationService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.stereotype.Component

@Component
class NotificationProcessor (
        @Autowired private val webhookNotificationService: WebhookNotificationService
) {

    private val logger = LoggerFactory.getLogger(NotificationProcessor::class.java)

    @SqsListener(value = ["notification"], deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    fun processNotification(notificationMessage: NotificationMessage) {
        logger.info("Sending alert {} to {}", notificationMessage.payload.message, notificationMessage.destination)
        webhookNotificationService.send(notificationMessage.payload, notificationMessage.destination)
    }
}
