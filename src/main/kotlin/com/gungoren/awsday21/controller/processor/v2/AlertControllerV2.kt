package com.gungoren.awsday21.controller.processor.v2

import com.gungoren.awsday21.data.ApiKeyRepository
import com.gungoren.awsday21.controller.processor.AlertPayload
import com.gungoren.awsday21.engine.RuleEngine
import com.gungoren.awsday21.data.RuleRepository
import com.gungoren.awsday21.service.WebhookNotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Callable
import java.util.concurrent.Executors

@RestController
class AlertControllerV2 (
        @Autowired private val apiKeyRepository: ApiKeyRepository,
        @Autowired private val ruleRepository: RuleRepository,
        @Autowired private val webhookNotificationService: WebhookNotificationService
) {

    private val engine = RuleEngine()

    @PostMapping("ingest/v2")
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

        val pool = Executors.newFixedThreadPool(4)
        for (destination in destinations) {
            pool.submit(Callable {
                webhookNotificationService.send(payload, destination)
            })
        }
        pool.shutdown()

        return ResponseEntity.ok(true)
    }
}
