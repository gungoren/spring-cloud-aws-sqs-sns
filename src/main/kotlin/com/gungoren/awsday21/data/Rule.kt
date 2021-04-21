package com.gungoren.awsday21.data

import com.amazonaws.services.dynamodbv2.datamodeling.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*


@DynamoDBTable(tableName = "rule")
data class Rule(

        @DynamoDBHashKey(attributeName = "customerId")
        var customerId: String = "",

        @DynamoDBRangeKey(attributeName = "id")
        var id: String = "",

        @DynamoDBAttribute(attributeName = "expression")
        var expression: String = "",

        @DynamoDBAttribute(attributeName = "destination")
        var destination: String = ""
)

@Repository
class RuleRepository(
        @Autowired private val mapper: DynamoDBMapper
) {
    fun createRule(customerId: String, team: String, expression: String): String {
        val rule = Rule(customerId, id = UUID.randomUUID().toString(), expression = expression, destination = team)
        mapper.save(rule)
        return rule.id
    }

    fun listRule(customerId: String): List<Rule> {
        val exp = DynamoDBQueryExpression<Rule>().withHashKeyValues(Rule(customerId = customerId))
        val query = mapper.query(Rule::class.java, exp)
        return query.toList()
    }
}
