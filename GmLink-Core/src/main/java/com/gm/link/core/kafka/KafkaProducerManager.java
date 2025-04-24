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
    private static Properties props = new Properties();

    static {
        // todo kafka地址待填
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.SERVERS_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // 批处理核心参数
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 1 * 1024 * 1024); // 批次大小 1Mb
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1000); // 批次等待时间 1s
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 32 * 1024 * 1024);
        // 可选优化配置
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // 高可靠性
        props.put(ProducerConfig.RETRIES_CONFIG, 3);  // 重试次数
    }

    public static KafkaProducer<String, String> getProducer() {
        if (producer == null) {
            synchronized (KafkaProducerManager.class) {
                if (producer == null) {
                    producer = new KafkaProducer<>(props);
                }
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
