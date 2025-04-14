package com.gm.link.core.netty;

import com.gm.link.common.utils.SystemUtil;
import com.gm.link.core.config.LifeCycle;
import com.gm.link.core.config.LinkConfig;
import com.gm.link.core.config.NettyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.atomic.AtomicBoolean;

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
    public void start() {
        serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                .channel(SystemUtil.useEpollMode() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);

    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }
}
