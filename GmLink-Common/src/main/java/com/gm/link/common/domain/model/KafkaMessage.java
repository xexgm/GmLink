package com.gm.link.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: hhj023
 * @Date: 2025/4/30
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaMessage {

    private String topic;
    private int partition;
    private long offset;
    private byte[] payload;
    private String msgId;
    private String key;
    private String tag;
    private Properties properties = new Properties();
    private Map<String, String> headers = new HashMap<>();

    public String toString() {
        return "KafkaMessage{topic='" + this.topic + '\'' + ", partition=" + this.partition + ", offset=" + this.offset + ", payload=" + new String(this.payload) + ", msgId='" + this.msgId + '\'' + ", key='" + this.key + '\'' + ", tag='" + this.tag + '\'' + ", properties=" + this.properties + ", headers=" + this.headers + '}';
    }
}
