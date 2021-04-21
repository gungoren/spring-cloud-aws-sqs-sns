package com.gungoren.awsday21.config

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter





@Configuration
class AwsConfig {

    @Value("\${cloud.aws.credentials.profile-name}")
    private var profile = ""

    @Bean
    fun queue(): QueueMessagingTemplate {
        return QueueMessagingTemplate(AmazonSQSAsyncClientBuilder
                .standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(ProfileCredentialsProvider(profile))
                .build()
        )
    }

    @Bean
    fun dynamo(): DynamoDBMapper {
        return DynamoDBMapper(AmazonDynamoDBAsyncClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).withCredentials(ProfileCredentialsProvider(profile)).build())
    }

    @Bean
    fun topic(): NotificationMessagingTemplate {
        return NotificationMessagingTemplate(AmazonSNSAsyncClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).withCredentials(ProfileCredentialsProvider(profile)).build())
    }

    private fun initObjectMapper(): ObjectMapper? {
        val objectMapper = ObjectMapper()
        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return objectMapper
    }

    @Bean
    fun mappingJackson2MessageConverter(objectMapper: ObjectMapper?): MappingJackson2MessageConverter? {
        val jackson2MessageConverter = MappingJackson2MessageConverter()
        jackson2MessageConverter.objectMapper = objectMapper!!
        return jackson2MessageConverter
    }

}
