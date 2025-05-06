package com.gm.link.core.netty.processor;

import com.gm.link.common.domain.model.CompleteMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: xexgm
 */
public abstract class AbstractMessageProcessor<T> {

    public abstract void process(ChannelHandlerContext ctx, T msg);

}
