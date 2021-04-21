package com.gungoren.awsday21.controller.processor

data class AlertPayload(
        val message: String,
        val service: String,
        val priority: Int,
        val details: Map<String, Any>
)
