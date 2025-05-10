package com.gm.link.core.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: hhj023
 * @Date: 2025/4/30
 * @Description: 所有用户id与机器id的映射 Long -> Integer
 */
// Abandoned 不作使用，不能保证 实时+可靠
// kafka分区广播，建立连接，断开连接，都需要维护
@Deprecated
public class UserMachineMap {

    private static final ConcurrentHashMap<Long, Integer> user2MachineMap = new ConcurrentHashMap<>();

    // 几个操作：put remove get
    public static void put(Long userId, Integer machineId) {
        user2MachineMap.put(userId, machineId);
    }

    public static void remove(Long userId) {
        user2MachineMap.remove(userId);
    }

    public static Integer getUser2MachineId(Long userId) {
        return user2MachineMap.get(userId);
    }
}
