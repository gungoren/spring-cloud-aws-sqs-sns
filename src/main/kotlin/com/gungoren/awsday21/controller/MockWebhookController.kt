package com.gungoren.awsday21.controller

import com.gungoren.awsday21.controller.processor.AlertPayload
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.random.Random

@RestController
@RequestMapping("/webhook")
class MockWebhookController {


    private val logger = LoggerFactory.getLogger(MockWebhookController::class.java)

    @PostMapping("/{team}")
    fun mockWebhook(
            @PathVariable team: String,
            @RequestBody payload: AlertPayload
    ) : ResponseEntity<Boolean> {
        randomSleep()
        logger.info("\u0830\u0025 Received Alert for: {} {}", team, payload.message)
        return ResponseEntity.ok(true)
    }

    fun randomSleep() {
        val r = Random(System.currentTimeMillis())
        val duration = r.nextInt(500) + 1000
        Thread.sleep(duration.toLong())
    }
}
