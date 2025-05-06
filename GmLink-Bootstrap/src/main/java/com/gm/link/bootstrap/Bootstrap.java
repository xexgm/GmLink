package com.gm.link.bootstrap;

import com.gm.link.core.Server.GrpcServer;
import com.gm.link.core.Server.NettyServer;
import com.gm.link.core.cache.LinkClusterManager;
import com.gm.link.core.config.LinkConfig;
import com.gm.link.core.config.NacosRegisterConfig;
import com.gm.link.core.register.NacosRegisterCenter;
import com.gm.link.core.register.RegisterCenterProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: xexgm
 */
@Slf4j
public class Bootstrap {

    public static void run(String[] args) {
        new Bootstrap().start(args);
    }

    public void start(String[] args) {
        log.info("[startGmLink]...");

        new NettyServer().start();
        GrpcServer grpcServer = new GrpcServer();
        grpcServer.start();
        grpcServer.blockUntilShutdown();

        // 初始化注册中心
        initConfigCenter();

    }

    private void initConfigCenter() {
        RegisterCenterProcessor registerCenterProcessor = new NacosRegisterCenter();
        registerCenterProcessor.init();
        registerCenterProcessor.subscribeServiceChange(LinkClusterManager::updateClusterInstances);
    }
}
