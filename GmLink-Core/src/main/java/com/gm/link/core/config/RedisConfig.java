package com.gm.link.core.config;

/**
 * @Author: xexgm
 */
public class RedisConfig {

    /** redis key 过期时间 **/
    public static String KEY_EXPIRE_TIME = "300";

    /** reids setnx 操作**/
    public static String OP_SETNX = "SETNX";

    /** redis expire 续期操作 **/
    public static String OP_EXPIRE = "EXPIRE";

    /** redis userId key 前缀 **/
    public static String PREFIX_USER_ID = "user:";

    /** redis 获取机器id 地址 **/
    public static String REDIS_HOST = "";

    /** redis 获取机器id 端口 **/
    public static Integer REDIS_PORT;

    /** redis 机器id 前缀**/
    public static String MACHINE_ID_KEY = "machine:id:generator";
}
