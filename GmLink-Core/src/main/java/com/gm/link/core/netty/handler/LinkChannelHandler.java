package com.gm.link.core.netty.handler;

import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.enums.MessageType;
import com.gm.link.core.netty.processor.AbstractMessageProcessor;
import com.gm.link.core.netty.processor.ProcessorFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * @Author: xexgm
 * description: 责任链 -> 处理上行消息
 */
public class LinkChannelHandler extends SimpleChannelInboundHandler<CompleteMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CompleteMessage completeMessage) throws Exception {
        MessageType messageType = MessageType.fromType((short) completeMessage.getPacketHeader().getMessageType());
        if (messageType == null) {
            // 在读的中途停止了传递，手动释放 ByteBuf，避免内存泄漏
            ReferenceCountUtil.release(completeMessage);
            return;
        }
        AbstractMessageProcessor<CompleteMessage> processor = ProcessorFactory.getProcessor(messageType);
        processor.process(channelHandlerContext, completeMessage);

    }
}
