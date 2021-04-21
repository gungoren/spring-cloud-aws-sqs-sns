package com.gungoren.awsday21.controller.mgmt

import com.gungoren.awsday21.data.Rule
import com.gungoren.awsday21.data.RuleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/rules")
class RuleController(
        @Autowired private val ruleRepository: RuleRepository
) {

    @PostMapping("/create")
    fun createKey(@RequestBody rule: Rule): ResponseEntity<String> {
        val key = ruleRepository.createRule(rule.customerId, rule.destination, rule.expression)
        return ResponseEntity.ok(key)
    }
}
