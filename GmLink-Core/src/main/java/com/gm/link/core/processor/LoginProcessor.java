package com.gm.link.core.processor;

import com.gm.link.common.constant.ChannelAttrKey;
import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.domain.model.MessageBody;
import com.gm.link.common.domain.protobuf.PacketHeader;
import com.gm.link.common.enums.AppId;
import com.gm.link.core.netty.UserChannelCtxMap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import static com.gm.link.common.enums.AppId.LINK_SERVER;
import static com.gm.link.common.enums.MessageType.LOGIN_MESSAGE;

/**
 * @Author: xexgm
 */
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
        // 3.往kafka 发登录消息


        // 响应客户端已登录
        CompleteMessage rspMessage = CompleteMessage.builder()
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
                                .timestamp(System.currentTimeMillis())
                                .content("登录成功")
                                .build()
                ).build();
        ctx.channel().writeAndFlush(rspMessage);
    }

}
