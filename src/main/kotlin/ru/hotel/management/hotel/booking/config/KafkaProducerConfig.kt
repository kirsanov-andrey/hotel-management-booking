package ru.hotel.management.hotel.admin.config.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import ru.hotel.management.hotel.booking.domain.message.CustomerPush
import ru.hotel.management.hotel.booking.domain.message.HotelPush

@Configuration
class KafkaProducerConfig() {
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String? = null

    private val JAAS_TEMPLATE: String = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";"

    @Bean
    fun producerConfigs(): Map<String, Any?> {
        val props: MutableMap<String, Any?> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        props[ProducerConfig.CLIENT_ID_CONFIG] = "hotel_store"
        props[ProducerConfig.RETRIES_CONFIG] = 2
        val jaasConfig: String  = String.format(JAAS_TEMPLATE, "admin", "admin-secret");
        props["sasl.jaas.config"] = jaasConfig
        props["sasl.mechanism"] = "PLAIN"
        props["security.protocol"] = "SASL_PLAINTEXT"
        return props
    }

    @Bean
    fun hotelProducerFactory(): ProducerFactory<String, HotelPush> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun hotelKafkaTemplate(): KafkaTemplate<String, HotelPush> {
        return KafkaTemplate(hotelProducerFactory())
    }

    @Bean
    fun customerProducerFactory(): ProducerFactory<String, CustomerPush> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }
    @Bean
    fun customerKafkaTemplate(): KafkaTemplate<String, CustomerPush> {
        return KafkaTemplate(customerProducerFactory())
    }
}
