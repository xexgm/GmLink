package com.gm.link.core.Server;

import com.gm.link.core.grpc.PushServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * @Author: hhj023
 * @Date: 2025/4/30
 * @Description:
 */
public class GrpcServer {
    private final int port;
    private Server server;

    public GrpcServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new PushServiceImpl())
                .build()
                .start();
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
