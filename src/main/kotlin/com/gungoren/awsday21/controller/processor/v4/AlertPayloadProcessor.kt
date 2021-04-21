package com.gungoren.awsday21.controller.processor.v4

import com.gungoren.awsday21.data.RuleRepository
import com.gungoren.awsday21.engine.RuleEngine
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Component

@Component
class AlertPayloadProcessor (
        @Autowired private val ruleRepository: RuleRepository,
        @Autowired private val queueMessagingTemplate: QueueMessagingTemplate
) {

    private val engine = RuleEngine()

    private val logger = LoggerFactory.getLogger(AlertPayloadProcessor::class.java)

    @SqsListener(value = ["alert-payload"], deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    fun processAlert(notificationMessage: AlertPayloadMessage) {

        logger.info("Alert payload processor received message {} , {}", notificationMessage.customerId, notificationMessage.payload.message)
        val destinations = HashSet<String>()

        for (rule in ruleRepository.listRule(notificationMessage.customerId)) {
            val result = engine.executeRule(notificationMessage.payload, rule.expression)
            if (result) {
                destinations.add(rule.destination)
            }
        }

        for (destination in destinations) {
            queueMessagingTemplate.convertAndSend("notification", NotificationMessage(notificationMessage.payload, destination))
        }

    }
}
