package com.gm.link.core.kafka;

import com.gm.link.core.config.KafkaConfig;
import lombok.Getter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * @Author: xexgm
 */
public class KafkaProducerManager {

    private static final Properties props = new Properties() {
        {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.SERVERS_CONFIG);
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            put(ProducerConfig.ACKS_CONFIG, "1");
        }
    };

    /* 状态服务生产者 */
    @Getter
    private static KafkaProducer<String, String> producer = new KafkaProducer<>(props);

//    static {
//        // todo kafka地址待填
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.SERVERS_CONFIG);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        // 批处理核心参数
////        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 1024 * 1024); // 批次大小 1Mb
////        props.put(ProducerConfig.LINGER_MS_CONFIG, 100); // 批次等待时间 100ms
////        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 32 * 1024 * 1024);
//        // 可选优化配置
//        props.put(ProducerConfig.ACKS_CONFIG, "1");
//    }

    public static void shutdown() {
        if (producer != null) {
            producer.close();
        }
    }
}
