package com.gm.link.core.netty.handler;

import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.domain.model.MessageBody;
import com.gm.link.common.domain.protobuf.PacketHeader;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import static com.gm.link.common.enums.AppId.LINK_SERVER;
import static com.gm.link.common.enums.MessageType.HEARTBEAT_MESSAGE;
import static io.netty.handler.timeout.IdleState.READER_IDLE;

/**
 * @Author: xexgm
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    // 计数器，满三次就续期 redis

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 触发空闲事件
        if (evt instanceof IdleStateEvent event) {
            // 读空闲事件
            if (READER_IDLE.equals(event.state())) {
                // 发送心跳消息
                ctx.writeAndFlush(getHeartBeatMessage(System.currentTimeMillis()));
            }
        }
    }


    // 构造心跳消息
    private static CompleteMessage getHeartBeatMessage(long timestamp) {
        // 包体可以带一下时间戳，设定超时等待的事件
        return new CompleteMessage(
                PacketHeader
                        .newBuilder()
                        .setAppId(LINK_SERVER.getId())
                        .setMessageType(HEARTBEAT_MESSAGE.getType())
                        .build(),
                // 包体可以带一下时间戳，设定超时等待的事件
                MessageBody
                        .builder()
                        .timeStamp(timestamp)
                        .build()
        );
    }
}
