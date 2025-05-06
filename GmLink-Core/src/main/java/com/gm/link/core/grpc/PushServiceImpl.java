package com.gm.link.core.grpc;

import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.domain.model.MessageBody;
import com.gm.link.common.domain.protobuf.PacketHeader;
import com.gm.link.common.grpc.PushGrpc;
import com.gm.link.common.grpc.PushServiceGrpc;
import com.gm.link.core.cache.LinkClusterManager;
import com.gm.link.core.cache.UserChannelCtxMap;
import com.gm.link.core.cache.UserMachineMap;
import com.gm.link.core.config.LinkConfig;
import com.gm.link.core.config.NettyConfig;
import com.gm.link.core.netty.NettyClient;
import io.grpc.stub.StreamObserver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: hhj023
 * @Date: 2025/4/28
 * @Description: 推送接口
 */
@Slf4j
public class PushServiceImpl extends PushServiceGrpc.PushServiceImplBase {

    // todo 服务端接受到内部转发的消息的处理器 （messageType == 8 的处理器）handler里的 processor
    // todo 响应码需要新增和优化，便于debug
    @Override
    public void push2Link(PushGrpc.PushRequest request, StreamObserver<PushGrpc.PushResponse> responseObserver) {
        // 接收消息
        long userId = request.getToId();
        ChannelHandlerContext ctx = UserChannelCtxMap.getChannelCtx(userId);

        // case1: 用户长连接不在当前机器，转发到其他Link机器
        if (ctx == null) {
            // 从 user-machine 关系map 里查找
            Integer targetMachineId = UserMachineMap.getUser2MachineId(userId);

            // todo 如果从当前map找不到，是否引入重试，因为 toId，要么有连接，要么下线了
            // todo 有连接，但是 targetMachineId == 0，也有可能是因为同步不及时,这个广播同步还没做

            // 根据机器id，拿到机器之间建立的连接，进行转发
            // 判断是否已经存在连接，否则新建连接
            Channel targetChannel = LinkClusterManager.getLinkId2Channel(targetMachineId);

            // 构建消息进行转发
            CompleteMessage forwardMessage = buildForwardMessage(request);
            ChannelFuture channelFuture = null;

            // case i:管道连接存在并且可用
            if (targetChannel != null && targetChannel.isActive()) {
                channelFuture = targetChannel.writeAndFlush(forwardMessage);
                // todo 通过监听器获取响应
                channelFuture.addListener(future -> {
                    if (future.isSuccess()) {
                        responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(true).build());
                    } else {
                        responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("转发失败").build());
                    }
                });
                return;
            }

            // case ii: channel不存在或者不可用，新建连接 todo 如何维护 channel?
            // 这里只要管转发给目标机器就行
            NettyClient nettyClient = null;
            try {
                nettyClient = connectTargetMachine(targetMachineId);
            } catch (InterruptedException e) {
                log.error("[linkClientConnectLinkServer] 中台客户端转发建立连接失败: {}", e.getMessage());
            }

            if (nettyClient == null || nettyClient.getChannel() == null || !nettyClient.getChannel().isActive()) {
                // 中台内部连接建立失败，返回响应
                responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("中台内部连接建立失败").build());
                return;
            }

            Channel newChannel = nettyClient.getChannel();
            // 维护channel, 建立当前机器与目标机器的channel
            LinkClusterManager.addLinkId2Channel(targetMachineId, newChannel);

            // 转发
            channelFuture = nettyClient.send(forwardMessage);

            if (channelFuture == null) {
                responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("转发失败").build());
                return;
            }

            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(true).build());
                } else {
                    responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("转发失败").build());
                }
            });
            return;
        }

        // case2: 在当前机器，拿到channel
        if (ctx != null && ctx.channel().isActive()) {
            // 推送内容
            ChannelFuture channelFuture = ctx.channel().writeAndFlush(request.getContent());
            // 这里要通过监听器来获得响应
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(true).build());
                } else {
                    responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("推送失败").build());
                }
            });
            return;
        }

        // todo 在当前机器，但是不活跃，响应给客户端，连接不可用，做断线重连等等处理
        responseObserver.onNext(PushGrpc.PushResponse.newBuilder().setSuccess(false).setMsg("客户端与中台失去连接").build());
    }

    @Override
    public void batchPush2Link(PushGrpc.BatchPushRequest request, StreamObserver<PushGrpc.BatchPushResponse> responseObserver) {
        super.batchPush2Link(request, responseObserver);
    }

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
}
