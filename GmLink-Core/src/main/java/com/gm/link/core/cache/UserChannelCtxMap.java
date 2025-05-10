package com.gm.link.core.cache;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: xexgm
 * description: 保存当前机器的连接 userId - channelHandlerCtx 的映射关系
 */
public class UserChannelCtxMap {

    private static ConcurrentHashMap<Long, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>();

    public static void addChannelCtx(Long userId, ChannelHandlerContext ctx) {
        channelMap.putIfAbsent(userId, ctx);
    }

    public static void removeChannelCtx(Long userId) {
        if (userId != null) {
            channelMap.remove(userId);
        }
    }

    public static ChannelHandlerContext getChannelCtx(Long userId) {
        return channelMap.get(userId);
    }
}
