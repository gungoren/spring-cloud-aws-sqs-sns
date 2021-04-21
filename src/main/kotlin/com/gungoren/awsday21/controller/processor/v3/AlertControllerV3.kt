package com.gungoren.awsday21.controller.processor.v3

import com.gungoren.awsday21.data.ApiKeyRepository
import com.gungoren.awsday21.controller.processor.AlertPayload
import com.gungoren.awsday21.engine.RuleEngine
import com.gungoren.awsday21.data.RuleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AlertControllerV3 (
        @Autowired private val apiKeyRepository: ApiKeyRepository,
        @Autowired private val ruleRepository: RuleRepository,
        @Autowired private val queueMessagingTemplate: QueueMessagingTemplate
) {

    private val engine = RuleEngine()

    @PostMapping("ingest/v3")
    fun ingestData(@RequestHeader("X-API-Key") token: String, @RequestBody payload: AlertPayload): ResponseEntity<Boolean> {
        val apiKey = apiKeyRepository.getApiKey(token)

        if (apiKey == null || !apiKey.enabled) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val destinations = HashSet<String>()

        for (rule in ruleRepository.listRule(apiKey.customerId)) {
            val result = engine.executeRule(payload, rule.expression)
            if (result) {
                destinations.add(rule.destination)
            }
        }

        for (destination in destinations) {
            queueMessagingTemplate.convertAndSend("notification", NotificationMessage(payload, destination))
        }

        return ResponseEntity.ok(true)
    }
}
