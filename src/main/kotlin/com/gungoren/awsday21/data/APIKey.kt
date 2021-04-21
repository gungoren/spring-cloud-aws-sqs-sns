package com.gungoren.awsday21.data

import com.amazonaws.services.dynamodbv2.datamodeling.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*

@DynamoDBTable(tableName = "api-key")
data class APIKey(

        @DynamoDBHashKey(attributeName = "token")
        var token: String = "",

        @DynamoDBAttribute(attributeName = "customerId")
        var customerId: String = "",

        @DynamoDBAttribute(attributeName = "enabled")
        var enabled: Boolean = true
)


@Repository
class ApiKeyRepository(
        @Autowired private val mapper: DynamoDBMapper
) {
    fun createApiKey(customerId: String): String {
        val rule = APIKey(UUID.randomUUID().toString(), customerId)
        mapper.save(rule)
        return rule.token
    }

    fun getApiKey(token: String): APIKey? {
        val exp = DynamoDBQueryExpression<APIKey>().withHashKeyValues(APIKey(token = token))
        val query = mapper.query(APIKey::class.java, exp)
        return query.toList().firstOrNull()
    }
}

