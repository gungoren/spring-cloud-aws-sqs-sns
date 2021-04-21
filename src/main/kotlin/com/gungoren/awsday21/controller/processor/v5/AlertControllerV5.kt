package com.gungoren.awsday21.controller.processor.v5

import com.gungoren.awsday21.data.ApiKeyRepository
import com.gungoren.awsday21.controller.processor.AlertPayload
import com.gungoren.awsday21.controller.processor.v4.AlertPayloadMessage
import com.gungoren.awsday21.engine.RuleEngine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AlertControllerV5 (
        @Autowired private val apiKeyRepository: ApiKeyRepository,
        @Autowired private val notificationMessagingTemplate: NotificationMessagingTemplate
) {

    private val engine = RuleEngine()

    @PostMapping("ingest/v5")
    fun ingestData(@RequestHeader("X-API-Key") token: String, @RequestBody payload: AlertPayload): ResponseEntity<Boolean> {
        val apiKey = apiKeyRepository.getApiKey(token)

        if (apiKey == null || !apiKey.enabled) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        notificationMessagingTemplate.convertAndSend("alert-payload-topic", AlertPayloadMessage(payload, apiKey.customerId))

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(true)
    }
}
