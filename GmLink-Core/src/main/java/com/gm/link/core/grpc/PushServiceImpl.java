package com.gm.link.core.grpc;

import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.domain.model.MessageBody;
import com.gm.link.common.domain.protobuf.PacketHeader;
import com.gm.link.common.grpc.PushGrpc;
import com.gm.link.common.grpc.PushServiceGrpc;
import com.gm.link.core.cache.LinkClusterManager;
import com.gm.link.core.cache.UserChannelCtxMap;
import com.gm.link.core.config.LinkConfig;
import com.gm.link.core.config.NettyConfig;
import com.gm.link.core.netty.NettyClient;
import com.gm.link.core.redis.RedisClient;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.stub.StreamObserver;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * @Author: hhj023
 * @Date: 2025/4/28
 * @Description: 推送接口
 */
// todo 响应码需要新增和优化，便于debug
@Slf4j
public class PushServiceImpl extends PushServiceGrpc.PushServiceImplBase {

    // 方案1：Abandoned，中台机器内部 channel 转发
    // 服务端接受到内部转发的消息的处理器 （messageType == 8 的处理器）handler里的 processor
//    @Override
//    public void push2Link(PushGrpc.PushRequest request, StreamObserver<PushGrpc.PushResponse> responseObserver) {
//        // 接收消息
//        long userId = request.getToId();
//        ChannelHandlerContext ctx = UserChannelCtxMap.getChannelCtx(userId);
//
//        // case1: 用户长连接不在当前机器，转发到其他Link机器
//        if (ctx == null) {
//            // 从 user-machine 关系map 里查找
//            Integer targetMachineId = UserMachineMap.getUser2MachineId(userId);
//
//            // todo 如果从当前map找不到，是否引入重试，因为 toId，要么有连接，要么下线了
//            // todo 有连接，但是 targetMachineId == 0，也有可能是因为同步不及时,这个广播同步还没做
//
//            // 根据机器id，拿到机器之间建立的连接，进行转发
//            // 判断是否已经存在连接，否则新建连接
//            Channel targetChannel = LinkClusterManager.getLinkId2Channel(targetMachineId);
//
//            // 构建消息进行转发
//            CompleteMessage forwardMessage = buildForwardMessage(request);
//            ChannelFuture channelFuture = null;
//
//            // case i:管道连接存在并且可用
//            if (targetChannel != null && targetChannel.isActive()) {
//                channelFuture = targetChannel.writeAndFlush(forwardMessage);
//                // todo 通过监听器获取响应
//                channelFuture.addListener(future -> {
//                    if (future.isSuccess()) {
//                        responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(true).build());
//                    } else {
//                        responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("转发失败").build());
//                    }
//                });
//                return;
//            }
//
//            // case ii: channel不存在或者不可用，新建连接 todo 如何维护 channel?
//            // 这里只要管转发给目标机器就行
//            NettyClient nettyClient = null;
//            try {
//                nettyClient = connectTargetMachine(targetMachineId);
//            } catch (InterruptedException e) {
//                log.error("[linkClientConnectLinkServer] 中台客户端转发建立连接失败: {}", e.getMessage());
//            }
//
//            if (nettyClient == null || nettyClient.getChannel() == null || !nettyClient.getChannel().isActive()) {
//                // 中台内部连接建立失败，返回响应
//                responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("中台内部连接建立失败").build());
//                return;
//            }
//
//            Channel newChannel = nettyClient.getChannel();
//            // 维护channel, 建立当前机器与目标机器的channel
//            LinkClusterManager.addLinkId2Channel(targetMachineId, newChannel);
//
//            // 转发
//            channelFuture = nettyClient.send(forwardMessage);
//
//            if (channelFuture == null) {
//                responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("转发失败").build());
//                return;
//            }
//
//            channelFuture.addListener(future -> {
//                if (future.isSuccess()) {
//                    responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(true).build());
//                } else {
//                    responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("转发失败").build());
//                }
//            });
//            return;
//        }
//
//        // case2: 在当前机器，拿到channel
//        if (ctx.channel().isActive()) {
//            // 推送内容
//            ChannelFuture channelFuture = ctx.channel().writeAndFlush(request.getContent());
//            // 这里要通过监听器来获得响应
//            channelFuture.addListener(future -> {
//                if (future.isSuccess()) {
//                    responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(true).build());
//                } else {
//                    responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("推送失败").build());
//                }
//            });
//            return;
//        }
//
//        // todo 在当前机器，但是不活跃，响应给客户端，连接不可用，做断线重连等等处理
//        responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("客户端与中台失去连接").build());
//    }

    /**
     * 选用方案2：grpc转发
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void push2Link(PushGrpc.PushRequest request, StreamObserver<PushGrpc.PushResponse> responseObserver) {
        long toId = request.getToId();
        ChannelHandlerContext ctx = UserChannelCtxMap.getChannelCtx(toId);

        // case1: 用户长连接不在当前机器，转发到其他Link机器
        if (ctx == null) {
            // 从 redis拿到用户在线状态 userId -> 机器id
            Integer targetMachineId = RedisClient.getMachineId(toId);
            // 机器id判空处理
            if (targetMachineId == null) {
                responseObserver.onNext(buildErrorResponse("用户不在线"));
                responseObserver.onCompleted();
                return;
            }

            // 拿到grpc客户端
            PushServiceGrpc.PushServiceFutureStub stub = GrpcClientManager.getAsyncStub(targetMachineId);
            // 调用目标机器的grpc服务
            ListenableFuture<PushGrpc.PushResponse> future = stub.push2Link(request);

            Futures.addCallback(future, new FutureCallback<PushGrpc.PushResponse>() {
                @Override
                public void onSuccess(PushGrpc.PushResponse result) {
                    responseObserver.onNext(result);
                    responseObserver.onCompleted();
                }

                @Override
                public void onFailure(Throwable t) {
                    log.error("异步推送失败", t);
                    responseObserver.onNext(buildErrorResponse("异步调用失败"));
                    responseObserver.onCompleted();
                }
                // todo 这里的参数，考究一下
            }, Executors.newCachedThreadPool());
            return;
        }

        // case2: 在当前机器，拿到channel
        if (ctx.channel().isActive()) {
            // 推送内容
            ChannelFuture channelFuture = ctx.channel().writeAndFlush(request.getContent());
            // 这里要通过监听器来获得响应
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(true).build());
                    responseObserver.onCompleted();
                } else {
                    responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("推送失败").build());
                    responseObserver.onCompleted();
                }
            });
            return;
        }
        // channel不活跃，响应给客户端，连接不可用，做断线重连等等处理
        responseObserver.onNext(buildErrorResponse("客户端与中台失去连接"));
        responseObserver.onCompleted();
    }


    // todo 批量处理 2000人以上用 标签方案优化
    @Override
    public void batchPush2Link(PushGrpc.BatchPushRequest request, StreamObserver<PushGrpc.BatchPushResponse> responseObserver) {
        List<Long> toIdList = request.getToIdsList();

        //

        // 2000人以下 普通处理批量消息

        // 2000人以上 使用标签优化
    }


    // 普通处理批量消息
    // 根据 toIds，拿到 目标机器id，按照 目标机器id 进行分组，每组的数据为  toIdList
    // 遍历每个分组，判断分组的 targetMachineId 是否为当前机器，若是，直接推送
    // 若不是，调用目标机器的 批量消息grpc接口，进行转发
//    private PushGrpc.BatchPushResponse batchPushStrategy() {
//
//    }


    // 群聊业务层，需要维护 标签id
    // redis: tagId -> machineIds
    // Link中台: tagId -> channels (或者 userId，根据userId，去本地缓存拿 channel，拿不到就响应，业务层做其他处理)
    // 根据标签id，从redis查 machineIds，判断 machineIds 是否有效
//    private PushGrpc.BatchPushResponse tagSubscribeStrategy() {
//
//    }



    public CompleteMessage buildForwardMessage(PushGrpc.PushRequest request) {
        return CompleteMessage.builder()
                .packetHeader(
                        PacketHeader.newBuilder()
                                .setUid(request.getFromUserId())
                                .setMessageType((short) 8) // 8 -> 集群转发下行消息
                                .build()
                )
                .messageBody(
                        MessageBody.builder()
                                .content(request.getContent())
                                .timeStamp(System.currentTimeMillis())
                                .toId(request.getToId())
                                .messageType((short) 8)
                                .build()
                ).build();
    }

    /**
     * channel机器互连使用，现方案不使用该方法
     *
     * @param targetMachineId
     * @return
     * @throws InterruptedException
     */
    public NettyClient connectTargetMachine(Integer targetMachineId) throws InterruptedException {
        // 拿到目标机器的ip和端口
        String targetMachineIp = LinkClusterManager.getClusterMachineIp(targetMachineId);
        // 中台netty服务监听 9999 端口
        int targetMachinePort = NettyConfig.port;

        // 创建NettyClient
        NettyClient nettyClient = new NettyClient(targetMachineIp, targetMachinePort);

        // 尝试连接目标机器，连接失败直接抛出去异常
        nettyClient.connect();
        log.info("[linkClientConnectLinkServer] 中台客户端连接服务端成功, clientLinkId: {}, targetLinkId: {}",
                LinkConfig.MACHINE_ID, targetMachineId);

        return nettyClient;
    }


    /**
     * 构造失败响应
     *
     * @param msg
     * @return
     */
    private PushGrpc.PushResponse buildErrorResponse(String msg) {
        return PushGrpc.PushResponse.newBuilder()
                .setSuccess(false)
                .setMsg(msg)
                .build();
    }
}
