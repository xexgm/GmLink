package com.gm.link.core.netty.processor;

import static com.gm.link.common.enums.AppId.LINK_SERVER;
import static com.gm.link.common.enums.MessageType.HEARTBEAT_MESSAGE;

import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.gm.link.common.constant.ChannelAttrKey;
import com.gm.link.common.domain.model.RedisOperationMessage;
import com.gm.link.common.domain.protobuf.CompleteMessage;
import com.gm.link.common.domain.protobuf.PacketBody;
import com.gm.link.common.domain.protobuf.PacketHeader;
import com.gm.link.common.utils.JsonUtil;
import com.gm.link.core.cache.UserChannelCtxMap;
import com.gm.link.core.config.KafkaConfig;
import com.gm.link.core.config.RedisConfig;
import com.gm.link.core.kafka.KafkaProducerManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: xexgm
 *          description: 处理 客户端 -> 中台 心跳消息
 *          todo 减少心跳处理器的日志
 */
@Slf4j
public class HeartBeatProcessor extends AbstractMessageProcessor<CompleteMessage> {

    /********* 单例 ********/
    private HeartBeatProcessor() {
    }

    private static final HeartBeatProcessor INSTANCE = new HeartBeatProcessor();

    public static HeartBeatProcessor getInstance() {
        return INSTANCE;
    }

    @Override
    public void process(ChannelHandlerContext ctx, CompleteMessage msg) {
        // 从心跳消息中拿到 uid
        long uid = msg.getPacketHeader().getUid();
        log.info("[HeartBeatMessage] uid: {}", uid);

        // 拿到 channel，对心跳次数+1
        ChannelHandlerContext heartBeatCtx = UserChannelCtxMap.getChannelCtx(uid);
        if (heartBeatCtx == null || !heartBeatCtx.channel().isActive()) {
            log.info("[HeartBeatMessage] 客户端channel连接断开 uid: {}", uid);
            return;
        }
        AttributeKey<Long> heartBeatTimesKey = AttributeKey.valueOf(ChannelAttrKey.HEARTBEAT_TIMES);
        Long lastHeartBeatTimes = heartBeatCtx.channel().attr(heartBeatTimesKey).get();
        Long heartBeatTimes = lastHeartBeatTimes == null ? 1 : lastHeartBeatTimes + 1;
        heartBeatCtx.channel().attr(heartBeatTimesKey).set(heartBeatTimes);

        long lastTimes = lastHeartBeatTimes == null ? 0 : lastHeartBeatTimes;

        // 判断 %3 == 0 ? 续期redis
        if (lastTimes + 1 % 3 != 0) {
            return;
        }
        log.info("[HeartBeatMessage] 心跳达到三次，续期redis uid: {}, times: {}", uid, heartBeatTimes);

        // 构造续期redis 的 kafka消息
        ProducerRecord<String, byte[]> expireRecord = new ProducerRecord<>(
                // topic
                KafkaConfig.LINK_TOPIC,
                String.valueOf(uid),
                // value: 对redis进行操作，添加 userId 机器id，过期时间为300s
                CompleteMessage.newBuilder()
                .setPacketHeader(
                        PacketHeader.newBuilder()
                                .setAppId(LINK_SERVER.getId())
                                .setUid(uid)
                                .setMessageType(HEARTBEAT_MESSAGE.getType())
                                .build()
                )
                .setPacketBody(
                        PacketBody.newBuilder()
                                .setTimeStamp(System.currentTimeMillis())
                                .build()
                )
                .build()
                .toByteArray());

        KafkaProducerManager.getProducer().send(expireRecord, (metadata, exception) -> {
            if (heartBeatCtx.channel().isActive()) {
                heartBeatCtx.channel().writeAndFlush(
                        CompleteMessage.newBuilder()
                                // 包头携带 业务线id、userId、messageType
                                .setPacketHeader(
                                        PacketHeader.newBuilder()
                                                .setAppId(LINK_SERVER.getId())
                                                .setUid(uid)
                                                .setMessageType(HEARTBEAT_MESSAGE.getType())
                                                .build())
                                .setPacketBody(
                                        PacketBody.newBuilder()
                                                .setTimeStamp(System.currentTimeMillis())
                                                .setContent(exception == null ? "心跳续期成功"
                                                        : "心跳续期失败: " + exception.getMessage())
                                                .build())
                                .build());
            }
            // 日志输出
            if (exception != null) {
                log.error("[ExpireRedisKey] 接受心跳，续期redis失败: " + exception.getMessage());
            } else {
                log.info("[ExpireRedisKey] 接受心跳,续期redis成功, key: {}", RedisConfig.PREFIX_USER_ID + uid);
            }
        });

    }
}
