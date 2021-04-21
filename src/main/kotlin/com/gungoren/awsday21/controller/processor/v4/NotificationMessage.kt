package com.gungoren.awsday21.controller.processor.v4

import com.gungoren.awsday21.controller.processor.AlertPayload

data class NotificationMessage( val payload: AlertPayload, val destination: String)


data class AlertPayloadMessage(val payload: AlertPayload, val customerId: String)
