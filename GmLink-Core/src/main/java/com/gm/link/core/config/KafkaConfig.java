package com.gm.link.core.config;

/**
 * @Author: xexgm
 */
public class KafkaConfig {

    /**
     * kafka host
     */
    public static String SERVERS_CONFIG = "";

    /**
     * 登录、心跳、登出 topic
     */
    public static String LINK_TOPIC = "link_topic";

    /**
     * 中台机器处理下行消息的 kafka 分区格式: down_link_topic_${machineId}
     */
    public static String DOWN_LINK_TOPIC_PREFIX = "down_link_topic_";

}
