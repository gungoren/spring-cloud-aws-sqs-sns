package com.gungoren.awsday21

import com.gungoren.awsday21.controller.processor.AlertPayload
import com.gungoren.awsday21.engine.RuleEngine
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class EngineExpressionTest {

    private val engine = RuleEngine()

    @Test
    fun a() {
        val payload = AlertPayload(
                message = "User did not pay",
                service = "billing-service",
                priority = 3,
                details = mapOf(
                        "userId" to "123",
                        "payment" to "3 USD"
                )
        )

        val trueCases = listOf(
                "payload.priority > 2",
                "payload.service === 'billing-service'",
                "payload.details.userId === '123'"
        )

        val falseCases = listOf(
                "payload.message === 'osman'",
                "payload.details.payment === '24 TL'"
        )

        for (trueCase in trueCases) {
            Assertions.assertTrue(engine.executeRule(payload, trueCase), trueCase)
        }

        for (falseCase in falseCases) {
            Assertions.assertFalse(engine.executeRule(payload, falseCase), falseCase)
        }
    }
}
