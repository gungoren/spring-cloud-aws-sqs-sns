package com.gungoren.awsday21.service

import com.gungoren.awsday21.controller.processor.AlertPayload
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class WebhookNotificationService (
        @Autowired val restTemplate: RestTemplate
) {
    @Value("\${server.port}")
    private val port = 8080

    fun send(payload: AlertPayload, destination: String) {
        val response = restTemplate.postForEntity("http://localhost:${port}/webhook/${destination}", payload, Boolean::class.java)
    }
}
