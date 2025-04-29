package com.gm.link.core.grpc;

import com.gm.link.common.grpc.PushGrpc;
import com.gm.link.common.grpc.PushServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * @Author: hhj023
 * @Date: 2025/4/28
 * @Description: 单条推送接口
 */
public class PushServiceImpl extends PushServiceGrpc.PushServiceImplBase {

    @Override
    public void push2Link(PushGrpc.PushRequest request, StreamObserver<PushGrpc.PushResponse> responseObserver) {
        super.push2Link(request, responseObserver);
        // todo
        // 接收消息
        // 判断长连接是否在该台机器上，如果不是，则转发
    }

    @Override
    public void batchPush2Link(PushGrpc.BatchPushRequest request, StreamObserver<PushGrpc.BatchPushResponse> responseObserver) {
        super.batchPush2Link(request, responseObserver);
    }
}
