package com.gm.link.core.netty;

import com.gm.link.common.utils.SystemUtil;
import com.gm.link.core.codec.MessageProtocolDecoder;
import com.gm.link.core.codec.MessageProtocolEncoder;
import com.gm.link.core.config.LifeCycle;
import com.gm.link.core.config.LinkConfig;
import com.gm.link.core.config.NettyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.gm.link.common.constant.LinkConfigConstant.DEFAULT_PORT;

/**
 * @Author: xexgm
 * description: Netty 服务端实现
 */
public class NettyServer implements LifeCycle {

    private final LinkConfig config;

    // todo 自定义 netty 处理器

    private static final AtomicBoolean started = new AtomicBoolean(false);

    private ServerBootstrap serverBootstrap;
    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;

    public NettyServer() {
        config = new LinkConfig();
        // todo 创建 自定义 netty 管道处理器
        init();
    }
    @Override
    public void init() {
        serverBootstrap = new ServerBootstrap();
        if (SystemUtil.useEpollMode()) {
            bossEventLoopGroup = new EpollEventLoopGroup(NettyConfig.bossEventLoopGroupNum,
                    new DefaultThreadFactory("epoll-netty-boss-nio"));
            workerEventLoopGroup = new EpollEventLoopGroup(NettyConfig.workerEventLoopGroupNum,
                    new DefaultThreadFactory("epoll-netty-worker-nio"));
        } else {
            bossEventLoopGroup = new NioEventLoopGroup(NettyConfig.bossEventLoopGroupNum,
                    new DefaultThreadFactory("default-netty-boss-nio"));
            workerEventLoopGroup = new NioEventLoopGroup(NettyConfig.workerEventLoopGroupNum,
                    new DefaultThreadFactory("default-netty-worker-nio"));
        }
    }

    @Override
    public void start() throws InterruptedException {
        try {
            serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                    .channel(SystemUtil.useEpollMode() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // 自定义解码器
                                    .addLast(new MessageProtocolDecoder())
                                    // 自定义编码器
                                    .addLast(new MessageProtocolEncoder());
                                    // 自定义逻辑处理器
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = serverBootstrap.bind(DEFAULT_PORT).sync();
            future.channel().closeFuture().sync();
        } finally {
            workerEventLoopGroup.shutdownGracefully();
            bossEventLoopGroup.shutdownGracefully();
        }

    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }
}
