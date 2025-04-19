package com.gm.link.core.kafka;

import com.gm.link.core.config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * @Author: xexgm
 */
public class KafkaProducerManager {
    private static volatile KafkaProducer<String, String> producer;

    public static synchronized KafkaProducer<String, String> getProducer() {
        if (producer == null) {
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.SERVERS_CONFIG);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            // 可选优化配置
            props.put(ProducerConfig.ACKS_CONFIG, "all"); // 高可靠性
            props.put(ProducerConfig.RETRIES_CONFIG, 3);  // 重试次数
            // todo 批处理配置
            if (producer == null) {
                producer = new KafkaProducer<>(props);
            }
        }
        return producer;
    }

    public static void shutdown() {
        if (producer != null) {
            producer.close();
        }
    }
}
