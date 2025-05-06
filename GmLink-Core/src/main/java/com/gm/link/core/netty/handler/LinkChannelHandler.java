package com.gm.link.core.netty.handler;

import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.enums.MessageType;
import com.gm.link.core.netty.processor.AbstractMessageProcessor;
import com.gm.link.core.netty.processor.ProcessorFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author: xexgm
 */
public class LinkChannelHandler extends SimpleChannelInboundHandler<CompleteMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CompleteMessage completeMessage) throws Exception {
        MessageType messageType = MessageType.fromType((short) completeMessage.getPacketHeader().getMessageType());
        if (messageType == null) {
            return;
        }
        AbstractMessageProcessor<CompleteMessage> processor = ProcessorFactory.getProcessor(messageType);
        processor.process(channelHandlerContext, completeMessage);
    }

    // todo 处理 idleState 事件
}
