package com.gungoren.awsday21.controller.processor.v3

import com.gungoren.awsday21.controller.processor.AlertPayload

data class NotificationMessage(
        val payload: AlertPayload,
        val destination: String
)
