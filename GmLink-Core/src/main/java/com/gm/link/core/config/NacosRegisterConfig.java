package com.gm.link.core.config;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.gm.link.common.utils.NetUtil;

import java.util.Properties;

/**
 * @Author: xexgm
 */
public class NacosRegisterConfig {
    


    /** Nacos地址 host:port **/
    public static final String SERVERADDR = "192.168.222.142" + ":" + "8848";

    /** nacos默认命名空间，为空，代表 Public **/
    public static final String DEFAULT_NAMESPACE = "";

    /** nacos 默认 Group **/
    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    /** 集群分片 **/
    public static final String CLUSTER_NAME = "GM_LINK";

    /** nacos 默认超时时长 **/
    public static final int DEFAULT_TIMEOUT = 3000;

    public static final Properties PROPERTIES = new Properties() {
        {
            put(PropertyKeyConst.SERVER_ADDR, SERVERADDR);
            put(PropertyKeyConst.NAMESPACE, DEFAULT_NAMESPACE);
            // grpc 配置
//            put("nacos.remote.client.grpc.port", 9848);
            put("nacos.remote.client.grpc.timeout", 10000);
        }
    };
//
//    static {
//        PROPERTIES.put(PropertyKeyConst.SERVER_ADDR, SERVERADDR);
//        PROPERTIES.put(PropertyKeyConst.NAMESPACE, DEFAULT_NAMESPACE);
//        PROPERTIES.put(PropertyKeyConst.CLUSTER_NAME, CLUSTER_NAME);
//    }

    public static final String MACHINE_ID_KEY = "machine_id";
}
