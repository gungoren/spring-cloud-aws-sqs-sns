package com.gungoren.awsday21.engine

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gungoren.awsday21.controller.processor.AlertPayload
import javax.script.ScriptEngineManager

class RuleEngine {

    fun executeRule(payload: AlertPayload, expression: String): Boolean {
        val engine = ScriptEngineManager().getEngineByName("JavaScript")
        val data = jacksonObjectMapper().writeValueAsString(payload)
        engine.put("data", data)
        engine.eval("var payload = JSON.parse(data)")
        return engine.eval(expression) as Boolean
    }
}
