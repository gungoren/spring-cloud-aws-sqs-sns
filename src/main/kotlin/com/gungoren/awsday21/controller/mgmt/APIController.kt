package com.gungoren.awsday21.controller.mgmt

import com.gungoren.awsday21.data.ApiKeyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/keys")
class APIController(@Autowired private val apiKeyRepository: ApiKeyRepository) {


    @GetMapping("/create")
    fun createKey(@RequestParam customerId: String): ResponseEntity<String> {
        val key = apiKeyRepository.createApiKey(customerId)
        return ResponseEntity.ok(key)
    }
}
