package com.gm.link.core.Server;

import com.gm.link.core.config.GrpcConfig;
import com.gm.link.core.grpc.PushServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Author: hhj023
 * @Date: 2025/4/30
 * @Description:
 */
@Slf4j
public class GrpcServer {
    private final int port;
    private Server server;

    public GrpcServer() {
        this.port = GrpcConfig.GRPC_PORT;
    }

    public void start() {
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new PushServiceImpl())
                    .build()
                    .start();
        } catch (IOException e) {
            log.info("[grpcServer] 初始化异常: {}", e.getMessage());
        }
    }

    public void blockUntilShutdown() {
        if (server != null) {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                log.info("[grpcServer] 关闭异常: {}", e.getMessage());
            }
        }
    }
}
