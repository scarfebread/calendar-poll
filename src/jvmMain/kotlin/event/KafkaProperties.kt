package event

import io.confluent.kafka.serializers.KafkaJsonSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.io.FileInputStream
import java.util.*

val kafkaProducerConfig: Properties
    get() = loadProperties().apply {
        this[ProducerConfig.ACKS_CONFIG] = "all"
        this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.qualifiedName
        this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaJsonSerializer::class.qualifiedName
    }

val kafkaConsumerConfig : Properties
    get() = loadProperties().apply {
        this[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
    }

private fun loadProperties(): Properties {
    return Properties().apply {
        load(FileInputStream("src/jvmMain/resources/kafka.properties"))
        this[SaslConfigs.SASL_JAAS_CONFIG] = this[SaslConfigs.SASL_JAAS_CONFIG]
            .toString()
            .replace("KAFKA_PASSWORD", System.getenv("KAFKA_PASSWORD"))
    }
}