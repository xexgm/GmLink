package com.gm.link.core.cache;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.gm.link.core.config.NacosRegisterConfig;
import io.netty.channel.Channel;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: xexgm
 * description: 维护两类数据
 *              1.gm-Link集群信息，机器id -> 机器ip，由注册中心模块维护
 *              2.当前机器与其他机器建立的channel，用于下行消息的转发， 由当前机器维护 todo 如果channel断开了，要清理map
 */
public class LinkClusterManager {

    // targetMachineId -> targetMachineIp
    private static final ConcurrentHashMap<Integer, String> clusterMachineIpMap = new ConcurrentHashMap<>();

    // 当前机器 与 目标机器 建立的channel targetMachineId -> channel
    @Deprecated
    private static final ConcurrentHashMap<Integer, Channel> LinkId2ChannelMap = new ConcurrentHashMap<>();

    // nacos 监听器触发
    public static void updateClusterInstances(Set<Instance> newInstances) {
        // 先清理，再添加
        clusterMachineIpMap.clear();
        // 遍历newInstances，更新LinkId2MachineIpMap
        for (Instance instance : newInstances) {
            clusterMachineIpMap.put(Integer.valueOf(instance.getMetadata().get(NacosRegisterConfig.MACHINE_ID_KEY)), instance.getIp());
        }
    }

    public static String getClusterMachineIp(int machineId) {
        return clusterMachineIpMap.get(machineId);
    }

    @Deprecated
    public static void addLinkId2Channel(int machineId, Channel channel) {
        LinkId2ChannelMap.put(machineId, channel);
    }

    @Deprecated
    public static void removeLinkId2Channel(int machineId) {
        LinkId2ChannelMap.remove(machineId);
    }

    @Deprecated
    public static Channel getLinkId2Channel(int machineId) {
        return LinkId2ChannelMap.get(machineId);
    }
}
