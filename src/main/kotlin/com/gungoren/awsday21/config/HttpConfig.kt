package com.gungoren.awsday21.config

import org.apache.http.HeaderElementIterator
import org.apache.http.client.config.RequestConfig
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.ConnectionKeepAliveStrategy
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.message.BasicHeaderElementIterator
import org.apache.http.protocol.HTTP
import org.apache.http.ssl.SSLContextBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException


@Configuration
class HttpConfig {

    private val logger = LoggerFactory.getLogger(HttpConfig::class.java)

    // Determines the timeout in milliseconds until a connection is established.
    private val CONNECT_TIMEOUT = 30000

    // The timeout when requesting a connection from the connection manager.
    private val REQUEST_TIMEOUT = 30000

    // The timeout for waiting for data
    private val SOCKET_TIMEOUT = 60000

    private val MAX_TOTAL_CONNECTIONS = 50
    private val DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000
    private val CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30


    @Autowired
    var httpClient: CloseableHttpClient? = null

    @Bean
    fun restTemplate(): RestTemplate? {
        return clientHttpRequestFactory()?.let { RestTemplate(it) }
    }

    @Bean
    fun clientHttpRequestFactory(): HttpComponentsClientHttpRequestFactory? {
        val clientHttpRequestFactory = HttpComponentsClientHttpRequestFactory()
        clientHttpRequestFactory.httpClient = httpClient!!
        return clientHttpRequestFactory
    }

    @Bean
    fun httpClient(): CloseableHttpClient? {
        val requestConfig: RequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build()
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build()
    }

    @Bean
    fun poolingConnectionManager(): PoolingHttpClientConnectionManager? {
        val builder = SSLContextBuilder()
        try {
            builder.loadTrustMaterial(null, TrustSelfSignedStrategy())
        } catch (e: NoSuchAlgorithmException) {
            logger.error("Pooling Connection Manager Initialisation failure because of " + e.message, e)
        } catch (e: KeyStoreException) {
            logger.error("Pooling Connection Manager Initialisation failure because of " + e.message, e)
        }
        var sslsf: SSLConnectionSocketFactory? = null
        try {
            sslsf = SSLConnectionSocketFactory(builder.build())
        } catch (e: KeyManagementException) {
            logger.error("Pooling Connection Manager Initialisation failure because of " + e.message, e)
        } catch (e: NoSuchAlgorithmException) {
            logger.error("Pooling Connection Manager Initialisation failure because of " + e.message, e)
        }
        val socketFactoryRegistry: Registry<ConnectionSocketFactory?> = RegistryBuilder
                .create<ConnectionSocketFactory?>().register("https", sslsf)
                .register("http", PlainConnectionSocketFactory())
                .build()
        val poolingConnectionManager = PoolingHttpClientConnectionManager(socketFactoryRegistry)
        poolingConnectionManager.maxTotal = MAX_TOTAL_CONNECTIONS
        return poolingConnectionManager
    }

    @Bean
    fun connectionKeepAliveStrategy(): ConnectionKeepAliveStrategy? {
        return ConnectionKeepAliveStrategy { response, context ->
            val it: HeaderElementIterator = BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE))
            while (it.hasNext()) {
                val he = it.nextElement()
                val param = he.name
                val value = he.value
                if (value != null && param.equals("timeout", ignoreCase = true)) {
                    return@ConnectionKeepAliveStrategy value.toLong() * 1000
                }
            }
            DEFAULT_KEEP_ALIVE_TIME_MILLIS.toLong()
        }
    }

}
