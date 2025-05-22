package com.gm.link.core.kafka;

import com.gm.link.core.config.KafkaConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * @Author: xexgm
 */
@Slf4j
public class KafkaProducerManager {

    private static final Properties props = new Properties() {
        {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            put(ProducerConfig.ACKS_CONFIG, "1");
        }
    };

    /* 状态服务生产者 */
    private static KafkaProducer<String, String> producer;

//    static {
//        try {
//            producer = new KafkaProducer<>(props);
//        } catch (Exception e) {
//            log.error("KafkaProducerManager init error: {}", e.getMessage());
//        }
//    }

    //    public static KafkaProducer<String, String> getProducer() {
//        return producer;
//    }
    public static KafkaProducer<String, String> getProducer() {
        if (producer == null) {
            synchronized (KafkaProducerManager.class) {
                if (producer == null) {
                    try {
                        producer = new KafkaProducer<>(props);
                        log.info("KafkaProducer 初始化成功");
                    } catch (Exception e) {
                        log.error("KafkaProducer 初始化失败: ", e);
                        throw new RuntimeException("KafkaProducer 初始化失败", e);
                    }
                }
            }
        }
        return producer;
    }

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
