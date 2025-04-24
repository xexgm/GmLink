package com.gm.link.core.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import static com.gm.link.core.config.RedisConfig.*;

/**
 * @Author: xexgm
 */
public class MachineIdGenerator {

    private static final JedisPool jedisPool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        // 连接池大小
        config.setMaxTotal(1);
        jedisPool = new JedisPool(config, REDIS_HOST, REDIS_PORT);
        // 尝试初始化 key
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setnx(MACHINE_ID_KEY, "0");
        }
    }

    public static int getMachineId() {
        try (Jedis jedis = jedisPool.getResource()) {
            Long id = jedis.incr(MACHINE_ID_KEY);
            return id.intValue();
        }
    }
}
