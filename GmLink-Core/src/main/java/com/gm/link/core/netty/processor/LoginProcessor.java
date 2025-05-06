package com.gm.link.core.netty.processor;

import com.gm.link.common.constant.ChannelAttrKey;
import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.domain.model.MessageBody;
import com.gm.link.common.domain.model.RedisOperationMessage;
import com.gm.link.common.domain.protobuf.PacketHeader;
import com.gm.link.common.utils.JsonUtil;
import com.gm.link.core.config.KafkaConfig;
import com.gm.link.core.config.LinkConfig;
import com.gm.link.core.config.RedisConfig;
import com.gm.link.core.kafka.KafkaProducerManager;
import com.gm.link.core.cache.UserChannelCtxMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

import static com.gm.link.common.enums.AppId.LINK_SERVER;
import static com.gm.link.common.enums.MessageType.LOGIN_MESSAGE;

/**
 * @Author: xexgm
 * descrption: 登录处理器
 * todo 减少登录处理器的日志
 */
@Slf4j
public class LoginProcessor extends AbstractMessageProcessor<CompleteMessage>{

    /********* 单例 ********/
    private LoginProcessor(){
    }

    private static final LoginProcessor INSTANCE = new LoginProcessor();

    public static LoginProcessor getInstance(){
        return INSTANCE;
    }

    @Override
    public void process(ChannelHandlerContext ctx, CompleteMessage msg){
        PacketHeader packetHeader = msg.getPacketHeader();
        String token = packetHeader.getToken();
        // todo 验证token

        long userId = packetHeader.getUid();
        // 1.channelCtx 写到 map里
        UserChannelCtxMap.addChannelCtx(userId, ctx);
        // 2.channel 设置属性
        // userId属性
        AttributeKey<Long> userIdKey = AttributeKey.valueOf(ChannelAttrKey.USER_ID);
        ctx.channel().attr(userIdKey).set(userId);
        // 初始化心跳次数属性
        AttributeKey<Long> heartBeatTimesKey = AttributeKey.valueOf(ChannelAttrKey.HEARTBEAT_TIMES);
        ctx.channel().attr(heartBeatTimesKey).set(0L);
        // 3.往kafka 发登录消息,redis保存 userid - 机器id，以表示在线状态

        // 获取 producer
        KafkaProducer<String, String> producer = KafkaProducerManager.getProducer();

        // 构造 kafka 消息
        ProducerRecord<String, String> record = new ProducerRecord<>(
                // topic
                KafkaConfig.LINK_TOPIC,
                // value: 对redis进行操作，添加 userId 机器id，过期时间为300s
                JsonUtil.toJson(RedisOperationMessage
                        .builder()
                        .op(RedisConfig.OP_SETNX)
                        .key(RedisConfig.PREFIX_USER_ID+userId)
                        .value(String.valueOf(LinkConfig.MACHINE_ID))
                        .expireSeconds(Integer.parseInt(RedisConfig.KEY_EXPIRE_TIME))
                        .build()
                )
        );

        // 异步发送到 kafka
        Future<RecordMetadata> future = producer.send(record, (metadata, exception) -> {
            if (ctx.channel().isActive()) {
                // 响应客户端
                ctx.channel().writeAndFlush(
                        CompleteMessage.builder()
                                // 包头携带 业务线id、userId、messageType
                                .packetHeader(
                                        PacketHeader.newBuilder()
                                                .setAppId(LINK_SERVER.getId())
                                                .setUid(userId)
                                                .setMessageType(LOGIN_MESSAGE.getType())
                                                .build()
                                )
                                .messageBody(
                                        MessageBody.builder()
                                                .timeStamp(System.currentTimeMillis())
                                                .content(exception == null ? "登录成功" : "登陆失败: " + exception.getMessage())
                                                .build()
                                ).build()
                );
            }
            // 日志输出
            if (exception != null) {
                log.error("[sendKafka] 发送消息失败: " + exception.getMessage());
            } else {
                log.info("[sendKafka] 发送消息成功,Topic: {}", metadata.topic());
            }
        });

    }

}
