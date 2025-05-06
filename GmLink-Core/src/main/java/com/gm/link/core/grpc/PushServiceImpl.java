package com.gm.link.core.grpc;

import com.gm.link.common.grpc.PushGrpc;
import com.gm.link.common.grpc.PushServiceGrpc;
import com.gm.link.core.cache.UserChannelCtxMap;
import com.gm.link.core.cache.UserMachineMap;
import io.grpc.stub.StreamObserver;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

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
        long userId = request.getToId();
        ChannelHandlerContext ctx = UserChannelCtxMap.getChannelCtx(userId);

        // 用户长连接不在当前机器，转发到其他Link机器
        if (ctx == null) {
            // 从 user-machine 关系map 里查找
            Integer targetMachineId = UserMachineMap.getUser2MachineId(userId);
            // 根据机器id，拿到机器之间建立的连接，进行转发
        }

        // 在当前机器，拿到channel
        if (ctx.channel().isActive()) {
            // 推送整个请求给客户端，还是只推送内容 todo
            ChannelFuture channelFuture = ctx.channel().writeAndFlush(request);
            if (channelFuture.isSuccess()) {
                responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(true).build());
            } else {
                responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("推送失败").build());
            }
        }
    }

    @Override
    public void batchPush2Link(PushGrpc.BatchPushRequest request, StreamObserver<PushGrpc.BatchPushResponse> responseObserver) {
        super.batchPush2Link(request, responseObserver);
    }
}
