package com.gm.link.core.cache;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.gm.link.core.config.NacosRegisterConfig;
import io.netty.channel.ChannelHandlerContext;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: xexgm
 * description: 维护两类数据
 *              1.gm-Link集群信息，机器id -> 机器ip
 *              2.当前机器与其他机器建立的channel，用于下行消息的转发
 */
public class LinkClusterManager {

    // machineId -> machineIp
    // 需要提供 updateInstances、get对应机器ip方法
    private static final ConcurrentHashMap<Integer, String> clusterMachineIpMap = new ConcurrentHashMap<>();

    // 当前机器 与 目标机器 建立的channel machineId -> channel
    // 需要提供 add、delete、get 对应 channel 方法
    private static final ConcurrentHashMap<Integer, ChannelHandlerContext> LinkId2ChannelMap = new ConcurrentHashMap<>();

    // nacos 监听器触发
    public static void updateClusterInstances(Set<Instance> newInstances) {
        clusterMachineIpMap.clear();
        // 遍历newInstances，更新LinkId2MachineIpMap
        for (Instance instance : newInstances) {
            clusterMachineIpMap.put(Integer.valueOf(instance.getMetadata().get(NacosRegisterConfig.MACHINE_ID_KEY)), instance.getIp());
        }
    }

    public static String getClusterMachineIp(int machineId) {
        return clusterMachineIpMap.get(machineId);
    }

    public static void addLinkId2Channel(int machineId, ChannelHandlerContext channel) {
        LinkId2ChannelMap.put(machineId, channel);
    }

    public static void removeLinkId2Channel(int machineId) {
        LinkId2ChannelMap.remove(machineId);
    }

    public static ChannelHandlerContext getLinkId2Channel(int machineId) {
        return LinkId2ChannelMap.get(machineId);
    }
}
