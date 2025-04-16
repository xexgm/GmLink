package com.gm.link.core.processor;

import com.gm.link.common.domain.model.CompleteMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: xexgm
 */
public abstract class AbstractMessageProcessor {

    public void process(ChannelHandlerContext ctx, CompleteMessage msg){}

}
