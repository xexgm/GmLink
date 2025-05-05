package com.gm.link.core.map;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: hhj023
 * @Date: 2025/4/30
 * @Description: 所有用户id与机器id的映射 Long -> Integer
 */
// todo kafka分区广播，建立连接，断开连接，都需要维护
public class UserMachineMap {

    public static ConcurrentHashMap<Long, Integer> user2MachineMap = new ConcurrentHashMap<>();

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
