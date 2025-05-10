package com.gm.link.core.grpc;

import com.gm.link.common.grpc.PushServiceGrpc;
import com.gm.link.core.cache.LinkClusterManager;
import com.gm.link.core.config.GrpcConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xexgm
 * description: grpc客户端管理器
 */
public class GrpcClientManager {

    // 客户端 targetMachineId -> stub
    private static final Map<Integer, PushServiceGrpc.PushServiceFutureStub> asyncStubCache = new ConcurrentHashMap<>();

    // 连接池 targetMachineId -> channel
    private static final Map<Integer, ManagedChannel> channelPool = new ConcurrentHashMap<>();

    public static PushServiceGrpc.PushServiceFutureStub getAsyncStub(Integer machineId) {
        return asyncStubCache.computeIfAbsent(machineId, id ->
            PushServiceGrpc.newFutureStub(getChannel(machineId))
                    .withDeadlineAfter(500, TimeUnit.MILLISECONDS) // 超时时间
        );
    }

    // 连接复用
    private static ManagedChannel getChannel(Integer machineId) {
        return channelPool.computeIfAbsent(machineId, id -> {
            String ip = LinkClusterManager.getClusterMachineIp(id);
            return ManagedChannelBuilder.forAddress(ip, GrpcConfig.GRPC_PORT)
                    .keepAliveTime(300, TimeUnit.SECONDS) // 保持长连接
                    .keepAliveWithoutCalls(true) // 允许空闲保持
                    .maxRetryAttempts(3) // 最大重试次数
                    .build();
        });
    }
}
