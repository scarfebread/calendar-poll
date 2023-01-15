package event

import io.confluent.kafka.serializers.KafkaJsonSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import java.io.FileInputStream
import java.util.*

val kafkaConfig: Properties
    get() = Properties().apply {
        load(FileInputStream("/home/scarfebread/code/calendar-poll/src/jvmMain/resources/kafka.properties"))
        this[ProducerConfig.ACKS_CONFIG] = "all"
        this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.qualifiedName
        this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaJsonSerializer::class.qualifiedName
    }