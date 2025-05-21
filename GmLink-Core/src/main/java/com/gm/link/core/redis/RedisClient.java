package com.gm.link.core.redis;

import redis.clients.jedis.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.gm.link.core.config.RedisConfig.*;

/**
 * @Author: xexgm
 */
public class RedisClient {

    private static final JedisPool jedisPool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        // 连接池大小 预期QPS * 平均响应时间
        // 1条消息1000个人，就要查1000次，10个群，就是10000次
        config.setMaxTotal(500); // 最大连接数
        config.setMaxIdle(250); // 空闲连接数保持
        config.setMinIdle(50);  // 最小空闲连接数
        config.setMaxWait(Duration.ofMillis(500)); // 获取连接时的最大等待时间
        config.setTestOnBorrow(true); // 借用连接时进行有效性检查
        jedisPool = new JedisPool(config, REDIS_HOST, REDIS_PORT, 2000, "123456");
        // 尝试初始化 key
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setnx(MACHINE_ID_KEY, "0");
        }
    }

    public static Integer generateMachineId() {
        try (Jedis jedis = jedisPool.getResource()) {
            Long id = jedis.incr(MACHINE_ID_KEY);
            return id.intValue();
        }
    }

    // 单个接收者查询对应机器
    public static Integer getMachineId(Long userId) {
        String userKey = PREFIX_USER_ID  + userId;
        try (Jedis jedis = jedisPool.getResource()) {
            String machineId = jedis.get(userKey);
            if (machineId == null) {
                return null;
            }
            return Integer.parseInt(machineId);
        }
    }

    // 批量接收者查询对应机器
    public static List<Integer> batchGetMachineId(List<Long> userIds) {
        String[] keys = userIds.stream().map(userId -> PREFIX_USER_ID + userId).toArray(String[]::new);
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipeLine = jedis.pipelined();
            List<Response<String>> responses = new ArrayList<>(1000);

            for (String userKey : keys) {
                responses.add(pipeLine.get(userKey));
            }
            pipeLine.sync();

            return responses.stream()
                    .map(Response::get)
                    .map(Integer::parseInt)
                    .toList();
        }
    }
}
