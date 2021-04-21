package com.gungoren.awsday21.controller.processor.v4

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gungoren.awsday21.controller.processor.v5.SNSMessage
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
class AlertPayloadProcessorV2 (
        @Autowired private val ruleRepository: RuleRepository,
        @Autowired private val queueMessagingTemplate: QueueMessagingTemplate
) {

    private val engine = RuleEngine()

    private val logger = LoggerFactory.getLogger(AlertPayloadProcessor::class.java)

    private val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    @SqsListener(value = ["alert-process"], deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    fun processAlert(event: SNSMessage) {
        val value = mapper.readValue(event.Message, AlertPayloadMessage::class.java)
        println(value)
    }

    @SqsListener(value = ["alert-dump"], deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    fun dumpAlerts(event: SNSMessage) {
        val value = mapper.readValue(event.Message, AlertPayloadMessage::class.java)
        println(value)
    }


}
