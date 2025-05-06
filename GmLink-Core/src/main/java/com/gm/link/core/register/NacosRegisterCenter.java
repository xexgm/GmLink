package com.gm.link.core.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.gm.link.core.config.LinkConfig;
import com.gm.link.core.config.NacosRegisterConfig;
import com.gm.link.core.config.NettyConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: xexgm
 */
@Slf4j
public class NacosRegisterCenter implements RegisterCenterProcessor {

    /**
     * 用于维护服务实例信息
     **/
    private NamingService namingService;

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @SneakyThrows(Exception.class)
    @Override
    public void init() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }

        // 注册中心配置
        namingService = NamingFactory.createNamingService(NacosRegisterConfig.PROPERTIES);

        // 将 Link机器 注册到注册中心
        Instance instance = new Instance();
        // Link服务ip
        instance.setIp(LinkConfig.LINK_HOST);
        // netty监听端口作为主端口
        instance.setPort(NettyConfig.port);

        // 关键元数据配置
        Map<String, String> metadata = new HashMap<>();
        metadata.put("netty_port", "9999");
        metadata.put("grpc_port", "10001");
        metadata.put(NacosRegisterConfig.MACHINE_ID_KEY, LinkConfig.MACHINE_ID + "");
        metadata.put("service_type", "composite"); // 标记复合型服务

        namingService.registerInstance(LinkConfig.SERVICENAME, NacosRegisterConfig.DEFAULT_GROUP, instance);
        log.info("[registerLinkMachine] instance: {}", instance);

        /**
         * 服务发现使用                                               "gm-link"
         * List<Instance> instances = namingService.getAllInstances(LinkConfig.SERVICENAME);
         */


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                namingService.deregisterInstance(LinkConfig.SERVICENAME, NacosRegisterConfig.DEFAULT_GROUP, instance);
            } catch (NacosException e) {
                log.info("[shutdownRegister] error: {}", e.getMessage());
            }
        }));
        /**
         * 优雅下线处理
         * Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         *     namingService.deregisterInstance(LinkConfig.SERVICENAME, NacosRegisterConfig.DEFAULT_GROUP, instance);
         * }))
         */
    }

    // nacos监听器 -> gm-link 实例发生变化，更新本地机器的实例缓存，key: 机器id value: 机器ip
    // 新增内部机器连接管理者，存储 link集群机器信息，并且保存当前机器与其他机器的长连接
    @SneakyThrows(Exception.class)
    @Override
    public void subscribeServiceChange(RegisterCenterListener listener) {
        namingService.subscribe(LinkConfig.SERVICENAME, NacosRegisterConfig.DEFAULT_GROUP, event -> {
            if (event instanceof NamingEvent) {
                // 触发监听器
                log.info("[registerOnEvent] event: {}", event);

                String serviceName = ((NamingEvent) event).getServiceName();
                if (!LinkConfig.SERVICENAME.equals(serviceName)) {
                    // 不是当前服务实例的变化
                    return;
                }
                try {
                    List<Instance> currentInstances = namingService.getAllInstances(LinkConfig.SERVICENAME, NacosRegisterConfig.DEFAULT_GROUP);
                    Set<Instance> newInstances = new HashSet<>();
                    if (CollectionUtils.isNotEmpty(currentInstances)) {
                        for (Instance instance : currentInstances) {
                            if (instance == null) continue;
                            newInstances.add(instance);
                        }
                    }
                    // 调用我们自定义的 listener
                    listener.onInstancesChange(newInstances);
                } catch (NacosException e) {
                    log.info("[registerOnEvent] error: {}", e.getMessage());
                }
            }
        });
    }
}
