package com.gm.link.core.netty;

import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.utils.SystemUtil;
import com.gm.link.core.codec.MessageProtocolDecoder;
import com.gm.link.core.codec.MessageProtocolEncoder;
import com.gm.link.core.netty.handler.LinkChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Data;

/**
 * @Author: xexgm
 */
@Data
public class NettyClient {

    private Bootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    private Channel channel;

    private final String host;

    private final int port;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void init() {
        this.bootstrap = new Bootstrap();
        if (SystemUtil.useEpollMode()) {
            this.eventLoopGroup = new EpollEventLoopGroup(new DefaultThreadFactory("epoll-netty-nio"));
        } else {
            this.eventLoopGroup = new NioEventLoopGroup(new DefaultThreadFactory("default-netty-nio"));
        }
    }

    // todo 心跳检测、断线重连
    public Channel connect() throws InterruptedException {
        init();
        bootstrap.group(eventLoopGroup)
                .channel(SystemUtil.useEpollMode() ? EpollSocketChannel.class : NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                // 自定义解码器
                                .addLast(new MessageProtocolDecoder())
                                // 自定义编码器
                                .addLast(new MessageProtocolEncoder())
                                // 自定义业务处理器
                                .addLast(new LinkChannelHandler());

                    }
                });

        ChannelFuture future = bootstrap.connect(host, port).sync();
        if (future.isSuccess()) {
            this.channel = future.channel();
        }
        return this.channel;
    }

    public ChannelFuture send(CompleteMessage message) {
        ChannelFuture future = null;
        if (channel != null && channel.isActive()) {
            future = channel.writeAndFlush(message);
        }
        return future;
    }
}
